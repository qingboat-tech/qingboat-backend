package com.qingboat.as.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qingboat.as.entity.ReplyCommentEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ArticleCommentListVo {
    private Long id;

    private String articleId;

    private String content;

    private Long userId;

    private String nickName;

    private String headImgUrl;

    private Long replyCount;

    private byte role;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @TableField(exist = false)
    private List<ReplyCommentEntity> replyCommentEntityList;
}
