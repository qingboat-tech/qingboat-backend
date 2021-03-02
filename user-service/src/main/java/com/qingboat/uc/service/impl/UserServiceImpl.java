package com.qingboat.uc.service.impl;

import com.qingboat.uc.dao.UserDao;
import com.qingboat.uc.entity.UserEntity;
import com.qingboat.uc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserEntity getUserById(Long userId) {
        UserEntity user =userDao.getUserById(userId);
        return user;
    }
}
