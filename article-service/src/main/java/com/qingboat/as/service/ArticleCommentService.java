package com.qingboat.as.service;

import com.qingboat.as.entity.ArticleCommentEntity;

public interface ArticleCommentService {

    ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity);

    boolean removeArticleComment(String articleId,Long commentId);

    ArticleCommentEntity replyComment(ArticleCommentEntity articleCommentEntity);

}
