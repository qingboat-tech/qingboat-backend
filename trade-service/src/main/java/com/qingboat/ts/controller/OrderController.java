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


@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/createOrder")
    @ResponseBody
    public OrderEntity createOrder(@RequestParam Long goodsId, @RequestParam Long skuId , Model model){
        log.info(" RequestParam: goodsId=" +goodsId +" ;skuId="+skuId );

        AuthToken authToken = (AuthToken) model.getAttribute("USER_AUTHONTOKEN");
        if (authToken == null){
            throw new BaseException(500," USER_AUTHONTOKEN ERROR");
        }
        return orderService.createOrder(goodsId,skuId, authToken.getUserId());
    }




}
