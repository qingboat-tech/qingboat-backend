package com.qingboat.as.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingboat.as.entity.UserSubscriptionEntity;

public interface UserSubscriptionService extends IService<UserSubscriptionEntity> {


    Object createBillAndUpdateWallet(UserSubscriptionEntity userSubscriptionEntity);


    UserSubscriptionEntity subscribe(UserSubscriptionEntity userSubscriptionEntity);

}