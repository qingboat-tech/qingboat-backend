package com.qingboat.us.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@TableName(value="apps_userprofile", autoResultMap = true)// 映射数据库表名
public class UserProfileEntity implements Serializable {

    

    private Long id;
    private Long userId;
    private String nickname;
    private String headimgUrl;

    private Integer industryId;
    private String description;
    private String phone;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map[] expertiseArea;//创作者标签： json String, 格式：[{'key':'技术'}]

    private String position;

    private Integer role;  // 1:创作者；2：阅读者

    private String profileName; // 创作者profile名称

    private String creatorImgUrl; // 创作者profile图片

    private String profileImgUrl; // 创作者profile图片

    private String profileKey; // 创作者profile页面Key

    private String profileDesc; // 创作者profile介绍

    private String profileVideoLink; // profile视频链接

    private String luckyCode;       // 口令卡，正确的口令卡可以免费建立订阅关系

    @TableField("`status`")
    private Integer status; // 0: 待审核；1：审核通过；-1：审核不通过

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    private String socialInformationJk;
    private String socialInformationWb;
    private String socialInformationZh;
    private String socialInformationWxgzh;
    private String email;

}
