package com.qingboat.ts.service.impl;

import com.qingboat.base.exception.BaseException;
import com.qingboat.ts.dao.GoodsDao;
import com.qingboat.ts.dao.GoodsSkuDao;
import com.qingboat.base.utils.DateUtil;
import com.qingboat.ts.dao.OrderDao;
import com.qingboat.ts.entity.GoodsEntity;
import com.qingboat.ts.entity.GoodsSkuEntity;
import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.GoodsService;
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
    private GoodsService goodsService;

    @Autowired
    private RedisUtil redisUtil;

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
        orderEntity.setOrderNo(generateOrderId());
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

        int count  = orderDao.insert(orderEntity);
        OrderEntity rst = new OrderEntity();
        rst.setId(orderEntity.getId());
        rst.setOrderNo(orderEntity.getOrderNo());
        rst.setActualAmount(orderEntity.getActualAmount());
        rst.setTotalAmount(orderEntity.getTotalAmount());
        rst.setCurrency(orderEntity.getCurrency());
        rst.setCreatedAt(orderEntity.getCreatedAt());
        return rst;
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
