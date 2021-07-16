package com.qingboat.as.vo;

import lombok.Data;

@Data
public class UserProfileInfoVo {
    private String nickname;    //作者昵称
    private String profileName; //店铺名称
    private String headimgUrl;  //作者头像
    private String creatorImgUrl; //店铺头像
    private String profileImgUrl; //店铺背景图
}
