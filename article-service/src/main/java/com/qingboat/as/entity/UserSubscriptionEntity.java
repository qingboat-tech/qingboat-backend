package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qingboat.as.utils.handler.BenefitJsonHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "apps_usersubscription",autoResultMap = true)// 映射数据库表名
public class UserSubscriptionEntity implements Serializable {

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

    @TableField(typeHandler = BenefitJsonHandler.class)
    private List<BenefitEntity> benefitList;//权益列表： json String

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @TableField(exist = false)
    private String creatorHeadImgUrl;

    @TableField(exist = false)
    private String creatorNickname;

    @TableField(exist = false)
    private String subscriberHeadImgUrl;

    @TableField(exist = false)
    private String subscriberNickname;

    @TableField(exist = false)
    private String subscriberDesc;


    @TableField(exist = false)
    private Map[] expertiseArea;//创作者标签： json String, 格式：[{'key':'技术'}]


}
