package com.qingboat.us.controller;

import com.qingboat.us.redis.RedisUtil;
import com.qingboat.us.utils.handler.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
public class SendEmailController {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    RedisUtil redisUtil;


    public void sendTest(){
        SimpleMailMessage message = new SimpleMailMessage();
        // 发件人
        message.setFrom("hello@qingboat.com");
        // 收件人
        message.setTo("542842297@qq.com");
        // 邮件标题
        message.setSubject("Java发送邮件test");
        // 邮件内容
        message.setText("你好，这是一条用于测试Spring Boot邮件发送功能的邮件！哈哈哈~~~");
        // 抄送人
//        message.setCc("xxx@qq.com");
        mailSender.send(message);
    }
    public void send(String email,String code){

        SimpleMailMessage message = new SimpleMailMessage();
        // 发件人
        message.setFrom("hello@qingboat.com");
        // 收件人
        message.setTo(email);
        // 邮件标题
        message.setSubject("（氢舟）绑定邮箱");
        // 邮件内容
        message.setText("你好，您的邮箱绑定验证码为：" + code);
        // 抄送人
//        message.setCc("xxx@qq.com");
        mailSender.send(message);
    }

}
