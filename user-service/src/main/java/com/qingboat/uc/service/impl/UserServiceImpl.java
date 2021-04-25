package com.qingboat.uc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mongodb.BasicDBObject;
import com.qingboat.base.exception.BaseException;
import com.qingboat.uc.dao.CreatorApplyFormMongoDao;
import com.qingboat.uc.dao.UserProfileDao;
import com.qingboat.uc.entity.CreatorApplyFormEntity;
import com.qingboat.uc.entity.UserProfileEntity;
import com.qingboat.uc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private CreatorApplyFormMongoDao creatorApplyFormMongoDao;




    @Override
    public UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity) {
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userProfileEntity.getUserId());

        userProfileEntity.setUpdatedAt(new Date());

        log.info(userProfileEntity.toString());

        int rst = userProfileDao.update(userProfileEntity,queryWrapper);

        if(rst==1){
            return  userProfileEntity;
        }else {
            throw new BaseException(500,"userProfile is empty");
        }
    }

    @Override
    public CreatorApplyFormEntity saveCreatorApplyForm(CreatorApplyFormEntity creatorApplyFormEntity) {

        Long userId = creatorApplyFormEntity.getUserId();

        CreatorApplyFormEntity form = creatorApplyFormMongoDao.findFirstByUserId(userId);
        if (form!=null){
            creatorApplyFormEntity.setId(form.getId());
        }else {
            creatorApplyFormEntity.setCreatedTime(LocalDateTime.now());
        }
        return creatorApplyFormMongoDao.save(creatorApplyFormEntity);
    }

    @Override
    public CreatorApplyFormEntity getCreatorApplyForm(Long userId) {

        CreatorApplyFormEntity form = creatorApplyFormMongoDao.findFirstByUserId(userId);
        if (form == null){
            form = JSONObject.parseObject(applyFormJson,CreatorApplyFormEntity.class);
        }
        return form;
    }


    final private String applyFormJson ="{\n" +
            "  \"createdTime\": \"2021-04-25 12:23:09\",\n" +
            "  \"title\": \"创作者申请表\",\n" +
            "  \"questionEntityList\": [\n" +
            "    {\n" +
            "      \"desc\": \"1、填空题啊都不错（ ）\",\n" +
            "      \"type\": \"input\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"2、单选题）\",\n" +
            "      \"type\": \"radio\",\n" +
            "      \"optionList\": [\n" +
            "        {\n" +
            "          \"key\": \"A\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"B\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"C\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"D\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"3、多选题\",\n" +
            "      \"type\": \"check\",\n" +
            "      \"optionList\": [\n" +
            "        {\n" +
            "          \"key\": \"A\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"B\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"C\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"D\",\n" +
            "          \"value\": \"选项1\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
