package com.qingboat.ts.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("authtoken_token")// 映射数据库表名
public class AuthToken extends Model<AuthToken> implements Serializable {

    @TableId("`key`")
    private String key;

    private Long userId;

    @TableField("`created`")
    private Date createAt;

}
