package com.qingboat.uc.service;

import com.qingboat.uc.entity.CreatorApplyFormEntity;
import com.qingboat.uc.entity.UserProfileEntity;

public interface UserService {


    UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity);

    CreatorApplyFormEntity saveCreatorApplyForm(CreatorApplyFormEntity creatorApplyFormEntity);

    CreatorApplyFormEntity getCreatorApplyForm(Long userId);



}
