package com.qingboat.api.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class TierVo {
    private Long id;
    private String title;   //标题
    private String desc;  // 描述
    private Long creatorId; //用户id，creator的用户id

    private Integer monthPrice ;  // 价格（分）
    private Double  monthDiscount;  //打折率（什么类型？）

    private String subscribeDuration;  //free or month or year or monthAndYear
    private Integer yearPrice ;  // 价格（分）
    private Double  yearDiscount;  //打折率（什么类型？）
    private Integer subscribeLimit; //限量设置
    private String currency ="CNY"; //  币种

    private Integer status;  // 0:表示删除，1：表示有效
    private Integer subscribeCount; //订阅人数

    private  List<Object> benefitList;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public static Map<String, Object> ConvertToJson(TierVo entity) {
            //Object mapper instance
        ObjectMapper mapper = new ObjectMapper();

        // object -> Map
        Map<String, Object> map = mapper.convertValue(entity, Map.class);

        return map;
    }

}