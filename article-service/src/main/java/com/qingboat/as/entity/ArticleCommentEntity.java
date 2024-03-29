package com.qingboat.as.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
@TableName("apps_article_comment")// 映射数据库表名
public class ArticleCommentEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String articleId;

    private String content;

    private Long userId;

    private String nickName;

    private String headImgUrl;

    private Long replyCount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @TableField(exist = false)
    private List<ReplyCommentEntity> replyCommentEntityList;

    @TableField(exist = false)
    private byte role;

}
