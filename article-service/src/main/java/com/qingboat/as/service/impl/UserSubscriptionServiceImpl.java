package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.api.TradeService;
import com.qingboat.api.vo.CreatorBillVo;
import com.qingboat.as.dao.UserSubscriptionDao;
import com.qingboat.as.entity.UserSubscriptionEntity;
import com.qingboat.as.filter.AuthFilter;
import com.qingboat.as.service.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSubscriptionServiceImpl extends ServiceImpl<UserSubscriptionDao, UserSubscriptionEntity> implements UserSubscriptionService {


    @Autowired
    private TradeService tradeService;

    @Override
    public Object createBillAndUpdateWallet(UserSubscriptionEntity userSubscriptionEntity) {
        // 给creator添加收益记录
        CreatorBillVo creatorBillVo = new CreatorBillVo();
        creatorBillVo.setCreatorId(userSubscriptionEntity.getCreatorId());
        creatorBillVo.setBillType(1);
        String typeChinese = null;
        if ("year".equals(userSubscriptionEntity.getSubscribeDuration())){
            typeChinese = "年";
        }
        if ("month".equals(userSubscriptionEntity.getSubscribeDuration())){
            typeChinese = "月";
        }
        creatorBillVo.setAmount(1L*userSubscriptionEntity.getOrderPrice());
        creatorBillVo.setOrderNo(userSubscriptionEntity.getOrderNo());
        creatorBillVo.setBillTitle("新增订阅"+typeChinese+"订阅会员");

        String getCreatorIdStr = String.valueOf(userSubscriptionEntity.getCreatorId());
        String sec = AuthFilter.getSecret(getCreatorIdStr);

        tradeService.createBillAndUpdateWallet(creatorBillVo, sec, getCreatorIdStr);
        return null;

    }
}
