package com.qingboat.us.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.us.dao.UserWechatDao;
import com.qingboat.us.entity.UserWechatEntity;
import com.qingboat.us.service.UserWechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserWechatServiceImpl extends ServiceImpl<UserWechatDao, UserWechatEntity> implements UserWechatService {

}
