package com.qingboat.ts.controller;

import com.qingboat.base.exception.BaseException;
import com.qingboat.ts.entity.AuthToken;
import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.entity.GoodsSkuEntity;
import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.GoodsService;
import com.qingboat.ts.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/createOrder")
    @ResponseBody
    public OrderEntity createOrder(@RequestBody Map<String,Long> param , @RequestAttribute(name = "UID") Long uid){
        Long goodsId = param.get("goodsId");
        Long skuId = param.get("skuId");

        log.info(" RequestParam: goodsId=" +goodsId +" ;skuId="+skuId );

        return orderService.createOrder(goodsId,skuId, uid);
    }




}
