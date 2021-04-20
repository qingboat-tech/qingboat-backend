package com.qingboat.as.entity;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Document
@Data
public class ArticleEntity {

    @Id
    private String id;      //文章Id

    private String title;   //文章标题

    private String desc;    //文章描述

    // @Indexed
    private String authorId;//作者Id

    private String imgUrl;  //文章封面图片

    private JSONArray data;    //文章数据

    // @Indexed
    private String parentId; //父文章Id

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}
