package com.qingboat.as.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "apps_msg",autoResultMap = true)// 映射数据库表名
public class MessageEntity implements Serializable {

    public static final Integer SYSTEM_MSG = 0;
    public static final Integer COMMENT_MSG = 1;
    public static final Integer REPLY_MSG = 2;
    public static final Integer STAR_MSG = 3;
    public static final Integer SUBSCRIBE_MSG = 4;


    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("`to`")
    private Long to;   //接收者

    private Long senderId; //发送者Id

    private String senderName; //发送者名称

    private String senderImgUrl; //发送者图像的url

    private Integer msgType;  // 消息类型：0:系统消息；1:评论消息；2:评论回复消息；3：点赞消息；4:订阅消息

    private String msgTitle; // 消息标题

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject msgData;//消息体： json String

    private String msgLink; // 0: 消息链接

    private Integer readFlag; //0：未读；1：已读

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public void setExtData(String key,Object value){
        if (msgData == null){
            msgData = new JSONObject();
        }
        msgData.put(key,value);
    }



}
