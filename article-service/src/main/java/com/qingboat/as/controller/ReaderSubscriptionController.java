package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.entity.*;

import com.qingboat.as.service.TierService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;

import com.qingboat.base.api.FeishuService;
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




    //=======================针对 reader 接口=============================
    /**
     * 获取某creator的全部会员等级的列表接口
     */
    @GetMapping(value = "/tiers")
    @ResponseBody
    public List<TierEntity> getTierEntityList(@Valid @RequestParam("creatorId") Long creatorId) {

        TierEntity TierEntity = new TierEntity();
        TierEntity.setCreatorId(creatorId);

        QueryWrapper<TierEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(TierEntity);

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
        //TODO 需要返回creator 昵称和图像

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
        TierEntity entity = tierService.getById(memberTierId);

//        if (entity !=null){
//            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
//            UserSubscriptionEntity queryEntity = new UserSubscriptionEntity();
//            queryEntity.setSubscriberId(subscriberId);
//            queryEntity.setCreatorId(creatorId);
//            UserSubscriptionEntity beforeUserSubscriptionEntity = userSubscriptionService.getOne(queryWrapper);
//
//            if (beforeUserSubscriptionEntity !=null){
//                if (entity.getId().equals( beforeUserSubscriptionEntity.getMemberTierId() )){
//                    //续订逻辑
//                    if (beforeUserSubscriptionEntity.getOrderId().equals(0L)){ // 续订免费套餐，直接返回
//                        return userSubscriptionEntity;
//                    }else{
//                        Date startTime = beforeUserSubscriptionEntity.getExpireDate();
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(startTime);
//
//                        cal.add(Calendar.DAY_OF_MONTH,entity.getDuration());
//
//                        beforeUserSubscriptionEntity.setOrderId(orderId);
//                        beforeUserSubscriptionEntity.setStartDate(startTime);
//                        beforeUserSubscriptionEntity.setExpireDate(cal.getTime());
//                        beforeUserSubscriptionEntity.setCreatedAt(null);
//                        beforeUserSubscriptionEntity.setUpdatedAt(null);
//                        userSubscriptionService.updateById(beforeUserSubscriptionEntity);
//                    }
//
//                }else {
//                    // 更改套餐，查询上次订阅的Tier
//                    TierEntity beforeEntity = tierService.getById(beforeUserSubscriptionEntity.getMemberTierId());
//                    beforeUserSubscriptionEntity.setMemberTierId(beforeEntity.getId());
//
//                    if(beforeUserSubscriptionEntity.getOrderId().equals(0L)){ // 上次是免费订阅的
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(new Date());
//                        cal.add(Calendar.DAY_OF_MONTH,entity.getDuration());
//                        Date expireDate = cal.getTime();
//
//                        beforeUserSubscriptionEntity.setOrderId(orderId);
//                        beforeUserSubscriptionEntity.setMemberTierId(memberTierId);
//                        beforeUserSubscriptionEntity.setStartDate(new Date());
//                        beforeUserSubscriptionEntity.setExpireDate(expireDate);
//
//                        beforeUserSubscriptionEntity.setCreatedAt(null);
//                        beforeUserSubscriptionEntity.setUpdatedAt(null);
//                        userSubscriptionService.updateById(beforeUserSubscriptionEntity);
//
//                    }else {
//                        if (orderId.equals( 0l)){ //本次改为免费订阅
//                            if (beforeUserSubscriptionEntity.getExpireDate().after(new Date())){
//                                throw new BaseException(500,"上次订阅还未到期，不能转免费订阅");
//                            }
//                            Calendar cal = Calendar.getInstance();
//                            cal.setTime(new Date());
//                            cal.add(Calendar.YEAR,+30);
//                            Date expireDate = cal.getTime();
//
//                            beforeUserSubscriptionEntity.setOrderId(orderId);
//                            beforeUserSubscriptionEntity.setMemberTierId(memberTierId);
//                            beforeUserSubscriptionEntity.setExpireDate(expireDate);
//
//                            beforeUserSubscriptionEntity.setCreatedAt(null);
//                            beforeUserSubscriptionEntity.setUpdatedAt(null);
//                            userSubscriptionService.updateById(beforeUserSubscriptionEntity);
//
//                        }else {
//                            Date startTime = beforeUserSubscriptionEntity.getExpireDate();
//                            Calendar cal = Calendar.getInstance();
//                            cal.setTime(startTime);
//                            cal.add(Calendar.DAY_OF_MONTH,entity.getDuration());
//
//
//                            beforeUserSubscriptionEntity.setOrderId(orderId);
//                            beforeUserSubscriptionEntity.setMemberTierId(memberTierId);
//                            beforeUserSubscriptionEntity.setStartDate(startTime);
//                            beforeUserSubscriptionEntity.setExpireDate(cal.getTime());
//
//                            beforeUserSubscriptionEntity.setCreatedAt(null);
//                            beforeUserSubscriptionEntity.setUpdatedAt(null);
//                            userSubscriptionService.updateById(beforeUserSubscriptionEntity);
//
//                        }
//
//                    }
//
//                }
//            }else { //新订阅
//              //  userSubscriptionEntity
//
//            }
//
//        }

//        return userSubscriptionService.page(page,queryWrapper);

        return null;
    }



}
