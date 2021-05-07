package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.*;
import com.qingboat.as.service.*;
import com.qingboat.as.vo.ArticleCommentVo;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping(value = "/comment")
@Slf4j
public class ArticleCommentController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private ArticleCommentService articleCommentService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    UserSubscriptionService userSubscriptionService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private MessageService messageService;

    // 评论
    @PostMapping(value = "/create")
    @ResponseBody
    public ArticleCommentEntity comment(@Valid @RequestBody ArticleCommentVo articleCommentVo){
        Long uid = getUId();
        if (checkCommentBenefit(articleCommentVo.getArticleId()) ){
            ArticleCommentEntity articleCommentEntity = new ArticleCommentEntity();
            articleCommentEntity.setArticleId(articleCommentVo.getArticleId());
            articleCommentEntity.setContent(articleCommentVo.getContent());
            articleCommentEntity.setUserId(uid);

            UserEntity userOperate = userService.findByUserId(uid);
            articleCommentEntity.setHeadImgUrl(userOperate.getHeadimgUrl());
            articleCommentEntity.setNickName(userOperate.getNickname());

            articleCommentEntity =  articleCommentService.addArticleComment(articleCommentEntity);

            //发送评论消息
            messageService.asyncSendCommentMessage(articleCommentEntity);

            return articleCommentEntity;
        }
        throw new BaseException(500,"该用户没有评论权限");
    }

    // 删除
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public Boolean delComment(@Valid @RequestBody ArticleCommentVo articleCommentVo) {
        return articleCommentService.removeArticleComment(
                articleCommentVo.getArticleId(),
                articleCommentVo.getCommentId(),
                getUId());
    }

    // reply列表
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<ArticleCommentEntity> list(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,@RequestParam(value = "pageSize",required = false) Integer pageSize, @RequestParam("articleId") String articleId) {
        return articleCommentService.findArticleComment(articleId,pageIndex,pageSize);
    }


    private boolean checkCommentBenefit(String articleId){
        ArticleEntity articleEntity = articleService.findArticleById(articleId);
        if (articleEntity == null){
            throw  new BaseException(500,"评论的文章不存在");
        }
        boolean can = false;
        if (getUIdStr().equals(articleEntity.getAuthorId())){
            return true;
        }else {
            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(UserSubscriptionEntity::getCreatorId ,Long.parseLong(articleEntity.getAuthorId()))
                    .eq(UserSubscriptionEntity::getSubscriberId,getUId())
                    .le(UserSubscriptionEntity::getExpireDate,new Date());
            UserSubscriptionEntity userSubscriptionEntity = userSubscriptionService.getOne(queryWrapper);
            if (userSubscriptionEntity!=null){
                for (BenefitEntity benefitEntity :userSubscriptionEntity.getBenefitList()) {
                    if ("COMMENT".equals(benefitEntity.getKey())){
                       return true;
                    }
                }
            }
        }
        throw new BaseException(500,"该用户没有评论权限");
    }



}
