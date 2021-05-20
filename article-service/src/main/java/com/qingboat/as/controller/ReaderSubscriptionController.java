package com.qingboat.as.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.entity.*;

import com.qingboat.as.service.MessageService;
import com.qingboat.as.service.TierService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;

import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "/readersubscription")
@Slf4j
public class ReaderSubscriptionController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private TierService tierService;

    @Autowired
    private MessageService messageService;


    //=======================针对 reader 接口=============================
    /**
     * 获取某creator的全部会员等级的列表接口
     */
    @GetMapping(value = "/tiers")
    @ResponseBody
    public List<TierEntity> getTierEntityList(@Valid @RequestParam("creatorId") Long creatorId) {
        QueryWrapper<TierEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",creatorId).eq("status",1);
        queryWrapper.orderByAsc("month_price");

        Long userId = (Long) getAttribute("UID");

        List<TierEntity> tierEntityList = tierService.list(queryWrapper);
        if (userId != null && !userId.equals(creatorId)){
            QueryWrapper<UserSubscriptionEntity> userSubscriptionEntityQueryWrapper = new QueryWrapper<>();
            userSubscriptionEntityQueryWrapper.lambda().eq(UserSubscriptionEntity::getSubscriberId,userId)
                    .eq(UserSubscriptionEntity::getCreatorId,creatorId)
                    .ge(UserSubscriptionEntity::getExpireDate,new Date());

            UserSubscriptionEntity subscriptionEntity =userSubscriptionService.getOne(userSubscriptionEntityQueryWrapper);
            if (subscriptionEntity!= null){
                for (TierEntity e:tierEntityList) {
                    if (e.getId().equals(subscriptionEntity.getMemberTierId())){
                        e.setSubscribed(Boolean.TRUE);
                        break;
                    }
                }
            }
        }

        return tierEntityList;
    }

    /**
     * 根据tierId获取单条信息
     */
    @GetMapping(value = "/tier")
    @ResponseBody
    public TierEntity getTierEntity(@Valid @RequestParam("tierId") Long tierId) {
        QueryWrapper<TierEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",tierId);

        log.info("tierId:"+tierId);

        return tierService.getOne(queryWrapper);
    }

    /**
     * 读者获取订阅的creator用户列表
     */
    @GetMapping(value = "/creators")
    @ResponseBody
    public IPage<UserSubscriptionEntity> getCreatorEntityList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,@RequestParam(value = "pageSize",required = false) Integer pageSize) {
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscriber_id",getUId());

        if (pageSize == null || pageSize<1){
            pageSize =10;
        }
        if (pageIndex == null || pageIndex<0){
            pageIndex =0;
        }


        IPage<UserSubscriptionEntity> page = new Page<>(pageIndex, pageSize);
        IPage<UserSubscriptionEntity> iPage = userSubscriptionService.page(page,queryWrapper);
        for (UserSubscriptionEntity user: page.getRecords()) {
            UserEntity u = userService.findByUserId(user.getCreatorId());
            user.setCreatorNickname(u.getNickname());
            user.setCreatorHeadImgUrl(u.getHeadimgUrl());
        }
        return iPage;
    }

    /**
     * 读者免费订阅
     */
    @PostMapping(value = "/freeSubscribe")
    @ResponseBody
    public UserSubscriptionEntity userFreeSubscription(@RequestBody Map<String,Object> param) {

        Long creatorId = Long.valueOf(param.get("creatorId").toString());
        Long  tierId = Long.valueOf(param.get("tierId").toString()) ;
        Long subscriberId = getUId();
        UserEntity userEntity = userService.findByUserId(subscriberId);
        if (userEntity == null){
            throw new BaseException(500,"操作失败：非法用户");
        }
        if (creatorId == null || tierId == null){
            throw new BaseException(500,"操作失败：请求参数非法");
        }
        if (subscriberId.equals(creatorId)){
            throw new BaseException(500,"操作失败：不能订阅自己");
        }
        //1、检查以前是否有订阅
        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subscriber_id",subscriberId).eq("creator_id",creatorId);
        UserSubscriptionEntity beforeUserSubscription = userSubscriptionService.getOne(queryWrapper);
        if (beforeUserSubscription!=null){
            throw new BaseException(500,"操作失败：您已经订阅该创作者");
        }
        //2、处理新的免费订阅
        TierEntity entity = tierService.getById(tierId);
        if (entity == null || !"free".equals(entity.getSubscribeDuration())){
            throw new BaseException(500,"操作失败：您已经订阅信息不存在或需要付费");
        }
        Date startTime = new Date() ;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(Calendar.YEAR,30);

        UserSubscriptionEntity userSubscriptionEntity = new UserSubscriptionEntity();
        userSubscriptionEntity.setCreatorId(creatorId);
        userSubscriptionEntity.setSubscriberId(getUId());
        userSubscriptionEntity.setStartDate(startTime);
        userSubscriptionEntity.setExpireDate(cal.getTime());
        userSubscriptionEntity.setBenefitList(entity.getBenefitList());
        userSubscriptionEntity.setOrderId(0l);
        userSubscriptionEntity.setMemberTierId(tierId);
        userSubscriptionEntity.setMemberTierName(entity.getTitle());
        userSubscriptionEntity.setSubscribeDuration("free");
        userSubscriptionEntity.setOrderPrice(0);

        userSubscriptionService.save(userSubscriptionEntity);
        //发送订阅消息
        messageService.asyncSendSubscriptionMessage(userSubscriptionEntity);
        return userSubscriptionEntity;
    }

    /**
     * 获取当前订阅人数数据 (全部、免费、付费)
     */
    @GetMapping(value = "/currentCount")
    @ResponseBody
    public Integer getCurrentSubscriptionCount(
            @RequestParam(value = "paid",required = false) Boolean paid,
            @RequestParam(value = "creatorId") String creatorId) {
        // TODO: redis缓存

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UserSubscriptionEntity> lambdaQueryWrapper =  queryWrapper.lambda();
        lambdaQueryWrapper.eq(UserSubscriptionEntity::getCreatorId, creatorId);
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
     * 读者付费订阅的回调，支付完成的回调，共内部调用
     */
    @PostMapping(value = "/subscribe")
    @ResponseBody
    public UserSubscriptionEntity userSubscription(@RequestBody UserSubscriptionEntity userSubscriptionEntity,
                                                   @RequestHeader("INNER-SEC") String innerSec,
                                                   @RequestHeader("UID") Long subscriberId) {
        //判断header 里 是否有INNER-SEC和UID
//        Long subscriberId = getUId();
        Long  creatorId = userSubscriptionEntity.getCreatorId();
        subscriberId = userSubscriptionEntity.getSubscriberId();
        Long  memberTierId = userSubscriptionEntity.getMemberTierId();
        Long orderId = userSubscriptionEntity.getOrderId();
        String orderNo = userSubscriptionEntity.getOrderNo();
        Integer orderPrice = userSubscriptionEntity.getOrderPrice();
        String subscribeDuration = userSubscriptionEntity.getSubscribeDuration();

        if (subscriberId==null || creatorId == null || memberTierId == null || orderId ==null || orderPrice == null ||  subscribeDuration==null  ){
            throw new BaseException(500,"操作失败：请求参数非法");
        }

        TierEntity thisSubscriptionTier = tierService.getById(memberTierId);

        if (thisSubscriptionTier !=null){
            userSubscriptionEntity.setMemberTierName(thisSubscriptionTier.getTitle());
            Integer subscriptionLimit = thisSubscriptionTier.getSubscribeLimit();
            if (subscriptionLimit !=null && subscriptionLimit>0){
                //检查订阅限额, 已经放在创建订单之前做检查了，此处不做限制处理了。
            }
            for(BenefitEntity benefitEntity : thisSubscriptionTier.getBenefitList()){
                benefitEntity.setCreatorId(null);
                benefitEntity.setCategory(null);
                benefitEntity.setDesc(null);
                benefitEntity.setUsedCount(null);
            }

            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("subscriber_id",subscriberId);
            queryWrapper.eq("creator_id",creatorId);

            List<UserSubscriptionEntity>  beforeSubscriptionList= userSubscriptionService.list(queryWrapper);
            log.info(" beforeSubscriptionList: "+ JSON.toJSONString(beforeSubscriptionList));
            //1、判断是否是续订
            if (beforeSubscriptionList != null && !beforeSubscriptionList.isEmpty() ){
                UserSubscriptionEntity beforeUserSubscription = beforeSubscriptionList.get(0);
                if (beforeUserSubscription.getMemberTierId().equals(memberTierId)){  // 会员续费
                    if (beforeUserSubscription.getOrderId() ==0
                            || "free".equalsIgnoreCase(beforeUserSubscription.getSubscribeDuration()) ){ // 上次和这次都是免费订阅
                        log.info("上次和这次是免费订阅 ");
                        //发送订阅消息
                       // messageService.asyncSendSubscriptionMessage(userSubscriptionEntity);
                        return userSubscriptionEntity;
                    }
                    // 会员续费
                    Date startTime = beforeUserSubscription.getExpireDate();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startTime);

                    if ("month".equals(subscribeDuration)){
                        cal.add(Calendar.MONTH,1);
                    }else if ("year".equals(subscribeDuration)){
                        cal.add(Calendar.YEAR,1);
                    }else {
                        throw  new BaseException(500," subscribeDuration= "+subscribeDuration +" 参数错误");
                    }

                    beforeUserSubscription.setMemberTierName(thisSubscriptionTier.getTitle());
                    beforeUserSubscription.setOrderId(orderId);
                    beforeUserSubscription.setOrderNo(orderNo);
                    beforeUserSubscription.setOrderPrice(orderPrice);
                    beforeUserSubscription.setSubscribeDuration(subscribeDuration);
                    beforeUserSubscription.setStartDate(startTime);
                    beforeUserSubscription.setExpireDate(cal.getTime());
                    beforeUserSubscription.setBenefitList(thisSubscriptionTier.getBenefitList());
                    beforeUserSubscription.setCreatedAt(null);
                    beforeUserSubscription.setUpdatedAt(null);
                    userSubscriptionService.updateById(beforeUserSubscription);

                    // 给creator添加收益记录
                    userSubscriptionService.createBillAndUpdateWallet(beforeUserSubscription);

                    //发送订阅消息
                    messageService.asyncSendSubscriptionMessage(beforeUserSubscription);
                    return beforeUserSubscription;
                }else { // 免费转付费会员
                    if ("free".equalsIgnoreCase(beforeUserSubscription.getSubscribeDuration())){
                        Date startTime = new Date() ;
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(startTime);

                        if ("month".equals(subscribeDuration)){
                            cal.add(Calendar.MONTH,1);
                        }else if ("year".equals(subscribeDuration)){
                            cal.add(Calendar.YEAR,1);
                        }else {
                            throw  new BaseException(500," subscribeDuration= "+subscribeDuration +" 参数错误");
                        }
                        beforeUserSubscription.setMemberTierId(thisSubscriptionTier.getId());
                        beforeUserSubscription.setMemberTierName(thisSubscriptionTier.getTitle());
                        beforeUserSubscription.setOrderId(orderId);
                        beforeUserSubscription.setOrderNo(orderNo);
                        beforeUserSubscription.setOrderPrice(orderPrice);
                        beforeUserSubscription.setSubscribeDuration(subscribeDuration);
                        beforeUserSubscription.setStartDate(startTime);
                        beforeUserSubscription.setExpireDate(cal.getTime());
                        beforeUserSubscription.setBenefitList(thisSubscriptionTier.getBenefitList());
                        beforeUserSubscription.setCreatedAt(null);
                        beforeUserSubscription.setUpdatedAt(null);
                        userSubscriptionService.updateById(beforeUserSubscription);
                        //发送订阅消息
                        messageService.asyncSendSubscriptionMessage(beforeUserSubscription);

                        // 给creator添加收益记录
                        userSubscriptionService.createBillAndUpdateWallet(beforeUserSubscription);
                        return beforeUserSubscription;
                    }else {
                        throw  new BaseException(500 , "暂时不支持会员升级");
                    }
                }
            }

            //2、处理新的订阅
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            if ("month".equals(subscribeDuration)){
                cal.add(Calendar.MONTH,1);
            }else if ("year".equals(subscribeDuration)){
                cal.add(Calendar.YEAR,1);
            }else if ("free".equals(subscribeDuration) || Long.valueOf(0l).equals(thisSubscriptionTier.getMonthPrice())){
                cal.add(Calendar.YEAR,30);
            }

            userSubscriptionEntity.setSubscriberId(getUId());
            userSubscriptionEntity.setStartDate(new Date());
            userSubscriptionEntity.setExpireDate(cal.getTime());
            userSubscriptionEntity.setBenefitList(thisSubscriptionTier.getBenefitList());
            userSubscriptionEntity.setOrderId(orderId);
            userSubscriptionEntity.setOrderNo(orderNo);

            userSubscriptionService.save(userSubscriptionEntity);

            // 给creator添加收益记录
            userSubscriptionService.createBillAndUpdateWallet(userSubscriptionEntity);

            //发送订阅消息
            messageService.asyncSendSubscriptionMessage(userSubscriptionEntity);
            return userSubscriptionEntity;

        }else {
            throw  new BaseException(500,"TierId="+memberTierId +" 不存在。");
        }

    }

}
