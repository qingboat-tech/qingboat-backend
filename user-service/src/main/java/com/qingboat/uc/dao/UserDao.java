package com.qingboat.uc.dao;

import com.qingboat.uc.entity.UserEntity;

public interface UserDao {

    UserEntity getUserById(Long userId);

}
