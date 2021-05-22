package com.qingboat.us.task;

import com.alibaba.fastjson.JSON;
import com.qingboat.us.redis.mq.RedisMessage;
import com.qingboat.us.redis.mq.annotation.StreamListener;
import com.qingboat.us.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ApplyCreatorRedisTask {

    @Autowired
    private UserService userService;


    @StreamListener
    public void processTask(RedisMessage message) {
        if ("TOPIC:applyCreator".equals(message.getTopic())){
            Long userId = (Long) message.getBody();
            userService.applyCreator(userId);
        }
        log.info("processTask，消息处理监听器, msg: {}", JSON.toJSONString(message));
    }


}
