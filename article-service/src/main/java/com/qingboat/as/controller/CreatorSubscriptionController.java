package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.TierEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import com.qingboat.as.service.BenefitService;
import com.qingboat.as.service.TierService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;
import com.qingboat.base.api.FeishuService;

import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/creatorsubscription")
@Slf4j
public class CreatorSubscriptionController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private BenefitService benefitService;

    @Autowired
    private TierService tierService;

    //=======================针对 creator 接口=============================

    /**
     * 获取Creator的订阅者列表接口，包括当前订阅人数和昨日新增两个query parameter结果集
     */
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<UserSubscriptionEntity> findSubscriptionList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,@RequestParam(value = "pageSize",required = false) Integer pageSize) {
        Long uid = getUId();
        UserSubscriptionEntity entity = new UserSubscriptionEntity();
        entity.setCreatorId(uid);

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        if (pageSize == null || pageSize<1){
            pageSize =10;
        }
        IPage<UserSubscriptionEntity> page = new Page<>(pageIndex, pageSize);
        for (UserSubscriptionEntity user: page.getRecords()) {
            UserEntity u = userService.findByUserId(user.getSubscriberId());
            user.setSubscriberNickname(u.getNickname());
            user.setSubscriberHeadImgUrl(u.getHeadimgUrl());
        }
        return userSubscriptionService.page(page, queryWrapper);
    }


    /**
     * 获取当前订阅人数数据
     */
    @GetMapping(value = "/currentCount")
    @ResponseBody
    public Integer getCurrentSubscriptionCount() {
        // TODO: redis缓存
        Long uid = getUId();
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
        Long uid = getUId();
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        queryWrapper.lambda().
                gt(UserSubscriptionEntity::getCreatedAt, yesterday).
                lt(UserSubscriptionEntity::getCreatedAt, today).
                eq(UserSubscriptionEntity::getCreatorId, uid);
        return userSubscriptionService.count(queryWrapper);

    }


    /**
     * 获取系统的权益列表
     */
    @GetMapping(value = "/getBenefits")
    @ResponseBody
    public List<BenefitEntity> getBenefits() {
        Long creatorId = getUId();
        QueryWrapper<BenefitEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",0).or().eq("creator_id",creatorId);
        return benefitService.list(queryWrapper);
    }

    /**
     * 添加创作者的权益
     */
    @PostMapping(value = "/saveBenefit")
    @ResponseBody
    public BenefitEntity saveBenefit(@RequestBody BenefitEntity benefitEntity) {
        Long creatorId = getUId();
        String key = UUID.randomUUID().toString();
        benefitEntity.setCreatorId(creatorId);
        benefitEntity.setKey(key);

        boolean rst =  benefitService.saveOrUpdate(benefitEntity);
        if (rst){
            return benefitEntity;
        }
        throw new BaseException(500,"添加创作者的权益失败");
    }
    /**
     * 添加创作者的权益
     */
    @DeleteMapping(value = "/delBenefit")
    @ResponseBody
    public Boolean delBenefit(@RequestParam("benefitId") Long benefitId) {
        Long creatorId = getUId();
        QueryWrapper<BenefitEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",benefitId).eq("creator_id",creatorId);
        boolean rst = benefitService.remove(queryWrapper);
        return rst;
    }


    /**
     * 获取creator会员等级列表
     */
    @GetMapping(value = "/getTierList")
    @ResponseBody
    public List<TierEntity> getTierList(@RequestParam("needMock") Integer needMock) {
        QueryWrapper<TierEntity> wrapper = new QueryWrapper<>();
        TierEntity tierEntity = new TierEntity();
        tierEntity.setStatus(1);
        tierEntity.setCreatorId(getUId());
        wrapper.setEntity(tierEntity);
        List<TierEntity> list = tierService.list(wrapper);
        if (list!=null && !list.isEmpty()){
            //TODO 添加当前订阅的人数

            return list;
        }
        if (Integer.valueOf(1).equals(needMock)){
            list = new ArrayList<>();
            tierEntity = new TierEntity();
            tierEntity.setCreatorId(getUId());
            tierEntity.setTitle("免费订阅");
            tierEntity.setCreatedAt(new Date());
            tierEntity.setMonthPrice(0);
            tierEntity.setMonthDiscount(10.00);
            tierEntity.setDesc("免费订阅模板");
            tierEntity.setSubscribeCount(0);

            List<BenefitEntity> bList = new ArrayList<>();
            BenefitEntity benefitEntity = new BenefitEntity();
            benefitEntity.setId(1l);
            benefitEntity.setKey("FREE");
            benefitEntity.setTitle("每月推送优质免费文章");
            bList.add(benefitEntity);

            tierEntity.setBenefitList(bList);

            list.add(tierEntity);

            tierEntity = new TierEntity();
            tierEntity.setCreatorId(getUId());
            tierEntity.setTitle("方案一");
            tierEntity.setCreatedAt(new Date());
            tierEntity.setMonthPrice(800);
            tierEntity.setMonthDiscount(10.00);
            tierEntity.setYearPrice(9600);
            tierEntity.setYearDiscount(8.00);
            tierEntity.setDesc("付费订阅模板");
            tierEntity.setSubscribeLimit(10000);
            tierEntity.setSubscribeCount(0);

            bList = new ArrayList<>();
            benefitEntity = new BenefitEntity();
            benefitEntity.setId(2l);
            benefitEntity.setKey("READ");
            benefitEntity.setTitle("订阅期无限制阅读");
            bList.add(benefitEntity);

            benefitEntity = new BenefitEntity();
            benefitEntity.setId(3l);
            benefitEntity.setKey("COMMENT");
            benefitEntity.setTitle("评论区互动");
            bList.add(benefitEntity);

            tierEntity.setBenefitList(bList);

            list.add(tierEntity);

            return list;
        }

        return null;
    }


    /**
     * 创建会员等级的接口
     */
    @PostMapping(value = "/tier")
    @ResponseBody
    public TierEntity addCreatorTierEntity(@RequestBody TierEntity tierEntity) {
        Long uid = getUId();
        tierEntity.setCreatorId(uid);
        tierEntity.setStatus(1);
        tierService.saveOrUpdate(tierEntity);
        return tierEntity;
    }

    /**
     * 创建会员等级的接口
     */
    @DeleteMapping(value = "/tier")
    @ResponseBody
    public TierEntity delTierEntity(@RequestParam("tierId") Long tierId) {
        Long uid = getUId();
        TierEntity tierEntity = tierService.getById(tierId);
        if (tierEntity == null){
            throw new BaseException(500,"操作失败：该会员等级不存在");
        }
        if (tierEntity.getCreatorId().equals(uid)){
            tierEntity.setStatus(0);

            tierService.updateById(tierEntity);
            return tierEntity;
        }
        throw new BaseException(500,"操作失败：该会员等级不归属你哦");
    }


}
