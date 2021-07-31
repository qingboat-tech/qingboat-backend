package com.qingboat.us.vo;


import com.qingboat.us.entity.UserProfileEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class UserProfileEntityVO {

    private Long id;
    private Long userId;
    private String nickname;
    private String headimgUrl;

    private Integer industryId;
    private String description;
    private String phone;


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

    private Integer status; // 0: 待审核；1：审核通过；-1：审核不通过


    private Date createdAt;


    private Date updatedAt;

    private List<SocialInformationKeyValue> socialInformation;

    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgUrl() {
        return headimgUrl;
    }

    public void setHeadimgUrl(String headimgUrl) {
        this.headimgUrl = headimgUrl;
    }

    public Integer getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Map[] getExpertiseArea() {
        return expertiseArea;
    }

    public void setExpertiseArea(Map[] expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getCreatorImgUrl() {
        return creatorImgUrl;
    }

    public void setCreatorImgUrl(String creatorImgUrl) {
        this.creatorImgUrl = creatorImgUrl;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getProfileKey() {
        return profileKey;
    }

    public void setProfileKey(String profileKey) {
        this.profileKey = profileKey;
    }

    public String getProfileDesc() {
        return profileDesc;
    }

    public void setProfileDesc(String profileDesc) {
        this.profileDesc = profileDesc;
    }

    public String getProfileVideoLink() {
        return profileVideoLink;
    }

    public void setProfileVideoLink(String profileVideoLink) {
        this.profileVideoLink = profileVideoLink;
    }

    public String getLuckyCode() {
        return luckyCode;
    }

    public void setLuckyCode(String luckyCode) {
        this.luckyCode = luckyCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<SocialInformationKeyValue> getSocialInformation() {
        return socialInformation;
    }

    public void setSocialInformation(List<SocialInformationKeyValue> socialInformation) {
        this.socialInformation = socialInformation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public  synchronized void userProfileEntityToVo(UserProfileEntity userProfileEntity){
        this.id = userProfileEntity.getId();
        this.userId = userProfileEntity.getUserId();
        this.nickname = userProfileEntity.getNickname();
        this.headimgUrl = userProfileEntity.getHeadimgUrl();
        this.industryId = userProfileEntity.getIndustryId();
        this.description = userProfileEntity.getDescription();
        this.phone = userProfileEntity.getPhone();
        this.expertiseArea = userProfileEntity.getExpertiseArea();
        this.position = userProfileEntity.getPosition();
        this.role = userProfileEntity.getRole();
        this.profileName = userProfileEntity.getProfileName();
        this.creatorImgUrl = userProfileEntity.getCreatorImgUrl();
        this.profileImgUrl = userProfileEntity.getProfileImgUrl();
        this.profileKey = userProfileEntity.getProfileKey();
        this.profileDesc = userProfileEntity.getProfileDesc();
        this.profileVideoLink = userProfileEntity.getProfileVideoLink();
        this.luckyCode = userProfileEntity.getLuckyCode();
        this.status = userProfileEntity.getStatus();
        this.createdAt = userProfileEntity.getCreatedAt();
        this.updatedAt = userProfileEntity.getUpdatedAt();
        this.email = userProfileEntity.getEmail();
        if (userProfileEntity.getSocialInformationJk() != null){
            this.socialInformation.add(new SocialInformationKeyValue("即刻",userProfileEntity.getSocialInformationJk()));
        }
        if (userProfileEntity.getSocialInformationWb() != null){
            this.socialInformation.add(new SocialInformationKeyValue("微博",userProfileEntity.getSocialInformationWb()));
        }
        if ( userProfileEntity.getSocialInformationZh() != null){
            this.socialInformation.add(new SocialInformationKeyValue("知乎",userProfileEntity.getSocialInformationZh()));
        }
        if (userProfileEntity.getSocialInformationWxgzh() != null){
            this.socialInformation.add(new SocialInformationKeyValue("微信公众号",userProfileEntity.getSocialInformationWxgzh()));
        }
    }

    class SocialInformationKeyValue{
        private String key;
        private String value;

        public SocialInformationKeyValue() {
        }

        public SocialInformationKeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


}
