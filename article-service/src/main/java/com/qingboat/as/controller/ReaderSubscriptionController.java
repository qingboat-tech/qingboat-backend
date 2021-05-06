package com.qingboat.as.controller;

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

        TierEntity tierEntity = new TierEntity();
        tierEntity.setCreatorId(creatorId);
        tierEntity.setStatus(1);

        QueryWrapper<TierEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(tierEntity);

        return tierService.list(queryWrapper);
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
        for (UserSubscriptionEntity user: page.getRecords()) {
            UserEntity u = userService.findByUserId(user.getCreatorId());
            user.setCreatorNickname(u.getNickname());
            user.setCreatorHeadImgUrl(u.getHeadimgUrl());
        }
        return userSubscriptionService.page(page,queryWrapper);
    }

    /**
     * 读者订阅 Creator Tier
     */
    @PostMapping(value = "/subscribe")
    @ResponseBody
    public UserSubscriptionEntity userSubscription(@RequestBody UserSubscriptionEntity userSubscriptionEntity) {

        Long subscriberId = getUId();
        Long  creatorId = userSubscriptionEntity.getCreatorId();
        Long  memberTierId = userSubscriptionEntity.getMemberTierId();
        Long orderId = userSubscriptionEntity.getOrderId();
        Integer orderPrice = userSubscriptionEntity.getOrderPrice();
        String subscribeDuration = userSubscriptionEntity.getSubscribeDuration();

        if (creatorId == null || memberTierId == null || orderId ==null || orderPrice == null ||  subscribeDuration==null  ){
            throw new BaseException(500,"操作失败：请求参数非法");
        }

        TierEntity entity = tierService.getById(memberTierId);

        if (entity !=null){
            Integer subscriptionLimit = entity.getSubscribeLimit();
            if (subscriptionLimit !=null && subscriptionLimit>0){
                //TODO 检查订阅限额
            }

            UserSubscriptionEntity queryEntity = new UserSubscriptionEntity();
            queryEntity.setSubscriberId(subscriberId);
            queryEntity.setCreatorId(creatorId);
            queryEntity.setSubscriberId(subscriberId);

            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.setEntity(queryEntity);

            UserSubscriptionEntity beforeUserSubscription = userSubscriptionService.getOne(queryWrapper);
            //1、判断是否是续订
            if (beforeUserSubscription != null ){
                if (beforeUserSubscription.getMemberTierId().equals(memberTierId)){
                    if (beforeUserSubscription.getOrderId() ==0 ){ // 上次和这次都是免费订阅
                        log.info("上次和这次都是免费订阅 ");
                        //发送订阅消息
                        messageService.sendSubscriptionMessage(userSubscriptionEntity);
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

                    beforeUserSubscription.setOrderId(orderId);
                    beforeUserSubscription.setOrderPrice(orderPrice);
                    beforeUserSubscription.setSubscribeDuration(subscribeDuration);
                    beforeUserSubscription.setStartDate(startTime);
                    beforeUserSubscription.setExpireDate(cal.getTime());
                    beforeUserSubscription.setBenefitList(entity.getBenefitList());
                    beforeUserSubscription.setCreatedAt(null);
                    beforeUserSubscription.setUpdatedAt(null);
                    userSubscriptionService.updateById(beforeUserSubscription);
                    //发送订阅消息
                    messageService.sendSubscriptionMessage(userSubscriptionEntity);
                    return beforeUserSubscription;
                }else { // 免费转付费会员
                    TierEntity beforeTier  = tierService.getById(beforeUserSubscription.getMemberTierId());
                    if (Long.valueOf(0l).equals(beforeTier.getMonthPrice())){

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

                        beforeUserSubscription.setOrderId(orderId);
                        beforeUserSubscription.setOrderPrice(orderPrice);
                        beforeUserSubscription.setSubscribeDuration(subscribeDuration);
                        beforeUserSubscription.setStartDate(startTime);
                        beforeUserSubscription.setExpireDate(cal.getTime());
                        beforeUserSubscription.setBenefitList(entity.getBenefitList());
                        beforeUserSubscription.setCreatedAt(null);
                        beforeUserSubscription.setUpdatedAt(null);
                        userSubscriptionService.updateById(beforeUserSubscription);
                        //发送订阅消息
                        messageService.sendSubscriptionMessage(userSubscriptionEntity);
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
            }else if (Long.valueOf(0l).equals(entity.getMonthPrice())){
                cal.add(Calendar.YEAR,30);
            }

            userSubscriptionEntity.setSubscriberId(getUId());
            userSubscriptionEntity.setStartDate(new Date());
            userSubscriptionEntity.setExpireDate(cal.getTime());
            userSubscriptionEntity.setBenefitList(entity.getBenefitList());
            userSubscriptionEntity.setOrderId(orderId);

            userSubscriptionService.save(userSubscriptionEntity);
            //发送订阅消息
            messageService.sendSubscriptionMessage(userSubscriptionEntity);
            return userSubscriptionEntity;

        }else {
            throw  new BaseException(500,"TierId="+memberTierId +" 不存在。");
        }

    }

}