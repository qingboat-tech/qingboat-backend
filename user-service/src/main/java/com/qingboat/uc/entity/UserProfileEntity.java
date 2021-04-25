package com.qingboat.uc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@TableName("apps_userprofile")// 映射数据库表名
public class UserProfileEntity {

    private Long id;
    private Long userId;
    private String nickname;
    private String headimgUrl;

    private Integer industryId;
    private String description;
    private String phone;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map[] expertiseArea;//创作者标签： json String, 格式：[{'key':'技术'}]

    private Integer role;  // 1:创作者；2：阅读者

    private String profileImgUrl; // 创作者profile图片
    private Integer status; // 0: 待审核；1：审核通过；-1：审核不通过

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;


}
