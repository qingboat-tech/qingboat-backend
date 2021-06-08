package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

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
     * (注意：当这张表很大的时候，效率会很慢)
     */
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<UserSubscriptionEntity> findSubscriptionList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                                              @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                                              @RequestParam(value = "paid",required = false) Boolean paid) {
        Long uid = getUId();
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UserSubscriptionEntity> lambdaQueryWrapper =  queryWrapper.lambda();
        lambdaQueryWrapper.eq(UserSubscriptionEntity::getCreatorId, uid);
        if (paid!=null){
            if (paid){
                lambdaQueryWrapper.ne(UserSubscriptionEntity::getOrderId,0);
            }else {
                lambdaQueryWrapper.eq(UserSubscriptionEntity::getOrderId,0);
            }
        }
        if (pageSize == null || pageSize<1){
            pageSize =10;
        }
        if (pageIndex == null || pageIndex<1){
            pageIndex =1;
        }
        IPage<UserSubscriptionEntity> page = new Page<>(pageIndex, pageSize);
        page = userSubscriptionService.page(page, queryWrapper);
        if (page.getRecords()!=null){
            for (UserSubscriptionEntity subscriptionEntity: page.getRecords()) {
                UserEntity u = userService.findByUserId(subscriptionEntity.getSubscriberId());
                subscriptionEntity.setSubscriberNickname(u.getNickname());
                subscriptionEntity.setSubscriberHeadImgUrl(u.getHeadimgUrl());
                subscriptionEntity.setExpertiseArea(u.getExpertiseArea());
                subscriptionEntity.setSubscriberDesc(u.getDescription());
            }
        }

        return page;
    }

    /**
     * 获取该套餐订阅人数数据
     */
    @GetMapping(value = "/getTierSubscritionCount")
    @ResponseBody
    public Integer getTierSubscritionCount(@RequestParam("tierId") Long tierId) {
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UserSubscriptionEntity> lambdaQueryWrapper =  queryWrapper.lambda();
        lambdaQueryWrapper.eq(UserSubscriptionEntity::getMemberTierId, tierId);
        return userSubscriptionService.count(queryWrapper);
    }



    /**
     * 获取当前订阅人数数据 (全部、免费、付费)
     */
    @GetMapping(value = "/currentCount")
    @ResponseBody
    public Integer getCurrentSubscriptionCount(@RequestParam(value = "paid",required = false) Boolean paid) {
        // TODO: redis缓存
        Long uid = getUId();
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UserSubscriptionEntity> lambdaQueryWrapper =  queryWrapper.lambda();
        lambdaQueryWrapper.eq(UserSubscriptionEntity::getCreatorId, uid);
        if (paid!=null){
            if (paid){
                lambdaQueryWrapper.ne(UserSubscriptionEntity::getOrderId,0);
            }else {
                lambdaQueryWrapper.eq(UserSubscriptionEntity::getOrderId,0);
            }
        }
        return userSubscriptionService.count(queryWrapper);

    }

    /**
     * 获取当前昨日新增人数数据(全部、免费、付费)
     */
    @GetMapping(value = "/lastCount")
    @ResponseBody
    public Integer getLastSubscriptionCount(@RequestParam(value = "paid",required = false) Boolean paid) {
        // TODO: redis缓存
        Long uid = getUId();
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LambdaQueryWrapper<UserSubscriptionEntity> lambdaQueryWrapper =  queryWrapper.lambda();

        lambdaQueryWrapper.
                gt(UserSubscriptionEntity::getCreatedAt, yesterday).
                lt(UserSubscriptionEntity::getCreatedAt, today).
                eq(UserSubscriptionEntity::getCreatorId, uid);
        if (paid!=null){
            if (paid){
                lambdaQueryWrapper.ne(UserSubscriptionEntity::getOrderId,0);
            }else {
                lambdaQueryWrapper.eq(UserSubscriptionEntity::getOrderId,0);
            }
        }
        return userSubscriptionService.count(queryWrapper);
    }

    /**
     * 获取系统的权益列表
     */
    @GetMapping(value = "/getBenefits")
    @ResponseBody
    public List<BenefitEntity> getBenefits(@RequestParam(value = "scope",required = false) String scope) {
        Long creatorId = getUId();
        QueryWrapper<BenefitEntity> queryWrapper = new QueryWrapper<>();
        if ("system".equals(scope)){
            queryWrapper.eq("`key`","FREE").or().eq("`key`","READ");
        }else {
            queryWrapper.eq("creator_id",0).or().eq("creator_id",creatorId);
        }
        List<BenefitEntity> benefitEntityList = benefitService.list(queryWrapper);

        QueryWrapper<TierEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id",creatorId);
        wrapper.eq("`status`",1);

        List<TierEntity> tierEntityList = tierService.list(wrapper);

        for (BenefitEntity benefitEntity: benefitEntityList){
            String benefitKey = benefitEntity.getKey();
            if (tierEntityList == null){
                break;
            }
            for (TierEntity  tierEntity: tierEntityList){
                if (tierEntity.getBenefitList() == null){
                    break;
                }
                for (BenefitEntity b: tierEntity.getBenefitList()){
                    if (benefitKey.equals(b.getKey())){
                        benefitEntity.addUsedCount();
                    }
                }
            }
        }
        return benefitEntityList;
    }

    /**
     * 添加创作者的权益
     */
    @PostMapping(value = "/saveBenefit")
    @ResponseBody
    public BenefitEntity saveBenefit(@RequestBody BenefitEntity benefitEntity) {
        Long creatorId = getUId();
        benefitEntity.setCreatorId(creatorId);
        if (benefitEntity.getKey() == null || benefitEntity.getKey().isEmpty()){
            String key = UUID.randomUUID().toString();
            benefitEntity.setKey(key);
        }

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
        return benefitService.remove(queryWrapper);
    }

    /**
     * 根据benefitKey，查询被Tier应用到的TierList
     *
     * @param creatorId
     * @param benefitKey
     * @return
     */
    @GetMapping(value = "/getTierListByBenefitKey")
    @ResponseBody
    public List<TierEntity> getTierListByBenefitKey(@RequestParam("creatorId") Long creatorId,@RequestParam("benefitKey")String benefitKey){
        return tierService.getTierListByBenefitKey(creatorId,benefitKey);
    }

    /**
     * 获取creator会员等级列表
     */
    @GetMapping(value = "/getTierList")
    @ResponseBody
    public List<TierEntity> getTierList(@RequestParam(value = "creatorId",required = false) Long creatorId,@RequestParam(value = "needMock",required = false) Integer needMock) {

        if (creatorId == null){
            creatorId = getUId();
        }

        TierEntity tierEntity = new TierEntity();
        tierEntity.setCreatorId(creatorId);
        tierEntity.setStatus(1);

        QueryWrapper<TierEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status",1).eq("creator_id",creatorId);
        wrapper.orderByAsc("month_price");

        List<TierEntity> list = tierService.list(wrapper);

        boolean hasFreeTier = false;
        boolean hasPaidTier = false;



        if (list!=null && !list.isEmpty()){
            //添加当前订阅的人数
            for (TierEntity tier:list){
//                QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
//                queryWrapper.lambda().eq(UserSubscriptionEntity::getCreatorId, getUId())
//                                     .eq(UserSubscriptionEntity::getMemberTierId,tier.getId());
//                int count = userSubscriptionService.count(queryWrapper);
//                tier.setSubscribeCount(count);

                if ("free".equalsIgnoreCase(tier.getSubscribeDuration()) && !hasFreeTier){
                    hasFreeTier = true;
                }else if (!hasPaidTier){
                    hasPaidTier = true;
                }
            }
        }
        if ( !Integer.valueOf(1).equals(needMock)){ //返回创作者真实的TierList
            return list;
        }

        if (list == null){
            list = new ArrayList<>();
        }
        if (!hasFreeTier){
            tierEntity = new TierEntity();
            tierEntity.setCreatorId(getUId());
            tierEntity.setTitle("大众会员");
            tierEntity.setMonthPrice(0);
            tierEntity.setMonthDiscount(10.00);
            tierEntity.setDesc("大众会员");
            tierEntity.setSubscribeDuration("free");
            tierEntity.setSubscribeCount(0);

            List<BenefitEntity> bList = new ArrayList<>();
            BenefitEntity benefitEntity = new BenefitEntity();
            benefitEntity.setId(1L);
            benefitEntity.setCreatorId(0L);
            benefitEntity.setKey("FREE");
            benefitEntity.setTitle("每月推送优质免费文章");
            bList.add(benefitEntity);

            tierEntity.setBenefitList(bList);

            list.add(0,tierEntity);
        }
        if (!hasPaidTier){
            tierEntity = new TierEntity();
            tierEntity.setCreatorId(getUId());
            tierEntity.setTitle("黄金会员");
            tierEntity.setSubscribeDuration("monthAndYear");
            tierEntity.setMonthPrice(800);
            tierEntity.setMonthDiscount(10.00);
            tierEntity.setYearPrice(9600);
            tierEntity.setYearDiscount(8.00);
            tierEntity.setDesc("黄金会员");
            tierEntity.setSubscribeLimit(10000);
            tierEntity.setSubscribeCount(0);

            List<BenefitEntity> bList = new ArrayList<>();
            BenefitEntity benefitEntity = new BenefitEntity();
            benefitEntity.setId(2L);
            benefitEntity.setCreatorId(0L);
            benefitEntity.setKey("READ");
            benefitEntity.setTitle("订阅期无限制阅读");
            bList.add(benefitEntity);

            benefitEntity = new BenefitEntity();
            benefitEntity.setId(3L);
            benefitEntity.setCreatorId(0L);
            benefitEntity.setKey("COMMENT");
            benefitEntity.setTitle("评论区互动");
            bList.add(benefitEntity);

            benefitEntity = new BenefitEntity();
            benefitEntity.setId(4L);
            benefitEntity.setCreatorId(0L);
            benefitEntity.setKey("WX_GROUP");
            benefitEntity.setTitle("加入创作者微信群");
            bList.add(benefitEntity);

            tierEntity.setBenefitList(bList);

            list.add(tierEntity);


        }

        return list;
    }


    /**
     * 创建会员等级的接口
     */
    @PostMapping(value = "/tier")
    @ResponseBody
    public TierEntity addCreatorTierEntity(@RequestBody TierEntity tierEntity) {
        if (StringUtils.isEmpty(tierEntity.getTitle())){
            throw new BaseException(500,"操作失败：创建会员等级标题为空");
        }
        List<BenefitEntity> benefitEntityList = tierEntity.getBenefitList();
        if (benefitEntityList == null || benefitEntityList.isEmpty()){
            throw new BaseException(500,"操作失败：会员等级订阅里的权益不能为空");
        }
        Set<String> benefitKeySet = new HashSet<>();
        for (BenefitEntity benefitEntity:benefitEntityList){
            if (benefitEntity.getId() == null ){
                throw new BaseException(500,"操作失败：创建会员等级订阅里的权益Id为空");
            }
            if (StringUtils.isEmpty(benefitEntity.getTitle())){
                throw new BaseException(500,"操作失败：创建会员等级订阅里的权益标题为空");
            }

            if (StringUtils.isEmpty(benefitEntity.getKey())){
                throw new BaseException(500,"操作失败：创建会员等级订阅里的权益Key为空");
            }
            benefitKeySet.add(benefitEntity.getKey());
        }

        if (!("free".equals(tierEntity.getSubscribeDuration())
                || "month".equals(tierEntity.getSubscribeDuration())
                || "year".equals(tierEntity.getSubscribeDuration())
                || "monthAndYear".equals(tierEntity.getSubscribeDuration()))){
            throw new BaseException(500,"操作失败：创建会员等级订阅周期参数错误");
        }
        tierEntity.setCurrency("CNY");
        if ("free".equals(tierEntity.getSubscribeDuration())){
            tierEntity.setYearPrice(0);
            tierEntity.setYearDiscount(0.0);
            tierEntity.setMonthPrice(0);
            tierEntity.setMonthDiscount(0.0);

            if (!benefitKeySet.contains("FREE")){
                throw new BaseException(500,"操作失败：免费会员等级里需要添加'每月推送优质免费文章'权益");
            }
        }
        if ("month".equals(tierEntity.getSubscribeDuration()) || "monthAndYear".equals(tierEntity.getSubscribeDuration()) ){
            if (tierEntity.getMonthPrice() == null || tierEntity.getMonthPrice()<0){
                tierEntity.setMonthPrice(0);
            }
            if (tierEntity.getMonthDiscount()>10 || tierEntity.getMonthDiscount()<0){
                throw new BaseException(500,"操作失败：创建会员等级订阅月折扣参数错误");
            }
        }
        if ("year".equals(tierEntity.getSubscribeDuration()) || "monthAndYear".equals(tierEntity.getSubscribeDuration()) ){
            if (tierEntity.getYearPrice() == null || tierEntity.getYearPrice()<0){
                tierEntity.setYearPrice(0);
            }
            if (tierEntity.getYearDiscount()>10 || tierEntity.getYearDiscount()<0){
                throw new BaseException(500,"操作失败：创建会员等级订阅月折扣参数错误");
            }
        }

        Long uid = getUId();
        tierEntity.setCreatorId(uid);
        tierEntity.setStatus(1);
        tierEntity.setUpdatedAt(new Date());
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
