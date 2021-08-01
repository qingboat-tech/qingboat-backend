package com.qingboat.us.vo;

import java.util.List;

/**
 *  TA 订阅的newsletter (用于卡片)
 *
 */
public class TaSubscriptionNewslettersVO {
    private String headimgUrl;  //creator 头像
    private String profileImgUrl;
    private String creatorImgUrl;
    private String position;
    private Integer userId;
    private String industry;
    private String description;
    private String nickname;
    private String profileDesc;
    private String profileName;
    private Integer subscriptionCount;
    private List<UserProfileVO1>  userProfileVO1List;
    private Boolean subscriptionRelationshipForMe;
    private Integer contentCount;
    private Integer benefitCount;

    public Integer getContentCount() {
        return contentCount;
    }

    public void setContentCount(Integer contentCount) {
        this.contentCount = contentCount;
    }

    public Integer getBenefitCount() {
        return benefitCount;
    }

    public void setBenefitCount(Integer benefitCount) {
        this.benefitCount = benefitCount;
    }

    public Boolean getSubscriptionRelationshipForMe() {
        return subscriptionRelationshipForMe;
    }

    public void setSubscriptionRelationshipForMe(Boolean subscriptionRelationshipForMe) {
        this.subscriptionRelationshipForMe = subscriptionRelationshipForMe;
    }

    public Integer getSubscriptionCount() {
        return subscriptionCount;
    }

    public void setSubscriptionCount(Integer subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
    }

    public List<UserProfileVO1> getUserProfileVO1List() {
        return userProfileVO1List;
    }

    public void setUserProfileVO1List(List<UserProfileVO1> userProfileVO1List) {
        this.userProfileVO1List = userProfileVO1List;
    }

    public String getHeadimgUrl() {
        return headimgUrl;
    }

    public void setHeadimgUrl(String headimgUrl) {
        this.headimgUrl = headimgUrl;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getCreatorImgUrl() {
        return creatorImgUrl;
    }

    public void setCreatorImgUrl(String creatorImgUrl) {
        this.creatorImgUrl = creatorImgUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileDesc() {
        return profileDesc;
    }

    public void setProfileDesc(String profileDesc) {
        this.profileDesc = profileDesc;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
