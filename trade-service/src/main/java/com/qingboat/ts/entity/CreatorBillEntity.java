package com.qingboat.ts.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("apps_creator_bill")// 映射数据库表名
public class CreatorBillEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 创作者id
    private Long creatorId;

    // 交易类型
    private Integer billType;

    // 交易展示标题
    private String billTitle;

    // 交易发生时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date billTime;

    // 交易金额
    private Integer amount;

    // 交易币种
    private String currency = "CNY";

    // 关联订单号，并非订单id
    private String orderNo;

}