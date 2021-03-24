package com.qingboat.ts.service.impl;

import com.qingboat.ts.dao.OrderDao;
import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;


    @Override
    public OrderEntity createOrder(Long goodsId, Long skuId ,Long userId) {
        log.info( "   ====OrderServiceImpl.createOrder======  ");
        return null;
    }
}
