package com.qingboat.as.entity;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Document
@Data
public class ArticleEntity {

    @Id
    private String id;      //文章Id

    private String title = "";   //文章标题

    private String desc = "";    //文章描述

    private Integer top = 0 ; //是否置顶，1：表示置顶

    private Integer status ; // 0:草稿；1：审核中；2：审核驳回；3：审核通过；4：已发布；5：只读不可编辑；6：禁用 ,7: 试读未订阅

    private Integer type ; // 0:newsLetter；1：learnPathway

    private String suggestion =""; // 审核被驳回的建议和原因

    @Deprecated
    private Integer scope ; //0:表示免费；1：收费

    private String categoryName ; //

    private String authorId;//作者Id

    private String authorNickName = "";//作者姓名

    private String authorImgUrl = "";//作者图像

    private String imgUrl= "";  //文章封面图片

    private JSONArray data;    //文章数据

    private String parentId = ""; //父文章Id

    private Set<String> benefit = new HashSet<>();

//    private List<Long> tierIdList = new ArrayList<>(); //按照价格滴到高排序存放 先free、再比价

    private Set<String> tags = new HashSet<>();  //文章标签

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    private Long starCount ; //点赞数
    private Long commentCount ; //评论数
    private Long readCount ; //阅读数

    @Transient
    private TierEntity tierEntity;  //推荐需要订阅的套餐对象

    @Transient
    private String readerRole;   //阅读者角色：author、free-subscriber、paid-subscriber、visitor

//    @Transient
//    private List<Long> userSubscribeTierIdList;  //会员订阅的套餐Id List

    @Transient
    private Boolean hasStar ; //判断自己是否点赞

    @Transient
    private Boolean canComment ; //判断是否评论




}
