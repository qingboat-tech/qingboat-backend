package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @TableField(exist = false)
    private String subscribeDuration = "month";  // month or year

    private Integer yearPrice ;  // 价格（分）
    private Double  yearDiscount;  //打折率（什么类型？）

    private Integer subscribeLimit; //限量设置

    private String currency; //  币种

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<BenefitEntity> benefitList;//权益列表： json String

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    private int status;  // 0:表示删除，1：表示有效


    @TableField(exist = false)
    private Integer subscribeCount; //订阅人数

}
