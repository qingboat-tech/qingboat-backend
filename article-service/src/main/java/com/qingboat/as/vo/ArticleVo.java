package com.qingboat.as.vo;

import com.qingboat.as.entity.ArticleEntity;
import lombok.Data;

@Data
public class ArticleVo extends ArticleEntity {

    private String authorNickName;
    private String authorImgUrl;
}
