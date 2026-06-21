package com.exercise.campus.service;

import com.exercise.campus.entity.po.MailLog;
import com.exercise.campus.entity.query.MailLogQuery;
import com.exercise.campus.entity.vo.MailLogVO;
import com.exercise.campus.vo.PageResultVO;

public interface MailLogService {

    PageResultVO<MailLogVO> pageList(MailLogQuery query);

    /** 内部：写日志 */
    void log(MailLog po);

    /** 当日 (userId, templateType) 是否已发过 */
    boolean isSentToday(String userId, Integer templateType);
}