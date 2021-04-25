package com.qingboat.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("apps_userprofile")// 映射数据库表名
public class UserProfileEntity {

    private Long id;
    private Long userId;
    private String nickname;
    private String headingUrl;

    private Integer industryId;
    private String description;
    private String phone;
    private String expertiseArea; //创作者标签： json String, 格式：[{'key':'技术'}]

    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    private Integer role;  // 1:创作者；2：阅读者

    private String profileImgUrl; // 创作者profile图片
    private Integer status; // 0: 待审核；1：审核通过；-1：审核不通过


}
