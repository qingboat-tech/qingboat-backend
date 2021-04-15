package com.qingboat.as.service;

import com.qingboat.as.entity.UserEntity;

public interface UserService {

    UserEntity findByUserId(Long userId);

}
