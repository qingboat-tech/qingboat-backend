package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.utils.DateUtil;
import com.qingboat.ts.api.TierService;
import com.qingboat.ts.api.TierServiceResponse;
import com.qingboat.ts.api.UserService;
import com.qingboat.ts.api.UserServiceResponse;
import com.qingboat.ts.dao.PurchaseOrderDao;
import com.qingboat.ts.entity.PurchaseOrderEntity;
import com.qingboat.ts.service.PurchaseOrderService;
import com.qingboat.ts.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;


@Service
@Slf4j
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderDao, PurchaseOrderEntity> implements PurchaseOrderService {

    @Autowired
    TierService tierService;

    @Autowired
    UserService userService;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public PurchaseOrderEntity createPurchaseOrder(Long tierId, String periodKey, Long uid) {
        TierServiceResponse tierServiceResponse = tierService.getTierById(tierId);

        if ( tierServiceResponse == null || tierServiceResponse.getStatus()==0) {
            throw new BaseException(500,"无效的tier");
        }

        // 获取creator和reader的信息
        UserServiceResponse readerUserServiceResponse = userService.getUserProfile(uid);
        UserServiceResponse creatorUserServiceResponse = userService.getUserProfile(tierServiceResponse.getCreatorId());

        PurchaseOrderEntity entity = new PurchaseOrderEntity();

        // 订单和支付信息
        entity.setOrderNo(generateOrderId());
        entity.setOrderStatus(0);
        entity.setPaymentStatus(0);
        entity.setPaymentMethod(1);

        // 金额信息
        if (Objects.equals("month",periodKey) ) {
            Double totalAmountDouble = (tierServiceResponse.getMonthPrice() * tierServiceResponse.getMonthDiscount()/10);
            Integer totalAmount = totalAmountDouble.intValue();

            entity.setTotalAmount(totalAmount);
            entity.setCouponAmount(0);
            entity.setActualAmount(totalAmount);
        }
        else if (Objects.equals("year",periodKey)) {
            Double totalAmountDouble = (tierServiceResponse.getYearPrice() * tierServiceResponse.getYearDiscount()/10);
            Integer totalAmount = totalAmountDouble.intValue();

            entity.setTotalAmount(totalAmount);
            entity.setCouponAmount(0);
            entity.setActualAmount(totalAmount);
        }
        else {
            throw new BaseException(500,"invalid periodkey");
        }

        // 买卖人的信息
        entity.setCreatorId(tierServiceResponse.getCreatorId());
        entity.setCreatorNickname(creatorUserServiceResponse.getNickname());
        entity.setPurchaseUserId(uid);
        entity.setPurchaseUserNickname(readerUserServiceResponse.getNickname());

        // tier信息
        entity.setSubscribeData(TierServiceResponse.ConvertToJson(tierServiceResponse));
        this.save(entity);

        return entity;
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
