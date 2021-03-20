package com.qingboat.ts.controller;

import com.qingboat.ts.service.GoodsService;
import com.qingboat.ts.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/getGoodsById")
    @ResponseBody
    public GoodsVo getCustomerById(@RequestParam(value = "goodsId") Long goodsId ){
        GoodsVo goodsVo = goodsService.getGoodsById(goodsId);
        return goodsVo;
    }
}
