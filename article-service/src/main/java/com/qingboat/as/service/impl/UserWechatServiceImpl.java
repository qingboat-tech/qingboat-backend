package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.TierDao;
import com.qingboat.as.dao.UserWechatDao;
import com.qingboat.as.entity.UserWechatEntity;
import com.qingboat.as.service.UserWechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserWechatServiceImpl extends ServiceImpl<UserWechatDao, UserWechatEntity> implements UserWechatService {
}
