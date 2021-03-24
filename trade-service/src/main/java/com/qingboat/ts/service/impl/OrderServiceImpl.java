package com.qingboat.ts.service.impl;

import com.qingboat.base.utils.DateUtil;
import com.qingboat.ts.dao.OrderDao;
import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.OrderService;
import com.qingboat.ts.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public OrderEntity createOrder(Long goodsId, Long skuId ,Long userId) {
        log.info( "   ====OrderServiceImpl.createOrder======  ");
        return null;
    }


    private String generateOrderId(){
        String today = DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYYMMDD);
        String key = "INCR_ORDER_"+today;

        Long value = redisUtil.increment(key,1);
        if (value ==1){
            redisUtil.expire(key,60*60*24);
        }
        String now = DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYYMMDDHHmm);
        return new StringBuilder(now).append(value).toString();
    }


}
