package com.exercise.campus.service.impl;

import com.exercise.campus.converter.MailConverter;
import com.exercise.campus.entity.po.MailLog;
import com.exercise.campus.entity.query.MailLogQuery;
import com.exercise.campus.entity.vo.MailLogVO;
import com.exercise.campus.service.MailLogService;
import com.exercise.campus.utils.DateUtils;
import com.exercise.campus.vo.PageResultVO;
import com.exercise.mappers.MailLogMapper;
import com.exercise.mappers.UserInfoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailLogServiceImpl implements MailLogService {

    @Autowired
    private MailLogMapper mailLogMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public PageResultVO<MailLogVO> pageList(MailLogQuery query) {
        Integer total = mailLogMapper.countByQuery(query);
        if (total == null || total == 0) return PageResultVO.empty();
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<MailLog> list = mailLogMapper.selectList(query);
        return PageResultVO.of(new PageInfo<>(list.stream()
                .map(MailConverter::toLogVO).collect(Collectors.toList())));
    }

    @Override
    public void log(MailLog po) {
        if (po.getSendDate() == null) po.setSendDate(DateUtils.todayGMT8());
        mailLogMapper.insert(po);
    }

    @Override
    public boolean isSentToday(String userId, Integer templateType) {
        Integer cnt = mailLogMapper.countTodaySuccess(userId, templateType, DateUtils.todayGMT8());
        return cnt != null && cnt > 0;
    }
}