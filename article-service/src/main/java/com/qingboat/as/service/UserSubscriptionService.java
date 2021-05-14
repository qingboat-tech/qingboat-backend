package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingboat.as.entity.CreatorBillEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

public interface UserSubscriptionService extends IService<UserSubscriptionEntity> {


    Object createBillAndUpdateWallet(UserSubscriptionEntity userSubscriptionEntity);

}