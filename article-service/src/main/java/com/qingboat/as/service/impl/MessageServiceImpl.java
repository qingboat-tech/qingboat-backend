package com.qingboat.as.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.api.WxMessageService;
import com.qingboat.as.api.WxTokenService;
import com.qingboat.as.dao.MessageDao;
import com.qingboat.as.entity.*;
import com.qingboat.as.filter.AuthFilter;
import com.qingboat.as.service.*;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    WxTokenService wxTokenService;

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    UserWechatService userWechatService;

    @Value("${business-domain}")
    private String businessDomain;

    @Value("${wx-msg-template.data-update}")
    private String dataUpdateTemplate;

    @Value("${wx-msg-template.new-order}")
    private String newOrderTemplate;

    @Value("${wx-msg-template.answer-result}")
    private String answerResultTemplate;

    @Override
    @Async
    public void asyncSendMessage(MessageEntity msg) {
        this.save(msg);
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
        msg.setExtData("tierEntityName",userSubscriptionEntity.getMemberTierName());
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

        // 发订阅者微信消息
        String subscribeIdStr = String.valueOf(subscribeUser.getUserId());
        String sec = AuthFilter.getSecret(subscribeIdStr);
        String token =  wxTokenService.getWxUserToken(sec, subscribeIdStr);
        JSONObject body = new JSONObject();
        JSONObject data = new JSONObject();

        // 找到发送者的微信openId
        QueryWrapper<UserWechatEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserWechatEntity::getUserId,subscribeUser.getUserId());
        UserWechatEntity userWechatEntity = userWechatService.getOne(queryWrapper);
        if (userWechatEntity == null){
            throw new BaseException(500,"订阅者没有微信openId,没法发消息");
        }

        body.put("touser",userWechatEntity.getOpenId());                    // 发给谁
        body.put("template_id",this.dataUpdateTemplate);                // 那个模板
        body.put("url",this.businessDomain+"/mysubscription");             // 打开地址
        body.put("data",data);

        data.put("first", JSON.parse("{'value': '订阅成功啦！'}"));
        data.put("keyword1", JSON.parse("{'value': '"+createUser.getNickname()+"'}"));
        data.put("keyword2", JSON.parse("{'value': '"+ DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD) +"'}"));
        data.put("remark", JSON.parse("{'value': '感谢您订阅,快来开启学习成长之旅吧！'}"));

        log.info( " request: " +body);
        Object obj = wxMessageService.sendMessage(token,body);
        log.info( " response: " +obj);

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
        msg.setExtData("tierEntityName",userSubscriptionEntity.getMemberTierName());
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

        // 给创作者发微信消息
        String creatorIdStr = String.valueOf(createUser.getUserId());
        JSONObject body2 = new JSONObject();
        JSONObject data2 = new JSONObject();
        // 商品
        data2.put("keyword1", JSON.parse("{'value': '"+tierEntity.getTitle()+"'}"));

        DecimalFormat df = new DecimalFormat("0.00");
        String price =df.format((double)userSubscriptionEntity.getOrderPrice()/100);
        // 金额
        data2.put("keyword2", JSON.parse("{'value': '"+price+" 元'}"));

        // 购买人昵称
        data2.put("keyword3", JSON.parse("{'value': '"+subscribeUser.getNickname() +"'}"));

        // 交易时间
        data2.put("keyword4", JSON.parse("{'value': '"+ DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD) +"'}"));

        // 交易流水号
        data2.put("keyword5", JSON.parse("{'value': '"+userSubscriptionEntity.getOrderNo() +"'}"));

        // 备注
        data2.put("remark", JSON.parse("{'value': '加油来创作下一篇爆款吧！'}"));


        // 找到发送者的微信openId
        QueryWrapper<UserWechatEntity> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.lambda().eq(UserWechatEntity::getUserId,createUser.getUserId());
        UserWechatEntity userWechatEntity2 = userWechatService.getOne(queryWrapper2);
        if (userWechatEntity2 == null){
            throw new BaseException(500,"creator没有微信openId,没法发消息");
        }

        body2.put("touser",userWechatEntity2.getOpenId());                   // 发给谁
        body2.put("template_id",this.newOrderTemplate);                      // 那个模板
        body2.put("url", this.businessDomain+"/creatorcenter/subscribe");             // 打开地址
        body2.put("data",data2);



        log.info( " request: " +body2);
        Object obj2 = wxMessageService.sendMessage(token,body2);
        log.info( " response: " +obj2);




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


        // 给creator发微信消息（提问结果通知）
        String creatorIdStr = String.valueOf(Long.parseLong(articleEntity.getAuthorId()));
        String sec = AuthFilter.getSecret(creatorIdStr);
        String token =  wxTokenService.getWxUserToken(sec, creatorIdStr);
        JSONObject body = new JSONObject();
        JSONObject data = new JSONObject();

        // 找到发送者的微信openId
        QueryWrapper<UserWechatEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserWechatEntity::getUserId,Long.parseLong(articleEntity.getAuthorId()));
        UserWechatEntity userWechatEntity = userWechatService.getOne(queryWrapper);
        if (userWechatEntity == null){
            throw new BaseException(500,"订阅者没有微信openId,没法发消息");
        }

        body.put("touser",userWechatEntity.getOpenId());                    // 发给谁
        body.put("template_id",this.answerResultTemplate);                   // 那个模板
        body.put("url",this.businessDomain+"/");             // 打开地址
        body.put("data",data);

        data.put("first", JSON.parse("{'value': '有新评论啦！'}"));
        // 回复者
        data.put("keyword1", JSON.parse("{'value': '"+articleCommentEntity.getNickName()+"'}"));
        // 回复时间
        data.put("keyword2", JSON.parse("{'value': '"+ DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD) +"'}"));
        // 回复内容
        data.put("keyword3", JSON.parse("{'value': '"+articleCommentEntity.getContent()+"'}"));
        // remark
        // data.put("remark", JSON.parse("{'value': '感谢您订阅,快来开启学习成长之旅吧！'}"));

        log.info( " request: " +body);
        Object obj = wxMessageService.sendMessage(token,body);
        log.info( " response: " +obj);
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

        // 给creator发微信消息（提问结果通知）
        String creatorIdStr = String.valueOf(Long.parseLong(articleEntity.getAuthorId()));
        String sec = AuthFilter.getSecret(creatorIdStr);
        String token =  wxTokenService.getWxUserToken(sec, creatorIdStr);
        JSONObject body = new JSONObject();
        JSONObject data = new JSONObject();

        // 找到发送者的微信openId
        QueryWrapper<UserWechatEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserWechatEntity::getUserId,Long.parseLong(articleEntity.getAuthorId()));
        UserWechatEntity userWechatEntity = userWechatService.getOne(queryWrapper);
        if (userWechatEntity == null){
            throw new BaseException(500,"订阅者没有微信openId,没法发消息");
        }

        body.put("touser",userWechatEntity.getOpenId());                    // 发给谁
        body.put("template_id",this.answerResultTemplate);                   // 那个模板
        body.put("url",this.businessDomain+"/");             // 打开地址
        body.put("data",data);

        data.put("first", JSON.parse("{'value': '有新回复啦！'}"));
        // 回复者
        data.put("keyword1", JSON.parse("{'value': '"+replyCommentEntity.getNickName()+"'}"));
        // 回复时间
        data.put("keyword2", JSON.parse("{'value': '"+ DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD) +"'}"));
        // 回复内容
        data.put("keyword3", JSON.parse("{'value': '"+replyCommentEntity.getContent()+"'}"));
        // remark
        // data.put("remark", JSON.parse("{'value': '感谢您订阅,快来开启学习成长之旅吧！'}"));

        log.info( " request: " +body);
        Object obj = wxMessageService.sendMessage(token,body);
        log.info( " response: " +obj);

    }

    @Override
    @Async
    public void asyncSendStarMessage(String articleId, Long starCount) {
//        if (starCount %10 != 0){
//            return;
//        }
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

        // 给creator发点赞
        String creatorIdStr = String.valueOf(Long.parseLong(articleEntity.getAuthorId()));
        String sec = AuthFilter.getSecret(creatorIdStr);
        String token =  wxTokenService.getWxUserToken(sec, creatorIdStr);
        JSONObject body = new JSONObject();
        JSONObject data = new JSONObject();

        // 找到发送者的微信openId
        QueryWrapper<UserWechatEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserWechatEntity::getUserId,Long.parseLong(articleEntity.getAuthorId()));
        UserWechatEntity userWechatEntity = userWechatService.getOne(queryWrapper);
        if (userWechatEntity == null){
            throw new BaseException(500,"订阅者没有微信openId,没法发消息");
        }

        body.put("touser",userWechatEntity.getOpenId());                    // 发给谁
        body.put("template_id",this.dataUpdateTemplate);                   // 那个模板
        body.put("url",this.businessDomain+"/");             // 打开地址
        body.put("data",data);

        data.put("first", JSON.parse("{'value': '有新点赞啦！'}"));
        // 回复者
        // TODO: 把小鲸改成真是用户
        data.put("keyword1", JSON.parse("{'value': '小鲸'}"));
        // 回复时间
        data.put("keyword2", JSON.parse("{'value': '"+ DateUtil.parseDateToStr(new Date(),DateUtil.DATE_FORMAT_YYYY_MM_DD) +"'}"));
        // remark
         data.put("remark", JSON.parse("{'value': '快来和你的粉丝互动吧！'}"));

        log.info( " request: " +body);
        Object obj = wxMessageService.sendMessage(token,body);
        log.info( " response: " +obj);

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
