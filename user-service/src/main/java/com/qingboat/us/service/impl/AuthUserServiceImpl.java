package com.qingboat.us.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.us.dao.AuthUserDao;
import com.qingboat.us.entity.AuthUserEntity;
import com.qingboat.us.service.AuthUserService;

public class AuthUserServiceImpl extends ServiceImpl<AuthUserDao, AuthUserEntity> implements AuthUserService {
    @Override
    public AuthUserEntity getAuthUerByUserNameAndPwd(String userName, String password) {
        QueryWrapper<AuthUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userName).eq("password",password);
        AuthUserEntity entity = this.getOne(queryWrapper);
        return entity;
    }
}
