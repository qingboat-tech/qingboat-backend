package com.qingboat.us.redis.mq;

import com.alibaba.fastjson.JSON;
import com.qingboat.us.redis.RedisUtil;
import com.qingboat.us.redis.mq.annotation.StreamListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class RedisQueue {

    private static final String QUEUE_NAME = "redis_queue";
    private Map<Object, Method> map = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisUtil redisUtil;

    private ExecutorService executors = Executors.newFixedThreadPool(5);


    public void push(RedisMessage msg){

        if (msg.getDelayTime() == null){
            msg.setDelayTime(System.currentTimeMillis());
        }

        redisUtil.zAdd(QUEUE_NAME, msg.getId(), msg.getDelayTime());
        redisUtil.set(msg.getId(), JSON.toJSONString(msg));

        //释放锁
    }

    @Scheduled(cron = "0/2 * * * * ?") //每2秒执行一次
    public void scheduledTaskByCorn() {
        try{
            Set<Object> rst = redisUtil.zRangeByScore(QUEUE_NAME,0,System.currentTimeMillis());
            if (rst !=null && !rst.isEmpty()){
                //获取锁
                boolean lockFlag = redisUtil.lock(QUEUE_NAME);
                if (!lockFlag){
                    return;
                }
                String[] todoMsgIds = new String[rst.size()];
                int index = 0;

                log.info(" 消息监听器: 等待执行任务数：" + rst.size());
                Map<Object, Method> map = getBean(StreamListener.class);
                Iterator ite = rst.iterator();
                while (ite.hasNext()){
                    String msgId  = (String) ite.next();
                    todoMsgIds[index++] = msgId;
                    Object v = redisUtil.get(msgId);
                    if (v ==null){
                        continue;
                    }
                    for (Map.Entry<Object, Method> entry : map.entrySet()) {
                        RedisMessage msg = JSON.parseObject(String.valueOf(v), RedisMessage.class);
                        executors.submit(() -> {
                            try {
                                entry.getValue().invoke(entry.getKey(), msg);
                            } catch (Throwable t) {
                                // 失败重新放入失败队列
                                log.error("消息监听器发送异常: ", t);
                            }
                        });
                        log.info("监听器-监听到到期消息: {}", JSON.toJSONString(msg));
                    }
                    boolean deleteRst = redisUtil.deleteKey(msgId);
                    if (!deleteRst){
                        log.error("消息监听器 msgId删除异常: msgId= ", msgId);
                    }
                }
                long delRst = redisUtil.zRemove(QUEUE_NAME,todoMsgIds);
                if (delRst != delRst){
                    log.error("消息监听器 zSet删除异常: msgId= ", todoMsgIds);
                }
            }
        }finally {
            redisUtil.unLock(QUEUE_NAME);
        }
    }



    private Map<Object, Method> getBean(Class<? extends Annotation> annotationClass) {
        if (!this.map.isEmpty()) {
            return this.map;
        }
        Map<Object, Method> map = new HashMap<>();
        String[] beans = applicationContext.getBeanDefinitionNames();
        for (String beanName : beans) {
            Class<?> clazz = applicationContext.getType(beanName);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                boolean present = method.isAnnotationPresent(annotationClass);
                if (present) {
                    map.put(applicationContext.getBean(beanName), method);
                }
            }
        }
        this.map = map;
        return map;
    }


}
