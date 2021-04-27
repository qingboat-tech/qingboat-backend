package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.dao.ArticleCommentDao;
import com.qingboat.as.entity.ArticleCommentEntity;
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

    @Override
    public ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity) {
        articleCommentEntity.setCreatedAt(new Date());
        articleCommentDao.insert(articleCommentEntity);
        return articleCommentEntity;
    }

    @Override
    public boolean removeArticleComment(String articleId, Long commentId) {
        QueryWrapper<ArticleCommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",commentId)
            .eq("article_id",articleId);
        int rst = articleCommentDao.delete(queryWrapper);
        if (rst>0){
            return true;
        }
        return false;
    }

    @Override
    public ArticleCommentEntity replyComment(ArticleCommentEntity articleCommentEntity) {
        QueryWrapper<ArticleCommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",articleCommentEntity.getId())
                .eq("article_id",articleCommentEntity.getArticleId());
        ArticleCommentEntity entity = articleCommentDao.selectOne(queryWrapper);
        List<ArticleCommentEntity> replyList = entity.getReplyList();
        if (replyList == null){
            replyList = new ArrayList<>(1);
        }
        replyList.add(0,articleCommentEntity);

        articleCommentDao.update(entity,queryWrapper);
        return entity;
    }

    @Override
    public IPage<ArticleCommentEntity> findArticleComment(String articleId, int pageIndex) {
        IPage<ArticleCommentEntity> page = new Page<>(pageIndex, 10);
        QueryWrapper<ArticleCommentEntity> queryWrapper = new QueryWrapper<>();
        page =articleCommentDao.selectPage(page,queryWrapper);

        return page;
    }
}
