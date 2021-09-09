package com.qingboat.us.controller;

import com.qingboat.base.MessageType;
import com.qingboat.base.mq.MessageTemplate;
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
        MessageProperties messageProperties = new MessageProperties();
       if (temp.equals("abc")){
//        messageProperties.setMessageId("1");
           messageProperties.setContentType(MessageType.FS_ARTICLE_APPROVED.getTypeName());
           messageProperties.setHeader("发起人","发起人 nickname");
           messageProperties.setHeader("操作人","操作人ID");
           messageProperties.setHeader("内容",temp);
       }else {
//        messageProperties.setMessageId("1");
           messageProperties.setContentType(MessageType.FS_CREATOR_APPLY.getTypeName());
           messageProperties.setHeader("发起人","FS_CREATOR_APPLY nickname");
           messageProperties.setHeader("操作人","FS_CREATOR_APPLY");
           messageProperties.setHeader("内容",temp);
       }
        Message message = new Message("".getBytes(StandardCharsets.UTF_8),messageProperties);
        rabbitTemplate.convertAndSend("push.message","",message);
//        rabbitTemplate.setConfirmCallback();
        return temp;
    }
}
