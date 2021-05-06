package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("apps_userprofile")// 映射数据库表名
public class UserEntity implements Serializable {

    private Long userId;

    private String headimgUrl;

    private String nickname;

    private Integer role;  // 1:创作者；2：阅读者

    @TableField("`status`")
    private Integer status; // 0: 待审核；1：审核通过；-1：审核不通过

}
