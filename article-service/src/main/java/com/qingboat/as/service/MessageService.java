package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingboat.as.entity.*;

public interface MessageService  {

    void asyncSendMessage(MessageEntity msg);

    void asyncSendSubscriptionMessage(UserSubscriptionEntity userSubscriptionEntity);


    void asyncSendCommentMessage(ArticleCommentEntity articleCommentEntity);

    void asyncSendReplyCommentMessage(ReplyCommentEntity replyCommentEntity);

    void asyncSendStarMessage(String articleId, Long starCount);

    IPage<MessageEntity> list(Long toUserId, Integer msgType ,Integer pageIndex,Integer pageSize);

    MessageEntity getLastUnReadMessage(Long toUserId,Integer msgType);

    Integer getUnreadMessageCount(Long toUserId, Integer msgType);

}
