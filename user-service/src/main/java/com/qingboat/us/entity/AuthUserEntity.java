package com.qingboat.us.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Map;

@Data
@TableName(value = "auth_user")// 映射数据库表名
public class AuthUserEntity implements Serializable {

    @Id
    private Long id;

    private String password;

    private String username;

    private Integer isStaff;

}
