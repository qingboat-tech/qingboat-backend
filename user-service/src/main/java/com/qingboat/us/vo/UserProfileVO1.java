package com.qingboat.us.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.Date;


public class UserProfileVO1 {

    private Integer id;
    private String headimgUrl;
    private String position;//职业
    private String nickname;
    private String industry; //行业
    private String description;//描述
    private Boolean haveUpdate; //是否有更新
    private String creatorImgUrl;  //店铺头像
    private String profileName;   //店铺名称
    private Integer userId;     //这个才是

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreatorImgUrl() {
        return creatorImgUrl;
    }

    public void setCreatorImgUrl(String creatorImgUrl) {
        this.creatorImgUrl = creatorImgUrl;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Boolean getHaveUpdate() {
        return haveUpdate;
    }

    public void setHaveUpdate(Boolean haveUpdate) {
        this.haveUpdate = haveUpdate;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadimgUrl() {
        return headimgUrl;
    }

    public void setHeadimgUrl(String headimgUrl) {
        this.headimgUrl = headimgUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    //    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date createTime;


}
