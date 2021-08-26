package com.qingboat.us.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class MessageController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/asdf123/{temp}")
    public String test(@PathVariable("temp")String temp){
//        RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
//            @Override
//            public void confirm(CorrelationData correlationData, boolean b, String s) {
//                System.out.println(correlationData.getReturnedMessage());
//                System.out.println("=======");
//                System.out.println(s);
//                System.out.println("=======");
//            }
//        };
//        rabbitTemplate.setConfirmCallback(confirmCallback);
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setMessageId("1");
//        Message message = new Message(temp.getBytes(StandardCharsets.UTF_8),messageProperties);

//        rabbitTemplate.send("MSGTopic","push.*.msg",message);
//        System.out.println(System.currentTimeMillis());
//        rabbitTemplate.convertAndSend("push.message","",temp);
        return temp;
    }
}
