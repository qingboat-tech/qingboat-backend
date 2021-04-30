package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.entity.*;

import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;

import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/subscription")
@Slf4j
public class SubscriptionController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;


    //=======================针对 creator 接口=============================

    /**
     * 获取Creator的订阅者列表接口，包括当前订阅人数和昨日新增两个query parameter结果集
     */
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<UserSubscriptionEntity> findSubscriptionList(@RequestParam("pageIndex") int pageIndex) {
        Long uid = getUId();

        return null;
    }


    /**
     * 获取当前订阅人数数据
     */
    @GetMapping(value = "/currentCount")
    @ResponseBody
    public Integer getCurrentSubscriptionCount(@RequestParam("pageIndex") int pageIndex) {
        Long uid = getUId();
        return 0;
    }

    /**
     * 获取当前昨日新增人数数据
     */
    @GetMapping(value = "/lastCount")
    @ResponseBody
    public Integer getLastSubscriptionCount(@RequestParam("pageIndex") int pageIndex) {
        Long uid = getUId();
        return 0;
    }


    /**
     * 获取creater的权益列表
     */
    @GetMapping(value = "/getBenefits")
    @ResponseBody
    public List<BenefitEntity> getBenefits() {
        Long uid = getUId();
        return null;
    }

    /**
     * 创建会员等级的接口
     */
    @PostMapping(value = "/tiers/add")
    @ResponseBody
    public MemberTierEntity addMemberTierEntityList() {
        Long uid = getUId();
        return null;
    }

    /**
     * 创建会员和权益的组合关系表
     */
    @PostMapping(value = "/memberTiers/add")
    @ResponseBody
    public List<MemberTierBenefitEntity> addMemberTierBenefitEntityList() {
        Long uid = getUId();
        return null;
    }



    //=======================针对 reader 接口=============================
    /**
     * 获取某creator的全部会员等级的列表接口
     */
    @GetMapping(value = "/tiers")
    @ResponseBody
    public List<MemberTierEntity> getMemberTierEntityList(@RequestParam("pageIndex") int pageIndex) {
        Long uid = getUId();
        return null;
    }

    /**
     * 读者获取订阅的creator用户列表
     */
    @GetMapping(value = "/creators")
    @ResponseBody
    public IPage<UserSubscriptionEntity> getCreatorEntityList(@RequestParam("pageIndex") int pageIndex) {
        UserSubscriptionEntity entity = new UserSubscriptionEntity();
        entity.setSubscriberId(getUId());

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);

        IPage<UserSubscriptionEntity> page = new Page<>(pageIndex, 10);

        return userSubscriptionService.page(page,queryWrapper);
    }

    /**
     * 获取该阅读者
     */
//    @GetMapping(value = "/tiers")
//    @ResponseBody
//    public List<MemberTierEntity> getMemberTierEntityList(@RequestParam("pageIndex") int pageIndex) {
//        Long uid = getUId();
//        return null;
//    }


    private Long getUId(){
        Long uid =  0l;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof  ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            uid = (Long) request.getAttribute("UID");
            if (uid == null){
                throw new BaseException(401,"AUTH_ERROR");
            }
        }
        return uid;
    }



}
