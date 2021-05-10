package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.ts.api.TierService;
import com.qingboat.ts.api.TierServiceResponse;
import com.qingboat.ts.api.UserService;
import com.qingboat.ts.api.UserServiceResponse;
import com.qingboat.ts.dao.PurchaseOrderDao;
import com.qingboat.ts.entity.PurchaseOrderEntity;
import com.qingboat.ts.service.PurchaseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderDao, PurchaseOrderEntity> implements PurchaseOrderService {

    @Autowired
    TierService tierService;

    @Autowired
    UserService userService;

    @Override
    public PurchaseOrderEntity createPurchaseOrder(Long tierId, Long periodKey, Long uid) {
        TierServiceResponse tierServiceResponse = tierService.getTierById(tierId);

        if ( tierServiceResponse == null || tierServiceResponse.getStatus()==0) {
            return null;
        }

        // 获取creator和reader的信息
        UserServiceResponse readerUserServiceResponse = userService.getUserProfile(uid);
        UserServiceResponse creatorUserServiceResponse = userService.getUserProfile(tierServiceResponse.getCreatorId());

        PurchaseOrderEntity entity = new PurchaseOrderEntity();

        // 订单和支付信息
        String seqKey = "1334";
        entity.setOrderNo("测试");
        entity.setOrderStatus(0);
        entity.setPaymentStatus(0);
        entity.setPaymentMethod(1);

        // 金额信息
        if (periodKey == 1) {
            Double totalAmountDouble = (tierServiceResponse.getMonthPrice() * tierServiceResponse.getMonthDiscount()/10);
            Integer totalAmount = totalAmountDouble.intValue();

            entity.setTotalAmount(totalAmount);
            entity.setCouponAmount(0);
            entity.setActualAmount(totalAmount);
        }
        else if (periodKey == 2) {
            Double totalAmountDouble = (tierServiceResponse.getYearPrice() * tierServiceResponse.getYearDiscount()/10);
            Integer totalAmount = totalAmountDouble.intValue();

            entity.setTotalAmount(totalAmount);
            entity.setCouponAmount(0);
            entity.setActualAmount(totalAmount);
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
}
