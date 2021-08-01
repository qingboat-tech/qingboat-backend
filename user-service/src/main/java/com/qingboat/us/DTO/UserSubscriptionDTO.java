package com.qingboat.us.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class UserSubscriptionDTO {
    private Integer id;
    private Integer subscriberId;
    private Integer creatorId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireDate;
    private Integer memberTierId;
    private Integer orderId;
    private Integer orderPrice;
    private String subscribeDuration;
    private String benefitList;
    private String memberTierName;
    private String orderNo;
}
