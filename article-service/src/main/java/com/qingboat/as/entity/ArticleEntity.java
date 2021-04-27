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

    private String title = "";   //文章标题

    private String desc = "";    //文章描述

    private Integer status ; // 0:草稿；1：审核中；2：审核驳回；3：审核通过；4：已发布

    private Integer type ; // 0:newsLetter；1：learnPathway

    private Integer scope ; //0:表示免费；1：收费

    private String categoryName ; //

    private String authorId;//作者Id

    private String authorNickName = "";//作者姓名

    private String authorImgUrl = "";//作者图像

    private String imgUrl= "";  //文章封面图片

    private JSONArray data;    //文章数据

    private String parentId = ""; //父文章Id

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    private Long starCount ; //点赞数
    private Long commentCount ; //评论数
    private Long readCount ; //阅读数





}
