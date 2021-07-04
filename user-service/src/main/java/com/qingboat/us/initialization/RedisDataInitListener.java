package com.qingboat.us.initialization;

import com.alibaba.fastjson.JSON;
import com.qingboat.us.dao.UserProfileDao;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.redis.RedisUtil;

import com.qingboat.us.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RedisDataInitListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProfileDao userProfileDao;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getSource() instanceof org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext){
            //初始化 热点创作者集合   此操作会导致 redis中数据会少于mysql中的数据（在操作期间有的新的人发生订阅或购买），但是大体上不会影响 热点榜的人
            List<Integer> creatorIds = userProfileDao.getCreatorIds();
            for (Integer creatorId:creatorIds) {
                Integer count_userIdsByCreatorOnNewslettersAndPathway = userService.getCount_UserIdsByCreatorOnNewslettersAndPathway(creatorId);
                UserProfileEntity userProfile = userService.getUserProfile((long) creatorId);
                String s = JSON.toJSONString(userProfile);
                redisUtil.zAdd_string("hotCreator",s,count_userIdsByCreatorOnNewslettersAndPathway);
            }
        }
//        RedisUtil redisUtil = applicationContext.getBean("redisUtil", RedisUtil.class);
//            System.out.println(redisUtil.zAdd_string("hot-test","id-1",100));
//            System.out.println(redisUtil.zAdd_string("hot-test", "id-2", 10));
//            System.out.println(redisUtil.zAdd_string("hot-test", "id-3", 110));
//            Set<String> objects = redisUtil.zRange_string("hot-test", 0, 1000);
//            Iterator<String> iterator = objects.iterator();
//            while (iterator.hasNext()){
//                System.out.println(iterator.next());
//            }
//        System.out.println("redis数据初始化完毕");

    }
}
