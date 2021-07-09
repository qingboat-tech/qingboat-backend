package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.TierDao;
import com.qingboat.as.dao.UserProfileDao;
import com.qingboat.as.entity.TierEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserProfileDao, UserEntity> implements UserService {

    @Autowired
    UserProfileDao userProfilaDao;


    @Override
    public UserEntity findByUserId(Long userId) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<UserEntity> findListByUserIds(Set<Long> userIdSet) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id",userIdSet);
        return this.list(queryWrapper);
    }

    @Override
    public byte getRoleByUserId(Integer userId) {
        return userProfilaDao.getRoleByUserId(userId);
    }
}
