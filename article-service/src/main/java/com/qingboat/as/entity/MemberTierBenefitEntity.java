package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("apps_member_benefit")// 映射数据库表名
public class MemberTierBenefitEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberTierId;

    private Long  creatorId;

    private Long  benefitId;

    private String  benefitName;

    private Integer  benefitValue; //权益对应的值（整型），比如说1对1多少次

}
