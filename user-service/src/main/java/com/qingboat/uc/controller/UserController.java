package com.qingboat.uc.controller;

import com.qingboat.base.exception.BaseException;
import com.qingboat.uc.entity.UserProfileEntity;
import com.qingboat.uc.service.ProviderService;
import com.qingboat.uc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/saveUserProfile")
    @ResponseBody
    public UserProfileEntity saveUserProfile(@Valid @RequestBody UserProfileEntity userProfileEntity){
        //TODO
        return null;
    }


    private String getUId(){
        String StrUid = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            Long uid = (Long) request.getAttribute("UID");
            if (uid == null){
                throw new BaseException(401,"AUTH_ERROR");
            }
            StrUid = String.valueOf(uid);
        }
        return StrUid;
    }

}
