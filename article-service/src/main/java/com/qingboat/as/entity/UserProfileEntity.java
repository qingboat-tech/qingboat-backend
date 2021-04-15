package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("apps_userprofile")// 映射数据库表名
public class UserProfileEntity implements Serializable {

    private Long userId;

    private String headimgUrl;

    private String nickname;

}
