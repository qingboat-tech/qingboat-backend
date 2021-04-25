package com.qingboat.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingboat.base.exception.BaseException;
import com.qingboat.uc.dao.CreatorApplyFormMongoDao;
import com.qingboat.uc.dao.UserProfileDao;
import com.qingboat.uc.entity.CreatorApplyFormEntity;
import com.qingboat.uc.entity.UserProfileEntity;
import com.qingboat.uc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private CreatorApplyFormMongoDao creatorApplyFormMongoDao;




    @Override
    public UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity) {
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userProfileEntity.getUserId());

        userProfileEntity.setUpdatedAt(new Date());

        log.info(userProfileEntity.toString());

        int rst = userProfileDao.update(userProfileEntity,queryWrapper);

        if(rst==1){
            return  userProfileEntity;
        }else {
            throw new BaseException(500,"userProfile is empty");
        }
    }

    @Override
    public CreatorApplyFormEntity saveCreatorApplyForm(CreatorApplyFormEntity creatorApplyFormEntity) {
        return null;
    }

    @Override
    public CreatorApplyFormEntity getCreatorApplyForm(Long userId) {
        return null;
    }
}
