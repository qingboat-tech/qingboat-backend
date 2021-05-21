package com.qingboat.as.redis.mq;

import com.qingboat.as.redis.mq.annotation.StreamListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestTask {

    @StreamListener
    public void handler(RedisMessage message) {
        log.info("TestTask，消息处理监听器, msgBody: {}", message.getBody());
    }
}
