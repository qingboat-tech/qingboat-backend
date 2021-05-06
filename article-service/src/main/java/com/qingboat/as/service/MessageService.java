package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingboat.as.entity.*;

public interface MessageService  {

    Boolean sendMessage(MessageEntity msg);

    void sendSubscriptionMessage(UserSubscriptionEntity userSubscriptionEntity);


    void sendCommentMessage(ArticleCommentEntity articleCommentEntity);

    void sendReplyCommentMessage(ReplyCommentEntity replyCommentEntity);

    void sendStarMessage(String articleId,Long starCount);

    IPage<MessageEntity> list(Long toUserId, Integer msgType ,int pageIndex);

    MessageEntity getLastUnReadMessage(Long toUserId,Integer msgType);

    Integer getUnreadMessageCount(Long toUserId, Integer msgType);

}
