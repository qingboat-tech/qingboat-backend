package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.InviteDao;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.InviteEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.service.InviteService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;
import com.qingboat.as.utils.RedisUtil;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.utils.BASE64Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class InviteServiceImpl extends ServiceImpl<InviteDao, InviteEntity> implements InviteService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;


    @Override
    public String buildInviteKey(String articleId, Long operatorId) {

        ArticleEntity articleEntity = articleService.findBaseInfoById(articleId);
        if (articleEntity  == null){
            throw  new BaseException(500,"推荐试读的文章不存在");
        }
        if (articleEntity.getAuthorId().equals(String.valueOf(operatorId))){
            //作者邀请试读
        }else {
            //检查该用户是否已订阅
            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(UserSubscriptionEntity::getSubscriberId,operatorId)
                    .eq(UserSubscriptionEntity::getCreatorId,Long.parseLong(articleEntity.getAuthorId()))
                    .ge(UserSubscriptionEntity::getExpireDate,new Date());
            int count = userSubscriptionService.count(queryWrapper);
            if (count == 0){
                throw new BaseException(500,"未订阅用户，禁止推荐试读分享");
            }
        }
        String inviteKey = BASE64Util.encode(articleId+"#"+ operatorId);
        return inviteKey;
    }

    @Override
    public boolean addInvite(String inviteKey, Long operatorId) {

        String decode = BASE64Util.decode(inviteKey);
        if (!decode.contains("#")){
            throw new BaseException(500,"非法邀请码，不能领取哦");
        }

        try{
            redisUtil.synLock(inviteKey);

            QueryWrapper<InviteEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("invite_key",inviteKey);
            queryWrapper.eq("user_id",operatorId);
            int count = this.count(queryWrapper);
            if (count>0){
                throw new BaseException(500,"你已经领取邀请码，不能重复领取哦");
            }

            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("invite_key",inviteKey);
            count = this.count(queryWrapper);
            if (count>= READ_LIMIT){
                throw new BaseException(505,"本次分享超出限量阅读，下次手速要快哦");
            }
            UserEntity user = userService.findByUserId(operatorId);

            InviteEntity inviteEntity = new InviteEntity();
            inviteEntity.setInviteKey(inviteKey);
            inviteEntity.setUserId(operatorId);
            inviteEntity.setNickname(user.getNickname());
            inviteEntity.setHeadimgUrl(user.getHeadimgUrl());

            this.save(inviteEntity);
        }finally {
            redisUtil.unLock(inviteKey);
        }
        return true;
    }

    @Override
    public boolean checkAndaddInvite(String inviteKey, Long operatorId) {
        boolean rst = false;
        try {
            rst = this.addInvite(inviteKey,operatorId);
        }catch (BaseException e){
            if ("你已经领取邀请码，不能重复领取哦".equals(e.getMessage())){
                return true;
            }
            throw e;
        }
        return rst;
    }

    @Override
    public boolean hasTakeInviteKey(String inviteKey, Long operatorId) {

        String decode = BASE64Util.decode(inviteKey);
        if (!decode.contains("#")){
            throw new BaseException(500,"非法邀请码，不能领取哦");
        }
        QueryWrapper<InviteEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("invite_key",inviteKey);
        queryWrapper.eq("user_id",operatorId);
        int count = this.count(queryWrapper);
        if (count>0){
            return true;
        }
        return false;
    }

    @Override
    public List<InviteEntity> getInviteUser(String inviteKey) {
        QueryWrapper<InviteEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("invite_key",inviteKey);
        return this.list(queryWrapper);
    }
}
