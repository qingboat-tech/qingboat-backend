package com.qingboat.ts.service.impl;

import com.qingboat.base.exception.BaseException;
import com.qingboat.ts.dao.GoodsDao;
import com.qingboat.ts.dao.GoodsSkuDao;
import com.qingboat.ts.dao.OrderDao;
import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.entity.GoodsSkuEntity;
import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.GoodsService;
import com.qingboat.ts.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    // inject good service
    @Autowired
    private GoodsService goodsService;

    @Override
    public OrderEntity createOrder(Long goodsId, Long skuId ,Long userId) {
        log.info( "   ====OrderServiceImpl.createOrder======  ");


        GoodsEntity goodsEntity = goodsService.getGoodsById(goodsId);
        if (goodsEntity==null) {
            throw new BaseException(500, "GoodsEntity Not Exist");
        }

        GoodsSkuEntity goodsSkuEntity = null;
        if (goodsEntity.getSkuList()!=null) {
            for (GoodsSkuEntity skuEntity : goodsEntity.getSkuList()){
                if (skuEntity.getId().equals(skuId)){
                    goodsSkuEntity = skuEntity;
                    break;
                }
            }
        }
        if (skuId!=null && goodsSkuEntity==null) {
            throw new BaseException(500, "Goods Sku Not Matched");
        }

        Integer totalAmount = null;
        String currency = null;
        if (goodsSkuEntity==null) {
            totalAmount = goodsEntity.getPrice();
            currency = goodsEntity.getCurrency();
        }
        else {
            totalAmount = goodsSkuEntity.getPrice();
            currency = goodsSkuEntity.getCurrency();
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setGoodsSkuId(skuId);
        orderEntity.setOrderNo("1222");
        orderEntity.setPurchaseUserId(userId);
        orderEntity.setGoodsId(goodsId);
        orderEntity.setGoodsTitle(goodsEntity.getTitle());
        orderEntity.setGoodsImgUrl(goodsEntity.getImgUrl());
        orderEntity.setProductType(2);
        orderEntity.setProductId(goodsEntity.getId());
        orderEntity.setCurrency(currency);
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setActualAmount(totalAmount);

        orderEntity.setOrderStatus(0);
        orderEntity.setPaymentStatus(0);
        orderEntity.setCouponAmount(0);
        orderEntity.setComment("");

        orderEntity.setCreatedAt(new Date());
        orderEntity.setUpdatedAt(new Date());

        int rst  = orderDao.insert(orderEntity);
        log.info( orderEntity.getId() +" rst :" +rst);

        return orderEntity;

    }
}
