package com.qingboat.as.service;

import com.qingboat.as.entity.InviteEntity;
import com.qingboat.as.entity.TierEntity;

import java.util.List;

/**
 * 文章阅读邀请服务，限制每人只能分享5次
 */
public interface InviteService  {

    int READ_LIMIT = 5;

    /**
     * 创建邀请码
     */
    String buildInviteKey(String articleId,Long operatorId);

    /**
     * 添加邀请人
     */
    boolean addInvite(String inviteKey,Long operatorId);

    /**
     * 判断是否合法领取和限制，ok再添加邀请人
     */
    boolean checkAndaddInvite(String inviteKey,Long operatorId);

    /**
     * 判断是否领取
     */
    boolean hasTakeInviteKey(String inviteKey,Long operatorId);

    /**
     * 查询邀请人List
     */
    List<InviteEntity> getInviteUser(String inviteKey);

}
