package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName("apps_readon")// 映射数据库表名
public class ReadonEntity {
    public ReadonEntity(Integer userId,Integer contentType,String contentId,Integer creatorId,Integer height){
        this.userId = userId;
        this.contentType = contentType;
        this.contentId = contentId;
        this.creatorId = creatorId;
        this.height = height;
    }
    private Integer userId;
    private Integer contentType;
    private String contentId;
    private Integer creatorId;
    private Integer height;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;
}
