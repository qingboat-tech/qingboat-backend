package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("apps_benefit")// 映射数据库表名
public class BenefitEntity implements Serializable  {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String key;

    private String title;

    @TableField("`desc`")
    private String desc;

    private String category;

}