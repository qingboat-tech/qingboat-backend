package com.qingboat.ts.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Data
@TableName("apps_purchaseorderuser")// 映射数据库表名

public class PurchaseOrder {

    private Long id;

    private String order_no;                  // 订单号

    private String order_status;              // 订单状态

    private Integer payment_status;           // 支付状态

    private Integer payment_method;           // 支付方式

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map[] wx_notification_raw_data;

    private String wx_transaction_id;         // 微信支付交易号

    private Integer total_amount;
    private Integer coupon_amount;
    private Integer actual_amount;            // 实际支付金额

    private String currency;                  // 币种：CNY

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String paid_time;                 // 支付时间

    private String comment;                   // 订单备注
    private String coupon_desc;               // coupon描述
    private String creator_nickname;          // creator的昵称

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map[] subscribe_data;

    private Integer coupon_id;                 // coupon表的id
    private Long creator_id;                   // 创造者的id

    private Long purchase_user_id;             // 下单用户的id

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created_at;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated_at;

}
