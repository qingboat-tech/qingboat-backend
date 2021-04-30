package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("apps_member_tier")// 映射数据库表名
public class MemberTierEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;   // 比如（免费、月，季,年）

    private Long creatorId; //用户id，creator的用户id

    private Integer duration;  // 周期 (单位：天)
    private Integer price ;  // 价格（分）
    private Double  discount;  //打折率（什么类型？）
    private String currency; //  币种
    private String desc;  // 描述

    @TableField(exist = false)
    private List<MemberTierBenefitEntity> memberTierBenefitEntityList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;



}
