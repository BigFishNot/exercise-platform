package com.exercise.campus.service.impl;

import com.exercise.campus.component.MailSender;
import com.exercise.campus.entity.dto.MailSendRequestDTO;
import com.exercise.campus.entity.po.MailConfig;
import com.exercise.campus.entity.po.MailLog;
import com.exercise.campus.entity.po.MailTemplate;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.vo.MailSendResultVO;
import com.exercise.campus.enums.MailSendStatusEnum;
import com.exercise.campus.enums.MailTriggerTypeEnum;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.service.MailConfigService;
import com.exercise.campus.service.MailLogService;
import com.exercise.campus.service.MailSendService;
import com.exercise.campus.service.MailTemplateService;
import com.exercise.campus.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件发送 Service 实现
 * - 管理员手动：选模板 + 用户列表, 跳过今日已发的, 失败写日志
 * - 渲染占位符：{xxx}
 */
@Slf4j
@Service
public class MailSendServiceImpl implements MailSendService {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([a-zA-Z]+)\\}");
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired private MailConfigService mailConfigService;
    @Autowired private MailTemplateService mailTemplateService;
    @Autowired private MailLogService mailLogService;
    @Autowired private MailSender mailSender;
    @Autowired private UserInfoService userInfoService;

    @Override
    public MailSendResultVO sendManual(String operatorId, MailSendRequestDTO dto) {
        // 1. 配置 + 模板前置校验
        MailConfig config = mailConfigService.loadPO();
        if (config == null) {
            throw new BusinessException(ResponseCodeEnum.MAIL_CONFIG_NOT_FOUND);
        }
        if (config.getEnabled() == null || config.getEnabled() != 1) {
            throw new BusinessException(ResponseCodeEnum.MAIL_CONFIG_DISABLED);
        }
        MailTemplate template = mailTemplateService.loadById(dto.getTemplateId());
        if (template == null) {
            throw new BusinessException(ResponseCodeEnum.MAIL_TEMPLATE_NOT_FOUND);
        }

        // 2. 逐个用户发送
        MailSendResultVO result = new MailSendResultVO();
        result.setSkipped(new ArrayList<>());
        result.setFailed(new ArrayList<>());

        int sent = 0, skipped = 0, failed = 0;
        List<String> userIds = dto.getUserIds() == null ? Collections.emptyList() : dto.getUserIds();
        for (String userId : userIds) {
            UserInfo user = userInfoService.getById(userId);
            if (user == null) {
                continue;
            }
            // 邮箱缺失 -> 跳过
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                addSkipped(result, user, "未设置邮箱");
                skipped++;
                continue;
            }
            // 今日已发 -> 跳过
            if (mailLogService.isSentToday(userId, template.getTemplateType())) {
                addSkipped(result, user, "今日已发送");
                skipped++;
                continue;
            }

            // 渲染 + 发送
            Map<String, String> vars = buildContextVars(user, template);
            String[] rendered = render(template, vars);

            MailLog logPo = new MailLog();
            logPo.setUserId(userId);
            logPo.setUserEmail(user.getEmail());
            logPo.setTemplateId(template.getTemplateId());
            logPo.setTemplateType(template.getTemplateType());
            logPo.setTriggerType(MailTriggerTypeEnum.MANUAL.getType());
            logPo.setOperatorId(operatorId);
            logPo.setRenderedTitle(rendered[0]);

            boolean ok = false;
            String err = null;
            try {
                ok = mailSender.send(config, user.getEmail(), rendered[0], rendered[1]);
            } catch (Exception e) {
                err = e.getMessage();
                log.warn("[mail:sendManual] send failed to={} err={}", user.getEmail(), err);
            }

            if (ok) {
                logPo.setStatus(MailSendStatusEnum.SUCCESS.getStatus());
                sent++;
            } else {
                logPo.setStatus(MailSendStatusEnum.FAILED.getStatus());
                logPo.setErrorSummary(err == null ? "发送失败" : truncate(err, 500));
                addFailed(result, user, logPo.getErrorSummary());
                failed++;
            }
            try {
                mailLogService.log(logPo);
            } catch (Exception ignore) {
                // 日志写失败不影响主流程
            }
        }
        result.setSentCount(sent);
        result.setSkippedCount(skipped);
        result.setFailedCount(failed);
        return result;
    }

    @Override
    public String[] render(MailTemplate template, Map<String, String> vars) {
        return new String[] {
                replace(template.getTitle(), vars),
                replace(template.getContent(), vars)
        };
    }

    /** 兼容旧接口：dto 传 templateId 也允许；用 mapper 查类型 */
    @SuppressWarnings("unused")
    private Integer resolveTemplateType(String templateId) {
        return Integer.valueOf(templateId);
    }

    private String replace(String s, Map<String, String> vars) {
        if (s == null) return "";
        Matcher m = PLACEHOLDER.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String v = vars.getOrDefault(key, "");
            m.appendReplacement(sb, Matcher.quoteReplacement(v));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private Map<String, String> buildContextVars(UserInfo user, MailTemplate template) {
        Map<String, String> vars = new HashMap<>();
        vars.put("nickName", nullSafe(user.getNickName()));
        vars.put("account", nullSafe(user.getAccount()));
        vars.put("email", nullSafe(user.getEmail()));
        vars.put("date", DATE_FMT.format(new Date()));
        // 占位（接 checkIn 后回填）
        vars.put("actualMinutes", "0");
        vars.put("targetMinutes", "0");
        vars.put("planStartDate", "");
        vars.put("planEndDate", "");
        return vars;
    }

    private String nullSafe(String s) { return s == null ? "" : s; }

    private String truncate(String s, int n) {
        if (s == null) return null;
        return s.length() > n ? s.substring(0, n) : s;
    }

    private void addSkipped(MailSendResultVO r, UserInfo u, String reason) {
        MailSendResultVO.SkippedUser s = new MailSendResultVO.SkippedUser();
        s.setUserId(u.getUserId());
        s.setAccount(u.getAccount());
        s.setReason(reason);
        r.getSkipped().add(s);
    }

    private void addFailed(MailSendResultVO r, UserInfo u, String err) {
        MailSendResultVO.FailedUser f = new MailSendResultVO.FailedUser();
        f.setUserId(u.getUserId());
        f.setAccount(u.getAccount());
        f.setError(err);
        r.getFailed().add(f);
    }
}