package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@TableName(value = "apps_userprofile",autoResultMap = true)// 映射数据库表名
public class UserEntity implements Serializable {

    private Long userId;

    private String headimgUrl;

    private String nickname;

    private String description;

    private Integer role;  // 1:创作者；2：阅读者

    @TableField("`status`")
    private Integer status; // 0: 待审核；1：审核通过；-1：审核不通过

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map[] expertiseArea;//创作者标签： json String, 格式：[{'key':'技术'}]

}
