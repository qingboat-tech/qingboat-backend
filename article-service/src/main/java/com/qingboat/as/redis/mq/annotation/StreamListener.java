package com.qingboat.as.redis.mq.annotation;

import java.lang.annotation.*;

/**
 * 延迟队列
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface StreamListener {

    /**
     * 监听主题
     *
     * @return
     */
    String value() default "delay:list:default";
}