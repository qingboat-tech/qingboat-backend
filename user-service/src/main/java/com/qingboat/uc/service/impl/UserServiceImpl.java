package com.qingboat.uc.service.impl;

import com.qingboat.uc.dao.CreatorApplyFormMongoDao;
import com.qingboat.uc.dao.UserProfileDao;
import com.qingboat.uc.entity.CreatorApplyFormEntity;
import com.qingboat.uc.entity.UserProfileEntity;
import com.qingboat.uc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private CreatorApplyFormMongoDao creatorApplyFormMongoDao;


    @Override
    public UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity) {

    //TODO
        return null;
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
