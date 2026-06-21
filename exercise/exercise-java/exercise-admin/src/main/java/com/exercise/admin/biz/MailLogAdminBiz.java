package com.exercise.admin.biz;

import com.exercise.campus.entity.query.MailLogQuery;
import com.exercise.campus.entity.vo.MailLogVO;
import com.exercise.campus.service.MailLogService;
import com.exercise.campus.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailLogAdminBiz {

    @Autowired
    private MailLogService mailLogService;

    public PageResultVO<MailLogVO> loadDataList(MailLogQuery query) {
        return mailLogService.pageList(query);
    }
}