package com.qingboat.us.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class LastAccessRecordsEntity {


    private Integer userId; //用户ID
    private Integer type;
    private String targetId; // 访问的pathwayID 或者 newsletter
    private Integer creatorId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastAccessTime;

    public LastAccessRecordsEntity() {
    }

    public LastAccessRecordsEntity(Integer userId, Integer type, String targetId) {
        this.userId = userId;
        this.type = type;
        this.targetId = targetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
}
