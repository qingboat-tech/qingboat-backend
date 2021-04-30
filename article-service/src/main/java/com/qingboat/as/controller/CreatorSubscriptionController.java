package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.TierEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import com.qingboat.as.service.BenefitService;
import com.qingboat.as.service.TierService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;
import com.qingboat.base.api.FeishuService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

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
    public IPage<UserSubscriptionEntity> findSubscriptionList(@RequestParam("pageIndex") int pageIndex) {
        Long uid = getUId();
        UserSubscriptionEntity entity = new UserSubscriptionEntity();
        entity.setSubscriberId(uid);

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
     * 获取creater的权益列表
     */
    @GetMapping(value = "/getBenefits")
    @ResponseBody
    public List<BenefitEntity> getBenefits() {
        return benefitService.list();
    }

    /**
     * 创建会员等级的接口
     */
    @PostMapping(value = "/tier/saveOrUpdate")
    @ResponseBody
    public TierEntity addCreatorTierEntity(@RequestBody TierEntity tierEntity) {
        Long uid = getUId();
        tierEntity.setCreatorId(uid);
        tierService.saveOrUpdate(tierEntity);
        return tierEntity;
    }


}
