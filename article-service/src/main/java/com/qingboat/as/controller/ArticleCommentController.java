package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.UserService;
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
@RequestMapping(value = "/comment")
@Slf4j
public class ArticleCommentController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private ArticleCommentService articleCommentService;

    @Autowired
    private FeishuService feishuService;

    // 评论
    @PostMapping(value = "/create")
    @ResponseBody
    public ArticleCommentEntity comment(@Valid @RequestBody ArticleCommentVo articleCommentVo){
        String uidString = getUIdStr();
        Long uid = Long.parseLong(uidString);

        ArticleCommentEntity articleCommentEntity = new ArticleCommentEntity();
        articleCommentEntity.setArticleId(articleCommentVo.getArticleId());
        articleCommentEntity.setContent(articleCommentVo.getContent());
        articleCommentEntity.setUserId(uid);

        UserEntity userOperate = userService.findByUserId(uid);
        articleCommentEntity.setHeadImgUrl(userOperate.getHeadimgUrl());
        articleCommentEntity.setNickName(userOperate.getNickname());

        return articleCommentService.addArticleComment(articleCommentEntity);
    }

    // 删除
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public Boolean delComment(@Valid @RequestBody ArticleCommentVo articleCommentVo) {

        // TODO: service层验证这条评论是否可以删除，是不是自己的
        return articleCommentService.removeArticleComment(
                articleCommentVo.getArticleId(),
                articleCommentVo.getCommentId(),
                getUId());
    }

    // reply列表
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<ArticleCommentEntity> list(@RequestParam("pageIndex") int pageIndex, @RequestParam("articleId") String articleId) {
        return articleCommentService.findArticleComment(articleId,pageIndex);
    }



}
