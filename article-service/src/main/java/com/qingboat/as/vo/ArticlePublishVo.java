package com.qingboat.as.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ArticlePublishVo implements Serializable {
    private String articleId;
    private String publishType;   //FREE  PAID
    private Set<String> tags;
}
