package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ArticleCommentEntity;

public interface ArticleCommentService {

    ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity);

    boolean removeArticleComment(String articleId,Long commentId);

    ArticleCommentEntity replyComment(ArticleCommentEntity articleCommentEntity);

    IPage<ArticleCommentEntity> findArticleComment(String articleId, int pageIndex);

}
