package com.qingboat.us.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@TableName(value="apps_usersubscription")
public class UserSubscriptionEntity {
    @Id
    private Integer id;

    private Integer subscriberId;

    private Integer creatorId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireDate;



    private Integer  memberTierId;

    private Integer orderId;

    private Integer orderPrice;

    private String subscribeDuration;

    private String benefitList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    private String memberTierName;

    private String orderNo;

}
