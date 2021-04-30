package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.MemberTierBenefitEntity;
import com.qingboat.as.entity.MemberTierEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping(value = "/creatorsubscription")
@Slf4j
public class UserSubscriptionController {

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
        String uid = getUId();
        UserSubscriptionEntity entity = new UserSubscriptionEntity();
        entity.setSubscriberId(Long.parseLong(uid));

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);

        IPage<UserSubscriptionEntity> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex, 10);
        return userSubscriptionService.page(page, queryWrapper);
    }


    /**
     * 获取当前订阅人数数据
     */
    @GetMapping(value = "/currentCount")
    @ResponseBody
    public Integer getCurrentSubscriptionCount() {
        // TODO: redis缓存
        String uid = getUId();
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserSubscriptionEntity::getCreatorId, uid);
        return userSubscriptionService.count(queryWrapper);

    }

    /**
     * 获取当前昨日新增人数数据
     */
    @GetMapping(value = "/lastCount")
    @ResponseBody
    public Integer getLastSubscriptionCount() {
        // TODO: redis缓存
        String uid = getUId();
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        log.info(yesterday.toString() + LocalDate.now());
        queryWrapper.lambda().gt(UserSubscriptionEntity::getCreatedAt, yesterday).lt(UserSubscriptionEntity::getCreatedAt, today);
        return userSubscriptionService.count(queryWrapper);

    }


    /**
     * 获取creater的权益列表
     */
    @GetMapping(value = "/getBenefits")
    @ResponseBody
    public List<BenefitEntity> getBenefits() {
        String uid = getUId();
        return null;
    }

    /**
     * 创建会员等级的接口
     */
    @PostMapping(value = "/tiers/add")
    @ResponseBody
    public MemberTierEntity addMemberTierEntityList() {
        String uid = getUId();
        return null;
    }

    /**
     * 创建会员和权益的组合关系表
     */
    @PostMapping(value = "/memberTiers/add")
    @ResponseBody
    public List<MemberTierBenefitEntity> addMemberTierBenefitEntityList() {
        String uid = getUId();
        return null;
    }



    //=======================针对 reader 接口=============================
    /**
     * 获取某creator的全部会员等级的列表接口
     */
    @GetMapping(value = "/tiers")
    @ResponseBody
    public List<MemberTierEntity> getMemberTierEntityList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return null;
    }

    /**
     * 读者获取订阅的creator用户列表
     */
    @GetMapping(value = "/creators")
    @ResponseBody
    public List<UserSubscriptionEntity> getCreatorEntityList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return null;
    }

    /**
     * 获取该阅读者
     */
//    @GetMapping(value = "/tiers")
//    @ResponseBody
//    public List<MemberTierEntity> getMemberTierEntityList(@RequestParam("pageIndex") int pageIndex) {
//        String uid = getUId();
//        return null;
//    }


    private String getUId(){
        String StrUid = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof  ServletRequestAttributes){
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
