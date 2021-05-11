package com.qingboat.us.service;

import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;

public interface UserService {


    UserProfileEntity applyCreator(Long uid);

    UserProfileEntity getUserProfile(Long uid);

    UserProfileEntity getUserProfileByProfileKey(String profileKey);


    Boolean confirmCreator(Long applyUserId,Boolean rst);

    UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity);

    CreatorApplyFormEntity saveCreatorApplyForm(CreatorApplyFormEntity creatorApplyFormEntity);

    CreatorApplyFormEntity getCreatorApplyForm(Long userId);



}
