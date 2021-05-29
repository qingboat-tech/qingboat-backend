package com.qingboat.api.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserSubscriptionVo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long subscriberId;

    private Long  creatorId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireDate;

    private Long  memberTierId;

    private String memberTierName;

    private String subscribeDuration = "month";  // month or year

    private Long  orderId;    // 当是免费订阅的，则 orderId 为0 表示

    private String orderNo;   // 订单号

    private Integer orderPrice ;  // 实际支付的订单金额（分）

    private List<Object> benefitList;//权益列表： json String

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;


}
