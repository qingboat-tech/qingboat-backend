package com.qingboat.as.entity;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class ArticleEntity {

    @Id
    private String id;      //文章Id

    private String title;   //文章标题

    private String desc;    //文章描述

    @Indexed
    private String authorId;//作者Id

    private String imgUrl;  //文章封面图片

    private JSONArray data;    //文章数据

    @Indexed
    private String parentId; //父文章Id

    private Date createdTime;

    private Date updatedTime;

}
