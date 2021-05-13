package com.qingboat.ts.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("apps_creator_wallet")// 创作者钱包
public class CreatorWalletEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 创作者id
    private Long creatorId;

    // 余额
    private Long balance;

    // 冻结金额
    private Long freeze;

    // 交易币种
    private String currency = "CNY";

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

}