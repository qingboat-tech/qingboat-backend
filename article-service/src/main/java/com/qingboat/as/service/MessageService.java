package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingboat.as.dao.MessageDao;
import com.qingboat.as.entity.*;

import java.util.List;
import java.util.Map;

public interface MessageService {

    void asyncSendMessage(MessageEntity msg);

    void asyncSendSubscriptionMessage(UserSubscriptionEntity userSubscriptionEntity);


    void asyncSendCommentMessage(ArticleCommentEntity articleCommentEntity);

    void asyncSendReplyCommentMessage(ReplyCommentEntity replyCommentEntity);

    void asyncSendStarMessage(String articleId, Long userId, Long starCount);

    IPage<MessageEntity> list(Long toUserId, Integer msgType ,Integer pageIndex,Integer pageSize);

    MessageEntity getLastUnReadMessage(Long toUserId,Integer msgType);

    Integer getUnreadMessageCount(Long toUserId, Integer msgType);

    List<Map<String,Integer>> getUnreadGroupbyMessageCount(Long toUserId);


}
