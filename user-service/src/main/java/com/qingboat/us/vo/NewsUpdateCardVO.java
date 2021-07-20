package com.qingboat.us.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
public class NewsUpdateCardVO implements Comparable<NewsUpdateCardVO>{
    private String creatorImgUrl;      //店铺头像
    private String profileName;        //店铺昵称

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;            //更新时间
    private String coverImgUrl;        //封面图片
    private Integer contentType;       //类型    1 表示pathaway， 2表示newsletter
    private String contentId;          //pathway 或者 newsletter 的主键
    private Integer likeCount;         //点赞数量
    private Boolean isLiked;           // 是否已经点赞
    private Integer creatorId;         //创作者ID
    private String description;        //描述
    private String nickname;           //昵称
    private String headimgUrl;         //用户头像
    private Boolean isPurchase;        //是否已购买
    private String title;              //标题




    @Override
    public int compareTo(NewsUpdateCardVO o) {
        return this.updateTime.compareTo(o.updateTime) >= 0 ? -1 : 1;
    }
}
