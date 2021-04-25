package com.qingboat.uc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document
@Data
public class CreatorApplyFormEntity implements Serializable {

    @Id
    private String id;      //创作者申请表Id
    private String title;   //创作者申请表
    private String desc;    //创作者申请表
    private Long userId;    //创作者Id (auth_user--->id)

    private List<QuestionEntity> questionEntityList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;


    @Data
    public static  class QuestionEntity  implements Serializable{
        private String desc;   //问题内容描述
        private String type;   //问题类型：input（填空）、check（多选），radio（单选）
        private List<OptionEntity> optionList; //答案可选项
        private List<OptionEntity> answerList; //答案

    }

    @Data
    public static  class OptionEntity  implements Serializable{
        private String key;   //可选项Key
        private String value;   //可选项显示描述
    }
}
