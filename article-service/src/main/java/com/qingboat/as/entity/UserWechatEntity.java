package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "apps_userwechat",autoResultMap = true)// 映射数据库表名
public class UserWechatEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String openId;

}
