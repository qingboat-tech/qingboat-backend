package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.UserSubscriptionEntity;

public interface SubscriptionService {
    /**
     * 用户订阅
     */
    UserSubscriptionEntity userSubscribe(UserSubscriptionEntity userSubscriptionEntity);

    /**
     * 查询用户订阅列表（根据用户Id分表）
     */
    IPage<UserSubscriptionEntity> getUserSubscriptionAllList(Long userId);

    /**
     * 查询用户订阅有效期列表（根据用户Id分表）
     */
    IPage<UserSubscriptionEntity> getUserSubscriptionList(Long userId);


    /**
     * 查询创作者户订阅列表（根据创作者Id分表）
     */
    IPage<CreatorUserSubscriptionEntity> getCreatorUserSubscriptionAllList(Long creatorId);

}
