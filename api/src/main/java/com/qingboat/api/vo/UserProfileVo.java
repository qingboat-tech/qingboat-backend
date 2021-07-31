package com.qingboat.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class UserProfileVo {

    private Long id;
    private Long userId;
    private String nickname;
    private String headimgUrl;

    private Integer industryId;
    private String description;
    private String phone;

    private Map[] expertiseArea;//创作者标签： json String, 格式：[{'key':'技术'}]

    private Integer role;  // 1:创作者；2：阅读者

    private String profileName; // 创作者profile名称

    private String creatorImgUrl; // 创作者profile图片

    private String profileImgUrl; // 创作者profile图片

    private String profileKey; // 创作者profile页面Key
    private String position;  // 岗位

    private String profileDesc; // 创作者profile介绍

    private String profileVideoLink; // profile视频链接

    private String luckyCode;  // 口令卡

    private Integer status; // 0: 待审核；1：审核通过；-1：审核不通过

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    private String socialInformationJk;
    private String socialInformationWb;
    private String socialInformationZh;
    private String socialInformationWxgzh;



}

