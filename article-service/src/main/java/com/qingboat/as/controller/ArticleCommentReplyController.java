package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.ReplyCommentEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.vo.ArticleCommentDelVo;
import com.qingboat.as.vo.ArticleCommentReplyVo;
import com.qingboat.as.vo.ArticleCommentVo;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/reply")
@Slf4j
public class ArticleCommentReplyController {


    @Autowired
    private UserService userService;

    @Autowired
    private ArticleCommentService articleCommentService;

    @Autowired
    private FeishuService feishuService;

    // 评论
    @PostMapping(value = "/create")
    @ResponseBody
    public ReplyCommentEntity createReply(@Valid @RequestBody ArticleCommentReplyVo articleCommentReplyVo){
        String uidString = getUId();
        Long uid = Long.parseLong(uidString);

        ReplyCommentEntity replyCommentEntity = new ReplyCommentEntity();
        replyCommentEntity.setArticleId(articleCommentReplyVo.getArticleId());
        replyCommentEntity.setContent(articleCommentReplyVo.getContent());
        replyCommentEntity.setCommentId(articleCommentReplyVo.getCommentId());

        replyCommentEntity.setUserId(uid);

        UserEntity userOperate = userService.findByUserId(uid);
        replyCommentEntity.setHeadImgUrl(userOperate.getHeadimgUrl());
        replyCommentEntity.setNickName(userOperate.getNickname());

        return articleCommentService.replyComment(replyCommentEntity);
    }

    // 删除评论
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public Boolean delReplyComment(@Valid @RequestBody ArticleCommentReplyVo articleCommentReplyVo) {
        return articleCommentService.removeReplyComment(
                articleCommentReplyVo.getArticleId(),
                articleCommentReplyVo.getReplyId(),
                Long.parseLong(getUId())
                );
    }

    // 评论
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<ReplyCommentEntity> list(@RequestParam("pageIndex") int pageIndex, @RequestParam("commentId") long commentId, @RequestParam("articleId") String articleId) {
        return articleCommentService.findReplyComment(articleId, commentId, pageIndex);
    }



    private String getUId(){
        String StrUid = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof  ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            Long uid = (Long) request.getAttribute("UID");
            if (uid == null){
                throw new BaseException(401,"AUTH_ERROR");
            }
            StrUid = String.valueOf(uid);
        }
        return StrUid;
    }



}
