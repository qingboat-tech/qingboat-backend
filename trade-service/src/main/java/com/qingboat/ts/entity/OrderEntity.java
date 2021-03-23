package com.qingboat.ts.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("apps_purchaseorder")// 映射数据库表名
public class OrderEntity extends Model<OrderEntity> implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String oderNo;
    private Integer actualAmount;
    private Integer orderStatus;  //
    private Integer paymentStatus; //
    private Integer totalAmount;
    private String currency;  //币种
    private Date paidTime;
    private String comment;
    private Date createdAt;
    private Date updatedAt;
    private Long purchaseUserId;
    private Integer couponAmount;
    private String couponDesc;
    private Integer paymentMethod;
    private Long couponId;
    private String wxNotificationRawData;
    private String wxTransactionId;
    private Long goodsId;
    private String goodsImgUrl;
    private String goodsTitle;
    private Long productId;
    private Integer productType;
}
