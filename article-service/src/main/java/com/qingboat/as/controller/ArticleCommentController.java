package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.ReplyCommentEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.utils.AliyunOssUtil;
import com.qingboat.as.utils.RssUtil;
import com.qingboat.as.utils.sensi.SensitiveFilter;
import com.qingboat.as.vo.ArticleCommentDelVo;
import com.qingboat.as.vo.ArticleCommentVo;
import com.qingboat.as.vo.ArticlePublishVo;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import com.rometools.rome.io.FeedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/comment")
@Slf4j
public class ArticleCommentController {


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
        String uidString = getUId();
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
    public Boolean delComment(@Valid @RequestBody ArticleCommentDelVo articleCommentDelVo) {

        // TODO: service层验证这条评论是否可以删除，是不是自己的
        return articleCommentService.removeArticleComment(
                articleCommentDelVo.getArticleId(),
                articleCommentDelVo.getId(),
                Long.parseLong(getUId()));
    }

    // reply列表
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<ArticleCommentEntity> list(@RequestParam("pageIndex") int pageIndex, @RequestParam("articleId") String articleId) {
        return articleCommentService.findArticleComment(articleId,pageIndex);
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
