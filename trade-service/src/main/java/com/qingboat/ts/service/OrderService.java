package com.qingboat.ts.service;

import com.qingboat.ts.entity.OrderEntity;

public interface OrderService {

    OrderEntity createOrder(Long goodsId, Long skuId,Long userId);
}
