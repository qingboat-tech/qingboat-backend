package com.qingboat.as.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.MessageDao;
import com.qingboat.as.entity.*;
import com.qingboat.as.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageDao, MessageEntity> implements MessageService {

    @Autowired
    private UserService userService;

    @Autowired
    private TierService tierService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleCommentService articleCommentService;

    @Override
    @Async
    public void asyncSendMessage(MessageEntity msg) {

        log.info("===sendMessage===" + JSON.toJSONString(msg));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("===sendMessage=== end " );

    }

    @Override
    @Async
    public void asyncSendSubscriptionMessage(UserSubscriptionEntity userSubscriptionEntity) {
        // 产生两条消息，一个给订阅者，一个给创作者
        //1、发给订阅者
        UserEntity subscribeUser = userService.findByUserId(userSubscriptionEntity.getSubscriberId());
        UserEntity createUser =  userService.findByUserId(userSubscriptionEntity.getCreatorId());
        TierEntity tierEntity = tierService.getById(userSubscriptionEntity.getMemberTierId());

        MessageEntity msg = new MessageEntity();
        msg.setMsgType(MessageEntity.SUBSCRIBE_MSG);
        msg.setMsgTitle("感谢您成功订阅："+tierEntity.getTitle());
        msg.setTo(subscribeUser.getUserId());
        msg.setSenderId(createUser.getUserId());
        msg.setSenderName(createUser.getNickname());
        msg.setSenderImgUrl(createUser.getHeadimgUrl());
        msg.setMsgLink(null); // TODO
        msg.setExtData("tierEntityId",userSubscriptionEntity.getMemberTierId());
        msg.setExtData("orderId",userSubscriptionEntity.getOrderId());
        msg.setExtData("orderPrice",userSubscriptionEntity.getOrderPrice());
        msg.setExtData("subscribeDuration",userSubscriptionEntity.getSubscribeDuration());
        msg.setExtData("benefitList",tierEntity.getBenefitList());
        msg.setExtData("creatorId",createUser.getUserId());
        msg.setExtData("creatorNickName",createUser.getNickname());
        msg.setExtData("creatorHeadImgUrl",createUser.getHeadimgUrl());
        msg.setExtData("subscriberId",subscribeUser.getUserId());
        msg.setExtData("subscriberNickName",subscribeUser.getNickname());
        msg.setExtData("subscriberHeadImgUrl",subscribeUser.getHeadimgUrl());
        this.save(msg);

        //2、发给创作者
        msg = new MessageEntity();
        msg.setMsgType(MessageEntity.SUBSCRIBE_MSG);
        msg.setMsgTitle(subscribeUser.getNickname()+" 已成功订阅您的"+tierEntity.getTitle());
        msg.setTo(createUser.getUserId());
        msg.setSenderId(subscribeUser.getUserId());
        msg.setSenderName(subscribeUser.getNickname());
        msg.setSenderImgUrl(subscribeUser.getHeadimgUrl());
        msg.setMsgLink(null); // TODO
        msg.setExtData("tierEntityId",userSubscriptionEntity.getMemberTierId());
        msg.setExtData("orderId",userSubscriptionEntity.getOrderId());
        msg.setExtData("orderPrice",userSubscriptionEntity.getOrderPrice());
        msg.setExtData("subscribeDuration",userSubscriptionEntity.getSubscribeDuration());
        msg.setExtData("benefitList",tierEntity.getBenefitList());
        msg.setExtData("creatorId",createUser.getUserId());
        msg.setExtData("creatorNickName",createUser.getNickname());
        msg.setExtData("creatorHeadImgUrl",createUser.getHeadimgUrl());
        msg.setExtData("subscriberId",subscribeUser.getUserId());
        msg.setExtData("subscriberNickName",subscribeUser.getNickname());
        msg.setExtData("subscriberHeadImgUrl",subscribeUser.getHeadimgUrl());
        this.save(msg);
    }

    @Override
    @Async
    public void asyncSendCommentMessage(ArticleCommentEntity articleCommentEntity) {

        ArticleEntity articleEntity = articleService.findBaseInfoById(articleCommentEntity.getArticleId());

        MessageEntity msg = new MessageEntity();
        msg.setMsgType(MessageEntity.COMMENT_MSG);
        msg.setMsgTitle(articleCommentEntity.getNickName()+" 评论了您的文章《"+articleEntity.getTitle()+"》");
        msg.setTo(Long.parseLong(articleEntity.getAuthorId()));
        msg.setSenderId(articleCommentEntity.getUserId());
        msg.setSenderName(articleCommentEntity.getNickName());
        msg.setSenderImgUrl(articleCommentEntity.getHeadImgUrl());
        msg.setMsgLink(null); // TODO
        msg.setExtData("articleId",articleEntity.getId());
        msg.setExtData("articleTitle",articleEntity.getTitle());
        msg.setExtData("articleCommentId",articleCommentEntity.getId());
        msg.setExtData("articleCommentContent",articleCommentEntity.getContent());
        msg.setExtData("commentUserId",articleCommentEntity.getUserId());
        msg.setExtData("commentUserNickName",articleCommentEntity.getNickName());
        msg.setExtData("commentUserHeadImgUrl",articleCommentEntity.getHeadImgUrl());

        this.save(msg);
    }

    @Override
    @Async
    public void asyncSendReplyCommentMessage(ReplyCommentEntity replyCommentEntity) {

        ArticleCommentEntity commentEntity = articleCommentService.findArticleComment(replyCommentEntity.getArticleId(),replyCommentEntity.getCommentId());
        ArticleEntity articleEntity = articleService.findBaseInfoById(replyCommentEntity.getArticleId());

        MessageEntity msg = new MessageEntity();
        msg.setMsgType(MessageEntity.REPLY_MSG);
        msg.setMsgTitle(replyCommentEntity.getNickName()+" 回复了您一个评论");
        msg.setTo(commentEntity.getUserId());

        msg.setSenderId(replyCommentEntity.getUserId());
        msg.setSenderName(replyCommentEntity.getNickName());
        msg.setSenderImgUrl(replyCommentEntity.getHeadImgUrl());
        msg.setMsgLink(null); // TODO
        msg.setExtData("articleId",replyCommentEntity.getArticleId());
        msg.setExtData("articleTitle",articleEntity.getTitle());
        msg.setExtData("replyCommentId",replyCommentEntity.getId());
        msg.setExtData("replyCommentContent",replyCommentEntity.getContent());
        msg.setExtData("replyUserId",replyCommentEntity.getUserId());
        msg.setExtData("replyUserNickName",replyCommentEntity.getNickName());
        msg.setExtData("replyUserHeadImgUrl",replyCommentEntity.getHeadImgUrl());

        this.save(msg);

    }

    @Override
    @Async
    public void asyncSendStarMessage(String articleId, Long starCount) {
        if (starCount %10 != 0){
            return;
        }
        //每增加10个赞，发一条点赞通知给创作者
        ArticleEntity articleEntity = articleService.findBaseInfoById(articleId);

        MessageEntity msg = new MessageEntity();
        msg.setMsgType(MessageEntity.STAR_MSG);
        msg.setMsgTitle("《"+articleEntity.getTitle()+"》已经获得"+starCount+"个赞！" );
        msg.setTo(Long.parseLong(articleEntity.getAuthorId()));
        msg.setSenderId(0l);
        msg.setSenderName("小鲸");
        msg.setSenderImgUrl("https://m.qingboat.com/static/admin/img/gis/move_vertex_on.svg");
        msg.setMsgLink(null); // TODO
        msg.setExtData("articleId",articleEntity.getId());
        msg.setExtData("articleTitle",articleEntity.getTitle());
        msg.setExtData("starCount",starCount);

        this.save(msg);

    }

    @Override
    public IPage<MessageEntity> list(Long toUserId, Integer msgType,Integer pageIndex,Integer pageSize) {
        MessageEntity entity = new MessageEntity();
        entity.setTo(toUserId);
        entity.setMsgType(msgType);
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByDesc("created_at");

        if (pageIndex == null || pageIndex<0){
            pageIndex = 1;
        }
        if (pageSize == null || pageSize<1){
            pageSize = 10;
        }

        IPage<MessageEntity> page = new Page<>(pageIndex, pageSize);
        page = this.page(page,queryWrapper);
        //同时更新为已读状态
        if (page.getRecords()!=null  && !page.getRecords().isEmpty()){
            List<MessageEntity> updateList = new ArrayList<>();
            for (MessageEntity messageEntity:page.getRecords()){
                if(messageEntity.getReadFlag() == 0){
                    MessageEntity msg = new MessageEntity();
                    msg.setId(messageEntity.getId());
                    msg.setTo(messageEntity.getTo());
                    msg.setReadFlag(1);
                    updateList.add(msg);
                }
            }
            if (!updateList.isEmpty()){
                this.updateBatchById(updateList,10);
            }
        }

        return page;
    }

    @Override
    public MessageEntity getLastUnReadMessage(Long toUserId, Integer msgType) {
        MessageEntity entity = new MessageEntity();
        entity.setTo(toUserId);
        entity.setMsgType(msgType);
        entity.setReadFlag(0);

        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByDesc("created_at").last(" LIMIT 1");

        return this.getOne(queryWrapper);
    }

    @Override
    public Integer getUnreadMessageCount(Long toUserId, Integer msgType) {
        MessageEntity entity = new MessageEntity();
        entity.setTo(toUserId);
        entity.setMsgType(msgType);
        entity.setReadFlag(0);
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return this.count(queryWrapper);
    }

}
