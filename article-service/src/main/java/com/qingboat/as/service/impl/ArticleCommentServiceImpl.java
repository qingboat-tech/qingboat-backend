package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.dao.ArticleCommentDao;
import com.qingboat.as.dao.ReplyCommentDao;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.ReplyCommentEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.ArticleCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ArticleCommentServiceImpl implements ArticleCommentService {


    @Autowired
    private ArticleCommentDao articleCommentDao;

    @Autowired
    private ReplyCommentDao replyCommentDao;

    @Override
    public ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity) {
        articleCommentEntity.setCreatedAt(new Date());
        articleCommentEntity.setReplyCount(0l);
        articleCommentDao.insert(articleCommentEntity);
        return articleCommentEntity;
    }

    @Override
    public boolean removeArticleComment(String articleId, Long commentId,Long userId) {
        QueryWrapper<ArticleCommentEntity> queryWrapper = new QueryWrapper<>();
        ArticleCommentEntity entity = new ArticleCommentEntity();
        entity.setId(commentId);
        entity.setArticleId(articleId);
        entity.setUserId(userId);
        queryWrapper.setEntity(entity);
        int rst = articleCommentDao.delete(queryWrapper);
        if (rst>0){
            return true;
        }
        return false;
    }

    @Override
    public IPage<ArticleCommentEntity> findArticleComment(String articleId, int pageIndex) {
        IPage<ArticleCommentEntity> page = new Page<>(pageIndex, 10);
        QueryWrapper<ArticleCommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        ArticleCommentEntity entity = new ArticleCommentEntity();
        entity.setArticleId(articleId);
        queryWrapper.setEntity(entity);
        page =articleCommentDao.selectPage(page,queryWrapper);
        return page;
    }

    @Override
    public ReplyCommentEntity replyComment(ReplyCommentEntity replyCommentEntity) {
        replyCommentEntity.setCreatedAt(new Date());
        replyCommentDao.insert(replyCommentEntity);

//        ArticleCommentEntity entity = new ArticleCommentEntity();
//        entity.setArticleId(replyCommentEntity.getArticleId());
//        entity.setId(replyCommentEntity.getCommentId());

//        Long replyCount = articleCommentDao.updateReplyCount(entity);
//        entity.setReplyCount(replyCount);

        return replyCommentEntity;
    }

    @Override
    public boolean removeReplyComment(String articleId, Long replyId,Long userId) {
        QueryWrapper<ReplyCommentEntity> queryWrapper = new QueryWrapper<>();
        ReplyCommentEntity entity = new ReplyCommentEntity();
        entity.setId(replyId);
        entity.setArticleId(articleId);
        entity.setUserId(userId);
        queryWrapper.setEntity(entity);
        int rst = replyCommentDao.delete(queryWrapper);
        if (rst>0){
            return true;
        }
        return false;
    }

    @Override
    public IPage<ReplyCommentEntity> findReplyComment(String articleId, Long commentId, int pageIndex) {
        IPage<ReplyCommentEntity> page = new Page<>(pageIndex, 10);
        QueryWrapper<ReplyCommentEntity> queryWrapper = new QueryWrapper<>();
        ReplyCommentEntity entity = new ReplyCommentEntity();
        entity.setArticleId(articleId);
        entity.setCommentId(commentId);
        queryWrapper.setEntity(entity);
        page =replyCommentDao.selectPage(page,queryWrapper);
        return page;
    }
}
