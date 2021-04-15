package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingboat.as.dao.UserProfileDao;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserProfileDao userProfileDao;

    @Override
    public UserEntity findByUserId(Long userId) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return userProfileDao.selectOne(queryWrapper);
    }
}
