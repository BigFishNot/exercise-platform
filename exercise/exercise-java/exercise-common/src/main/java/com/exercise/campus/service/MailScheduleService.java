package com.exercise.campus.service;

import com.exercise.campus.entity.po.MailConfig;
import com.exercise.campus.entity.po.MailLog;
import com.exercise.campus.entity.po.MailTemplate;
import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.enums.CheckInStatusEnum;
import com.exercise.campus.enums.MailSendStatusEnum;
import com.exercise.campus.enums.MailTemplateTypeEnum;
import com.exercise.campus.enums.MailTriggerTypeEnum;
import com.exercise.campus.service.ExerciseCheckInService;
import com.exercise.campus.service.ExercisePlanService;
import com.exercise.campus.service.MailConfigService;
import com.exercise.campus.service.MailLogService;
import com.exercise.campus.component.MailSender;
import com.exercise.campus.service.UserInfoService;
import com.exercise.campus.utils.DateUtils;
import com.exercise.campus.utils.MailTimeUtils;
import com.exercise.mappers.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮件调度 Service
 * 由 MailScheduledTask (admin 模块) 每分钟触发 tick()
 * 单次执行会扫描所有启用用户, 按当前时间点匹配 send_time_points,
 * 对未打卡/时长不足的用户发对应模板邮件
 */
@Slf4j
@Service
public class MailScheduleService {

    @Autowired private MailConfigService mailConfigService;
    @Autowired private MailTemplateService mailTemplateService;
    @Autowired private MailLogService mailLogService;
    @Autowired private MailSender mailSender;
    @Autowired private UserInfoService userInfoService;
    @Autowired private ExerciseCheckInService exerciseCheckInService;
    @Autowired private ExercisePlanService exercisePlanService;
    @Autowired private UserInfoMapper userInfoMapper;

    private static final SimpleDateFormat HHMM = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    static { HHMM.setTimeZone(DateUtils.GMT8); DATE_FMT.setTimeZone(DateUtils.GMT8); }

    /** 上次执行过的时间点（避免同分钟内重复触发） */
    private volatile String lastTriggeredTime = "";

    /**
     * 每分钟 tick: 判断当前时间是否在 send_time_points 中, 若是则执行一轮扫描
     */
    public void tick() {
        MailConfig config = mailConfigService.loadPO();
        if (config == null || config.getEnabled() == null || config.getEnabled() != 1) {
            return;
        }
        String[] points = MailTimeUtils.parse(config.getSendTimePoints());
        if (points.length == 0) {
            return;
        }
        String now = HHMM.format(new Date());
        // 同一分钟只触发一次（防止 cron 抖动）
        if (now.equals(lastTriggeredTime)) {
            return;
        }
        boolean matched = false;
        for (String p : points) {
            if (now.equals(p)) { matched = true; break; }
        }
        if (!matched) {
            return;
        }
        lastTriggeredTime = now;
        log.info("[mail:scheduled] time point hit: {}, start scan", now);
        try {
            int sent = scanAndSend(config);
            log.info("[mail:scheduled] done, sent {}", sent);
        } catch (Exception e) {
            log.error("[mail:scheduled] scan failed", e);
        }
    }

    private int scanAndSend(MailConfig config) {
        MailTemplate notDoneTpl = mailTemplateService.loadByType(MailTemplateTypeEnum.NOT_DONE.getType());
        MailTemplate insufTpl = mailTemplateService.loadByType(MailTemplateTypeEnum.INSUFFICIENT.getType());
        if (notDoneTpl == null && insufTpl == null) {
            log.warn("[mail:scheduled] no templates configured");
            return 0;
        }
        List<UserInfo> users = userInfoMapper.selectByStatus(1, 5000);
        int sent = 0;
        for (UserInfo user : users) {
            if (user.getEmail() == null || user.getEmail().isEmpty()) continue;
            try {
                if (sendOne(config, user, notDoneTpl, insufTpl)) {
                    sent++;
                }
            } catch (Exception e) {
                log.warn("[mail:scheduled] send to {} failed: {}", user.getAccount(), e.getMessage());
            }
        }
        return sent;
    }

