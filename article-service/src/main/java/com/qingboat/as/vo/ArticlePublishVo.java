package com.qingboat.as.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ArticlePublishVo implements Serializable {
    private String articleId;
    private Set<Long> tierIds;
    private Set<String> tags;
}
