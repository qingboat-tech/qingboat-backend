package com.qingboat.as.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.ReplyCommentEntity;

public interface ArticleCommentService {

    ArticleCommentEntity addArticleComment(ArticleCommentEntity articleCommentEntity);

    ArticleCommentEntity findArticleComment(String articleId,Long commentId);

    boolean removeArticleComment(String articleId,Long commentId ,Long userId);

    IPage<ArticleCommentEntity> findArticleComment(String articleId, Integer pageIndex,Integer pageSize);

    ReplyCommentEntity replyComment(ReplyCommentEntity replyCommentEntity);

    ReplyCommentEntity findArticleReplyComment(String articleId,Long replyId);

    boolean removeReplyComment(String articleId,Long replyId,Long userId);

    IPage<ReplyCommentEntity> findReplyComment(String articleId,Long commentId, Integer pageIndex,Integer pageSize);

}