    /**
     * 给一个用户发一封邮件
     * @return true=实际发了一封
     */
    private boolean sendOne(MailConfig config, UserInfo user,
                            MailTemplate notDoneTpl, MailTemplate insufTpl) {
        // 1) 今日打卡状态
        com.exercise.campus.entity.vo.CheckInTodayVO ck = exerciseCheckInService.getToday(user.getUserId());
        if (ck == null) return false;
        Integer status = ck.getStatus();
        // 2) 选模板
        MailTemplate tpl;
        if (status != null && status == CheckInStatusEnum.NOT_DONE.getStatus()) {
            tpl = notDoneTpl;
        } else if (status != null && status == CheckInStatusEnum.INSUFFICIENT.getStatus()) {
            tpl = insufTpl;
        } else {
            return false;  // 已打卡, 不发
        }
        if (tpl == null || tpl.getEnabled() == null || tpl.getEnabled() != 1) {
            return false;
        }
        // 3) 去重: 今日已发过该类型 -> 跳过
        if (mailLogService.isSentToday(user.getUserId(), tpl.getTemplateType())) {
            return false;
        }
        // 4) 渲染 + 发
        Map<String, String> vars = buildContextVars(user, ck);
        String[] rendered = render(tpl, vars);
        MailLog logPo = new MailLog();
        logPo.setUserId(user.getUserId());
        logPo.setUserEmail(user.getEmail());
        logPo.setTemplateId(tpl.getTemplateId());
        logPo.setTemplateType(tpl.getTemplateType());
        logPo.setTriggerType(MailTriggerTypeEnum.SCHEDULED.getType());
        logPo.setOperatorId(null);
        logPo.setRenderedTitle(rendered[0]);

        boolean ok = false; String err = null;
        try {
            ok = mailSender.send(config, user.getEmail(), rendered[0], rendered[1]);
        } catch (Exception e) {
            err = e.getMessage();
        }
        if (ok) {
            logPo.setStatus(MailSendStatusEnum.SUCCESS.getStatus());
        } else {
            logPo.setStatus(MailSendStatusEnum.FAILED.getStatus());
            logPo.setErrorSummary(err == null ? "发送失败" : truncate(err, 500));
        }
        try { mailLogService.log(logPo); } catch (Exception ignore) {}
        return ok;
    }

    /* 占位符渲染（与 MailSendServiceImpl 同一套, 独立, 不互相依赖） */
    private Map<String, String> buildContextVars(UserInfo user, com.exercise.campus.entity.vo.CheckInTodayVO ck) {
        Map<String, String> vars = new HashMap<>();
        vars.put("nickName", user.getNickName() == null ? "" : user.getNickName());
        vars.put("account", user.getAccount() == null ? "" : user.getAccount());
        vars.put("email", user.getEmail() == null ? "" : user.getEmail());
        vars.put("date", DATE_FMT.format(new Date()));
        vars.put("actualMinutes", String.valueOf(ck.getActualMinutes() == null ? 0 : ck.getActualMinutes()));
        vars.put("targetMinutes", String.valueOf(ck.getTargetMinutes() == null ? 0 : ck.getTargetMinutes()));
        // 阶段起止日期（取当前进行中计划）
        try {
            com.exercise.campus.entity.po.ExercisePlan plan = exercisePlanService.loadOngoing(user.getUserId());
            if (plan != null) {
                vars.put("planStartDate", plan.getStartDate() == null ? ""
                        : DATE_FMT.format(plan.getStartDate()));
                vars.put("planEndDate", plan.getEndDate() == null ? ""
                        : DATE_FMT.format(plan.getEndDate()));
            } else {
                vars.put("planStartDate", "");
                vars.put("planEndDate", "");
            }
        } catch (Exception e) {
            vars.put("planStartDate", "");
            vars.put("planEndDate", "");
        }
        return vars;
    }

    private String[] render(MailTemplate tpl, Map<String, String> vars) {
        String title = replace(tpl.getTitle(), vars);
        String content = replace(tpl.getContent(), vars);
        return new String[] { title, content };
    }

    private String replace(String s, Map<String, String> vars) {
        if (s == null) return "";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\{([a-zA-Z]+)\\}").matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(vars.getOrDefault(m.group(1), "")));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String truncate(String s, int n) {
        if (s == null) return null;
        return s.length() > n ? s.substring(0, n) : s;
    }
}