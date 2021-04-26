package com.qingboat.as.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticlePublishVo implements Serializable {
    private String articleId;
    private Integer scope;
}
