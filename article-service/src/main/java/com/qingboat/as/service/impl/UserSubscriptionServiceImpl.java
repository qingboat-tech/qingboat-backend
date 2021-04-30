package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.UserSubscriptionDao;
import com.qingboat.as.entity.UserSubscriptionEntity;
import com.qingboat.as.service.UserSubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class UserSubscriptionServiceImpl extends ServiceImpl<UserSubscriptionDao, UserSubscriptionEntity> implements UserSubscriptionService {

}
