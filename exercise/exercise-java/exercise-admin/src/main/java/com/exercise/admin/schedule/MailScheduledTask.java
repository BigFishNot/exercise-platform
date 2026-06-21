package com.exercise.admin.schedule;

import com.exercise.campus.service.MailScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 邮件调度入口（admin 端）
 * 每分钟 tick 一次, 由 MailScheduleService 判定是否到时间点 + 扫描推送
 * 需要在 ExerciseAdminApplication 加 @EnableScheduling
 */
@Slf4j
@Component
public class MailScheduledTask {

    @Autowired
    private MailScheduleService mailScheduleService;

    /** 每分钟第 0 秒执行 */
    @Scheduled(cron = "0 * * * * *")
    public void tick() {
        try {
            mailScheduleService.tick();
        } catch (Exception e) {
            log.error("[mail:scheduledTask] tick failed", e);
        }
    }
}