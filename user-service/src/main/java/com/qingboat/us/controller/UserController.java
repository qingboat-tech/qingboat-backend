package com.qingboat.us.controller;

import com.qingboat.base.exception.BaseException;
import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/saveUserProfile")
    @ResponseBody
    public UserProfileEntity saveUserProfile(@Valid @RequestBody UserProfileEntity userProfileEntity){
        Long uid = getUId();
        log.info(" RequestParam: uid=" +uid + ",RequestBody"+userProfileEntity);
        userProfileEntity.setUserId(uid);
//        userProfileEntity.setRole(1);
//        userProfileEntity.setStatus(0);

        return userService.saveUserProfile(userProfileEntity);
    }

    @GetMapping("/applyCreator")
    @ResponseBody
    public UserProfileEntity applyCreator(){
        Long uid = getUId();
        log.info(" applyCreator, RequestParam: uid=" +uid);

        UserProfileEntity userProfileEntity = userService.applyCreator(uid);

        return null;
    }


    @GetMapping("/getCreatorApplyForm")
    @ResponseBody
    public CreatorApplyFormEntity getCreatorApplyForm(){
        Long uid = getUId();
        CreatorApplyFormEntity creatorApplyFormEntity= userService.getCreatorApplyForm(uid);
        return creatorApplyFormEntity;
    }

    @PostMapping("/saveCreatorApplyForm")
    @ResponseBody
    public CreatorApplyFormEntity saveCreatorApplyForm(@Valid @RequestBody CreatorApplyFormEntity creatorApplyFormEntity){
        Long uid = getUId();

        creatorApplyFormEntity.setUserId(uid);
        creatorApplyFormEntity= userService.saveCreatorApplyForm(creatorApplyFormEntity);
        userService.applyCreator(uid);

        //TODO 给氢舟后台管理员发一个审核通知

        return creatorApplyFormEntity;
    }


    private Long getUId(){
        Long uid = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            uid = (Long) request.getAttribute("UID");
        }
        if (uid == null){
            throw new BaseException(401,"AUTH_ERROR");
        }
        return uid;
    }

}