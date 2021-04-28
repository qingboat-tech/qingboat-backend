package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.ReplyCommentEntity;

public interface ArticleCommentService {

    ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity);

    boolean removeArticleComment(String articleId,Long commentId ,Long userId);

    IPage<ArticleCommentEntity> findArticleComment(String articleId, int pageIndex);

    ReplyCommentEntity replyComment(ReplyCommentEntity replyCommentEntity);

    boolean removeReplyComment(String articleId,Long replyId,Long userId);

    IPage<ReplyCommentEntity> findReplyComment(String articleId,Long commentId, int pageIndex);

}
