package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("apps_benefit")// 映射数据库表名
public class BenefitEntity implements Serializable  {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerId;

    private String name;

    private String desc;

}
