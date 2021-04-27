package com.qingboat.as.service.impl;

import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.service.ArticleCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleCommentServiceImpl implements ArticleCommentService {

    @Override
    public ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity) {
        return null;
    }

    @Override
    public boolean removeArticleComment(String articleId, Long commentId) {
        return false;
    }

    @Override
    public ArticleCommentEntity replyComment(ArticleCommentEntity articleCommentEntity) {
        return null;
    }
}
