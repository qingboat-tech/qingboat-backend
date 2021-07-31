package com.qingboat.as.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ReadOnVo {
    private Integer creatorId;
    private String title;
    private String desc;
    private String headimgUrl;  //作者头像
    private String creatorimgUrl; //店铺头像
    private Integer contentType;
    private String nickname;
    private String profileName;
    private String pathwayName;
    private Integer height;
    private Integer pathwayId;
    private String contentId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastReadTime;
    private String articleAddress; // 如果是pathway 需要给出地址

}
