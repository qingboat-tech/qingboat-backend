package com.qingboat.as.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.api.TradeService;
import com.qingboat.api.vo.CreatorBillVo;
import com.qingboat.as.dao.UserSubscriptionDao;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.TierEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import com.qingboat.as.filter.AuthFilter;
import com.qingboat.as.service.MessageService;
import com.qingboat.as.service.TierService;
import com.qingboat.as.service.UserSubscriptionService;
import com.qingboat.base.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserSubscriptionServiceImpl extends ServiceImpl<UserSubscriptionDao, UserSubscriptionEntity> implements UserSubscriptionService {


    @Autowired
    private TradeService tradeService;

    @Autowired
    private TierService tierService;

    @Autowired
    private MessageService messageService;

    @Override
    public Object createBillAndUpdateWallet(UserSubscriptionEntity userSubscriptionEntity) {
        // 给creator添加收益记录
        CreatorBillVo creatorBillVo = new CreatorBillVo();
        creatorBillVo.setCreatorId(userSubscriptionEntity.getCreatorId());
        creatorBillVo.setBillType(1);
        String typeChinese = null;
        if ("year".equals(userSubscriptionEntity.getSubscribeDuration())){
            typeChinese = "年";
        }
        if ("month".equals(userSubscriptionEntity.getSubscribeDuration())){
            typeChinese = "月";
        }
        creatorBillVo.setAmount(1L*userSubscriptionEntity.getOrderPrice());
        creatorBillVo.setOrderNo(userSubscriptionEntity.getOrderNo());
        creatorBillVo.setBillTitle("新增"+typeChinese+"度订阅会员");

        String getCreatorIdStr = String.valueOf(userSubscriptionEntity.getCreatorId());
        String sec = AuthFilter.getSecret(getCreatorIdStr);

        tradeService.createBillAndUpdateWallet(creatorBillVo, sec, getCreatorIdStr);
        return null;

    }

    @Override
    public UserSubscriptionEntity subscribe(UserSubscriptionEntity userSubscriptionEntity) {
        Long  creatorId = userSubscriptionEntity.getCreatorId();
        Long subscriberId = userSubscriptionEntity.getSubscriberId();
        Long  memberTierId = userSubscriptionEntity.getMemberTierId();
        Long orderId = userSubscriptionEntity.getOrderId();
        String orderNo = userSubscriptionEntity.getOrderNo();
        Integer orderPrice = userSubscriptionEntity.getOrderPrice();
        String subscribeDuration = userSubscriptionEntity.getSubscribeDuration();

        log.info(userSubscriptionEntity.toString());
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

            List<UserSubscriptionEntity> beforeSubscriptionList= this.list(queryWrapper);
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
                    this.updateById(beforeUserSubscription);

                    // 给creator添加收益记录
                    this.createBillAndUpdateWallet(beforeUserSubscription);

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
                        this.updateById(beforeUserSubscription);
                        //发送订阅消息
                        messageService.asyncSendSubscriptionMessage(beforeUserSubscription);

                        // 给creator添加收益记录
                        this.createBillAndUpdateWallet(beforeUserSubscription);
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

            userSubscriptionEntity.setSubscriberId(subscriberId);
            userSubscriptionEntity.setStartDate(new Date());
            userSubscriptionEntity.setExpireDate(cal.getTime());
            userSubscriptionEntity.setBenefitList(thisSubscriptionTier.getBenefitList());
            userSubscriptionEntity.setOrderId(orderId);
            userSubscriptionEntity.setOrderNo(orderNo);

            this.save(userSubscriptionEntity);

            // 给creator添加收益记录
            this.createBillAndUpdateWallet(userSubscriptionEntity);

            //发送订阅消息
            messageService.asyncSendSubscriptionMessage(userSubscriptionEntity);
            return userSubscriptionEntity;

        }else {
            throw  new BaseException(500,"TierId="+memberTierId +" 不存在。");
        }
    }
}
