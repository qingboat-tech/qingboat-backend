package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ReplyCommentEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.MessageService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.vo.ArticleCommentReplyVo;
import com.qingboat.base.api.FeishuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/reply")
@Slf4j
public class ArticleCommentReplyController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private ArticleCommentService articleCommentService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private MessageService messageService;

    // 评论
    @PostMapping(value = "/create")
    @ResponseBody
    public ReplyCommentEntity createReply(@Valid @RequestBody ArticleCommentReplyVo articleCommentReplyVo){
        String uidString = getUIdStr();
        Long uid = Long.parseLong(uidString);

        ReplyCommentEntity replyCommentEntity = new ReplyCommentEntity();
        replyCommentEntity.setArticleId(articleCommentReplyVo.getArticleId());
        replyCommentEntity.setContent(articleCommentReplyVo.getContent());
        replyCommentEntity.setCommentId(articleCommentReplyVo.getCommentId());
        replyCommentEntity.setReplyCount(0l);

        replyCommentEntity.setUserId(uid);

        UserEntity userOperate = userService.findByUserId(uid);
        replyCommentEntity.setHeadImgUrl(userOperate.getHeadimgUrl());
        replyCommentEntity.setNickName(userOperate.getNickname());

        replyCommentEntity = articleCommentService.replyComment(replyCommentEntity);

        //发送评论回复消息
        messageService.asyncSendReplyCommentMessage(replyCommentEntity);

        return replyCommentEntity;
    }

    // 删除评论
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public Boolean delReplyComment(@Valid @RequestBody ArticleCommentReplyVo articleCommentReplyVo) {
        return articleCommentService.removeReplyComment(
                articleCommentReplyVo.getArticleId(),
                articleCommentReplyVo.getReplyId(),
                Long.parseLong(getUIdStr())
                );
    }

    // 评论
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<ReplyCommentEntity> list(@RequestParam(value = "pageIndex",required = false) Integer pageIndex, @RequestParam(value = "pageSize",required = false) Integer pageSize,@RequestParam("commentId") long commentId, @RequestParam("articleId") String articleId) {
        return articleCommentService.findReplyComment(articleId, commentId, pageIndex,pageSize);
    }


}
