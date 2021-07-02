package com.qingboat.us.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 购买同一个creator 的 pathway 和 订阅 newsletter 的用户
 */
@Data
public class SubscriptionAndFollowDTO {
    private Integer id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
