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

    private String orderNo;
    private Integer actualAmount;

    // ORDER_STATUS_CREATED = 0    # 已创建
    // ORDER_STATUS_PAID = 1       # 已支付
    // ORDER_STATUS_DELIVERY = 2   # 已发货（虚拟产品不需要）
    // ORDER_STATUS_TAKE = 3       # 已收货（虚拟产品不需要）
    // ORDER_STATUS_COMPLETE = 4   # 已完成
    // ORDER_STATUS_CANCEL = 8     # 已取消(用户取消)
    // ORDER_STATUS_CLOSE = 9      # 已关闭(超时关闭)
    private Integer orderStatus;

    // PAYMENT_STATUS_CREATED = 0         # 已创建,未支付
    // PAYMENT_STATUS_PAID = 1            # 已支付
    // PAYMENT_STATUS_REFUND = 2          # 已退款
    private Integer paymentStatus;

    private Integer totalAmount;
    private String currency;  //币种
    private Date paidTime;
    private String comment;
    private Date createdAt;
    private Date updatedAt;
    private Long purchaseUserId;
    private Integer couponAmount;
    private String couponDesc;

    // PAYMENT_METHOD_WECHAT = 1          # (CNY)微信支付(国内)
    // PAYMENT_METHOD_ALIPAY = 2          # (CNY)支付宝支付(国内)
    private Integer paymentMethod;

    private Long couponId;
    private String wxNotificationRawData;
    private String wxTransactionId;
    private Long goodsId;
    private String goodsImgUrl;
    private String goodsTitle;
    private Long goodsSkuId;

    private Long productId;
    private Integer productType;


}
