package com.qingboat.us.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="apps_userwechat")
public class UserWechatEntity {

    private String openId;

    private Long userId;
}
