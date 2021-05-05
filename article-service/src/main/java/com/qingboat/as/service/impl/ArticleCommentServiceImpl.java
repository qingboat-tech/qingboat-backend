package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.dao.ArticleCommentDao;
import com.qingboat.as.dao.ReplyCommentDao;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.ReplyCommentEntity;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ArticleCommentServiceImpl implements ArticleCommentService {


    @Autowired
    private ArticleCommentDao articleCommentDao;

    @Autowired
    private ReplyCommentDao replyCommentDao;

    @Autowired
    private ArticleService articleService;

    @Override
    public ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity) {
        articleCommentEntity.setCreatedAt(new Date());
        articleCommentEntity.setReplyCount(0l);
        articleCommentDao.insert(articleCommentEntity);
        //修改增加文章评论的数目
        articleService.increaseCommentCountByArticleId(articleCommentEntity.getArticleId());
        return articleCommentEntity;
    }

    @Override
    public ArticleCommentEntity findArticleComment(String articleId, Long commentId) {
        ArticleCommentEntity entity = new ArticleCommentEntity();
        entity.setId(commentId);
        entity.setArticleId(articleId);

        QueryWrapper<ArticleCommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return articleCommentDao.selectOne(queryWrapper);
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
            //修改减少文章评论的数目
            articleService.decreaseCommentCountByArticleId(articleId);
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

        ArticleCommentEntity entity = new ArticleCommentEntity();
        entity.setArticleId(replyCommentEntity.getArticleId());
        entity.setId(replyCommentEntity.getCommentId());
        entity.setReplyCount(1l);

        articleCommentDao.updateReplyCount(entity);
        replyCommentEntity.setReplyCount(entity.getReplyCount());
        return replyCommentEntity;
    }

    @Override
    public ReplyCommentEntity findArticleReplyComment(String articleId, Long replyId) {
        ReplyCommentEntity entity = new ReplyCommentEntity();
        entity.setId(replyId);
        entity.setArticleId(articleId);

        QueryWrapper<ReplyCommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return replyCommentDao.selectOne(queryWrapper);

    }

    @Override
    public boolean removeReplyComment(String articleId, Long replyId, Long userId) {
        QueryWrapper<ReplyCommentEntity> queryWrapper = new QueryWrapper<>();
        ReplyCommentEntity entity = new ReplyCommentEntity();
        entity.setId(replyId);
        entity.setArticleId(articleId);
        entity.setUserId(userId);
        queryWrapper.setEntity(entity);
        entity = replyCommentDao.selectOne(queryWrapper);
        if (entity!= null){
            ReplyCommentEntity delEntity = new ReplyCommentEntity();
            delEntity.setId(replyId);
            delEntity.setArticleId(articleId);
            queryWrapper.setEntity(delEntity);

            int rst = replyCommentDao.delete(queryWrapper);
            if (rst>0){
                ArticleCommentEntity commentEntity = new ArticleCommentEntity();
                commentEntity.setArticleId(articleId);
                commentEntity.setId(entity.getCommentId());
                commentEntity.setReplyCount(-1l);
                 articleCommentDao.updateReplyCount(commentEntity);
                return true;
            }
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
