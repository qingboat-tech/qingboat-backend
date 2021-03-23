package com.qingboat.ts.controller;

import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.entity.GoodsSkuEntity;
import com.qingboat.ts.service.GoodsService;
import com.qingboat.ts.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/getGoodsById")
    @ResponseBody
    public GoodsEntity getGoodsById(@RequestParam(value = "goodsId") Long goodsId ){
        GoodsEntity goodsEntity = goodsService.getGoodsById(goodsId);
        return goodsEntity;
    }

    @GetMapping("/getGoodsSkuByGoodsId")
    @ResponseBody
    public List<GoodsSkuEntity> getGoodsSkuByGoodsId(@RequestParam(value = "goodsId") Long goodsId ){
        List<GoodsSkuEntity> list = goodsService.getGoodsSkuByGoodsId(goodsId);
        return list;
    }






}
