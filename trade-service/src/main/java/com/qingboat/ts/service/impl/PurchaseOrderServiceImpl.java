package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.api.TierService;
import com.qingboat.api.UserProfileService;
import com.qingboat.api.vo.TierVo;
import com.qingboat.api.vo.UserProfileVo;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.utils.DateUtil;
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
    UserProfileService userProfileService;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public PurchaseOrderEntity createPurchaseOrder(Long tierId, String periodKey, Long uid) {
        TierVo tierVo = tierService.getTierById(tierId);

        if ( tierVo == null || tierVo.getStatus()==0) {
            throw new BaseException(500,"无效的tier");
        }

        // 获取creator和reader的信息
        UserProfileVo readerUserVo = userProfileService.getUserProfile(uid);
        UserProfileVo creatorUserVo = userProfileService.getUserProfile(tierVo.getCreatorId());

        PurchaseOrderEntity entity = new PurchaseOrderEntity();

        // 订单和支付信息
        entity.setOrderNo(generateOrderId());
        entity.setOrderStatus(0);
        entity.setPaymentStatus(0);
        entity.setPaymentMethod(1);

        // 金额信息
        if (Objects.equals("month",periodKey) ) {
            Double totalAmountDouble = (tierVo.getMonthPrice() * tierVo.getMonthDiscount()/10);
            Integer totalAmount = totalAmountDouble.intValue();

            entity.setTotalAmount(totalAmount);
            entity.setCouponAmount(0);
            entity.setActualAmount(totalAmount);
            entity.setSubscribeDuration("month");
        }
        else if (Objects.equals("year",periodKey)) {
            Double totalAmountDouble = (tierVo.getYearPrice() * tierVo.getYearDiscount()/10);
            Integer totalAmount = totalAmountDouble.intValue();

            entity.setTotalAmount(totalAmount);
            entity.setCouponAmount(0);
            entity.setActualAmount(totalAmount);
            entity.setSubscribeDuration("year");
        }
        else {
            throw new BaseException(500,"invalid periodkey");
        }

        // 买卖人的信息
        entity.setCreatorId(tierVo.getCreatorId());
        entity.setCreatorNickname(creatorUserVo.getNickname());
        entity.setPurchaseUserId(uid);
        entity.setPurchaseUserNickname(readerUserVo.getNickname());

        // tier信息
        entity.setSubscribeData(TierVo.ConvertToJson(tierVo));
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
