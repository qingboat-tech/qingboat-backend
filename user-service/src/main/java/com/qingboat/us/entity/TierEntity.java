package com.qingboat.us.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qingboat.us.utils.handler.BenefitJsonHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName(value = "apps_tier",autoResultMap = true)// 映射数据库表名
public class TierEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;   //标题

    @TableField("`desc`")
    private String desc;  // 描述

    private Long creatorId; //用户id，creator的用户id

    private Integer monthPrice ;  // 价格（分）
    private Double  monthDiscount;  //打折率（什么类型？）

    // 订阅周期
    private String subscribeDuration;  //free or month or year or monthAndYear

    private Integer yearPrice ;  // 价格（分）
    private Double  yearDiscount;  //打折率（什么类型？）

    private Integer subscribeLimit; //限量设置

    private String currency ="CNY"; //  币种

    @TableField(typeHandler = BenefitJsonHandler.class)
    private List<BenefitEntity> benefitList;//权益列表： json String

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @TableField("`status`")
    private int status;  // 0:表示删除，1：表示有效


    @TableField(exist = false)
    private Integer subscribeCount; //订阅人数

    @TableField(exist = false)
    private Boolean subscribed; //是否订阅

}
