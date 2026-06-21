package com.exercise.campus.component;

import com.exercise.campus.entity.po.MailConfig;
import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 通用邮件发送器
 * 每次发送按 MailConfig 动态构造 JavaMailSenderImpl
 */
@Slf4j
@Component
public class MailSender {

    /**
     * 发送一封邮件
     * @return true=成功, false=失败（不抛业务异常，由调用方写日志）
     */
    public boolean send(MailConfig config, String toEmail, String subject, String content) {
        if (config == null) throw new BusinessException(ResponseCodeEnum.MAIL_CONFIG_NOT_FOUND);
        if (config.getEnabled() == null || config.getEnabled() != 1) {
            throw new BusinessException(ResponseCodeEnum.MAIL_CONFIG_DISABLED);
        }
        String password;
        try {
            password = EncryptUtil.decrypt(config.getSmtpPassword());
        } catch (Exception e) {
            throw new BusinessException(ResponseCodeEnum.MAIL_PASSWORD_DECRYPT_FAIL);
        }
        JavaMailSenderImpl sender = buildSender(config, password);
        try {
            MimeMessage msg = sender.createMimeMessage();
            msg.setFrom(config.getSmtpUsername());
            msg.setRecipients(MimeMessage.RecipientType.TO, toEmail);
            msg.setSubject(subject, "UTF-8");
            msg.setText(content, "UTF-8", "plain");
            sender.send(msg);
            return true;
        } catch (Exception e) {
            log.warn("[mail:send] failed to={} err={}", toEmail, e.getMessage());
            return false;
        }
    }

    private JavaMailSenderImpl buildSender(MailConfig cfg, String password) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(cfg.getSmtpHost());
        sender.setPort(cfg.getSmtpPort() == null ? 465 : cfg.getSmtpPort());
        sender.setUsername(cfg.getSmtpUsername());
        sender.setPassword(password);
        sender.setDefaultEncoding("UTF-8");

        Properties props = new Properties();
        boolean ssl = cfg.getUseSsl() == null || cfg.getUseSsl() == 1;
        if (ssl) {
            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            props.setProperty("mail.smtp.starttls.enable", "true");
        }
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.timeout", "10000");
        props.setProperty("mail.smtp.connectiontimeout", "10000");
        sender.setJavaMailProperties(props);
        return sender;
    }
}