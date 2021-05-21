package com.qingboat.as.redis.mq;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息体
 *
 * @author david
 * @date 2021-05-21
 */
@Data
public class RedisMessage<T>  implements Serializable {

    /**
     * 消息唯一标识
     */
    private String id;
    /**
     * 消息主题
     */
    private String topic = "default";
    /**
     * 具体消息
     */
    private T body;
    /**
     * 延时时间, 格式为时间戳: 当前时间戳 + 实际延迟毫秒数
     */
    private Long delayTime = System.currentTimeMillis() + 10000L;  // 30秒
    /**
     * 消息发送时间
     */
    private LocalDateTime createTime;

    public RedisMessage(T t){
        this();
        this.body = t;
    }

    public RedisMessage(){
        this.id = "MSG_ID:"+ UUID.randomUUID().toString();
    }
}