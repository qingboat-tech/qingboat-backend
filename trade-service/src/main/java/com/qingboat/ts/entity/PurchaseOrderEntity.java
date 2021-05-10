package com.qingboat.ts.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Data
@TableName(value = "apps_purchase_order" ,autoResultMap = true)// 映射数据库表名
public class PurchaseOrderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;                  // 订单号

    private Integer orderStatus;              // 订单状态  0:创建   1:已支付  2:已发货  3:已收货 4:已完成  8:已取消(用户取消)  9:已关闭(超时关闭)

    private Integer paymentStatus;           // 支付状态  0:已创建,未支付   1:已支付  2:已退款

    private Integer paymentMethod;           // 支付方式  1:(CNY)微信支付(国内)  2:(CNY)支付宝支付(国内)

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String,Object> wxNotificationRawData;

    private String wxTransactionTd;         // 微信支付交易号

    private Integer totalAmount;
    private Integer couponAmount;
    private Integer actualAmount;            // 实际支付金额

    private String currency;                  // 币种：CNY

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String paidTime;                 // 支付时间

    private String comment;                   // 订单备注
    private String couponDesc;               // coupon描述

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map subscribeData;

    private Long couponId;                 // coupon表的id
    private Long creatorId;                   // 创造者的id
    private String creatorNickname;          // creator的昵称


    private Long purchaseUserId;             // 下单用户的id
    private String purchaseUserNickname;          // creator的昵称


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

}
