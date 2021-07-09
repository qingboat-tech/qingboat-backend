package com.qingboat.as.service;

import com.qingboat.as.entity.UserEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserEntity findByUserId(Long userId);

    List<UserEntity> findListByUserIds(Set<Long> userId);


    byte getRoleByUserId(Integer userId);



}
