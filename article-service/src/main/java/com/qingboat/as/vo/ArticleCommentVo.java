package com.qingboat.as.vo;

import lombok.Data;

import java.io.Serializable;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleCommentVo {
    private String articleId;
    private String content;
    private Long parentId;
}

