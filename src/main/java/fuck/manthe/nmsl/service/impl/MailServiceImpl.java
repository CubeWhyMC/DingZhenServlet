package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.service.MailService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    @Resource
    JavaMailSender mailSender;

    @Value("${spring.application.name}")
    String appName;

    @Override
    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordEmail(String to, String link) {
        send(to, "重置密码 - " + appName, "请点击链接来重置密码 " + link);
    }
}
