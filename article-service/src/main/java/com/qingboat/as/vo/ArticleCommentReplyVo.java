package com.qingboat.as.vo;

import lombok.Data;

@Data
public class ArticleCommentReplyVo {
    private String articleId;
    private String content;
    private Long commentId;
    private Long replyId;
}

