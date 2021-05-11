package com.qingboat.as.controller;

import com.alibaba.fastjson.JSONObject;
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
        List<BenefitEntity> benefitEntityList = benefitService.list(queryWrapper);

        QueryWrapper<TierEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id",creatorId);

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
        boolean rst = benefitService.remove(queryWrapper);
        return rst;
    }


    /**
     * 获取creator会员等级列表
     */
    @GetMapping(value = "/getTierList")
    @ResponseBody
    public List<TierEntity> getTierList(@RequestParam(value = "creatorId",required = false) Long creatorId,@RequestParam(value = "needMock",required = false) Integer needMock) {

        if (creatorId == null){
            creatorId = getUId();
            needMock = null;
        }

        QueryWrapper<TierEntity> wrapper = new QueryWrapper<>();
        TierEntity tierEntity = new TierEntity();
        tierEntity.setStatus(1);
        tierEntity.setCreatorId(creatorId);
        wrapper.setEntity(tierEntity);
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
            if (needMock == null){
                return list;
            }
        }
        if (list == null){
            list = new ArrayList<>();
        }
        if (!hasFreeTier){
            tierEntity = new TierEntity();
            tierEntity.setCreatorId(getUId());
            tierEntity.setTitle("示例方案一：免费");
            tierEntity.setMonthPrice(0);
            tierEntity.setMonthDiscount(10.00);
            tierEntity.setDesc("示例方案一模板");
            tierEntity.setSubscribeDuration("free");
            tierEntity.setSubscribeCount(0);

            List<BenefitEntity> bList = new ArrayList<>();
            BenefitEntity benefitEntity = new BenefitEntity();
            benefitEntity.setId(1l);
            benefitEntity.setCreatorId(0l);
            benefitEntity.setKey("FREE");
            benefitEntity.setTitle("每月推送优质免费文章");
            bList.add(benefitEntity);

            tierEntity.setBenefitList(bList);

            list.add(0,tierEntity);
        }
        if (!hasPaidTier){
            tierEntity = new TierEntity();
            tierEntity.setCreatorId(getUId());
            tierEntity.setTitle("示例方案二：付费");
            tierEntity.setSubscribeDuration("monthAndYear");
            tierEntity.setMonthPrice(800);
            tierEntity.setMonthDiscount(10.00);
            tierEntity.setYearPrice(9600);
            tierEntity.setYearDiscount(8.00);
            tierEntity.setDesc("付费订阅模板");
            tierEntity.setSubscribeLimit(10000);
            tierEntity.setSubscribeCount(0);

            List<BenefitEntity> bList = new ArrayList<>();
            BenefitEntity benefitEntity = new BenefitEntity();
            benefitEntity.setId(2l);
            benefitEntity.setCreatorId(0l);
            benefitEntity.setKey("READ");
            benefitEntity.setTitle("订阅期无限制阅读");
            bList.add(benefitEntity);

            benefitEntity = new BenefitEntity();
            benefitEntity.setId(3l);
            benefitEntity.setCreatorId(0l);
            benefitEntity.setKey("COMMENT");
            benefitEntity.setTitle("评论区互动");
            bList.add(benefitEntity);

            benefitEntity = new BenefitEntity();
            benefitEntity.setId(4l);
            benefitEntity.setCreatorId(0l);
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
        if (!(  "free".equals(tierEntity.getSubscribeDuration())
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
        List<BenefitEntity> benefitEntityList = tierEntity.getBenefitList();
        if (benefitEntityList == null || benefitEntityList.isEmpty()){
            throw new BaseException(500,"操作失败：会员等级订阅里的权益不能为空");
        }
        for (BenefitEntity benefitEntity:benefitEntityList){
            if (benefitEntity.getId() == null ){
                throw new BaseException(500,"操作失败：创建会员等级订阅里的权益Id为空");
            }
            if (StringUtils.isEmpty(benefitEntity.getKey())){
                throw new BaseException(500,"操作失败：创建会员等级订阅里的权益Key为空");
            }
            if (StringUtils.isEmpty(benefitEntity.getTitle())){
                throw new BaseException(500,"操作失败：创建会员等级订阅里的权益标题为空");
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
