package com.qingboat.us.service;

import com.qingboat.us.entity.AuthUserEntity;
import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;

public interface AuthUserService {

    AuthUserEntity getAuthUerByUserNameAndPwd(String userName,String password);

}
