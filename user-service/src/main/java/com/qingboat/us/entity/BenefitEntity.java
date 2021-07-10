package com.qingboat.us.entity;

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

    @TableField("`key`")
    private String key;

    private Long creatorId;  //系统的则为0

    private String title;

    @TableField("`desc`")
    private String desc;

    private String category;

    @TableField(exist = false)
    private Integer usedCount = 0;

    public void addUsedCount(){
        if (usedCount == null){
            usedCount = 0;
        }
        ++usedCount;
    }

}
