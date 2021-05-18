package com.qingboat.us.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.task.DelayQueueManager;
import com.qingboat.us.dao.CreatorApplyFormMongoDao;
import com.qingboat.us.dao.UserProfileDao;
import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.service.UserService;
import com.qingboat.us.task.ApplyCreatorTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private CreatorApplyFormMongoDao creatorApplyFormMongoDao;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private FeishuService feishuService;

    @Value("${business-domain-pathway-backend}")
    private String businessDomainPathwayBackend;

    @Override
    public UserProfileEntity applyCreator(Long userId) {

        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        UserProfileEntity userProfileEntity = userProfileDao.selectOne(queryWrapper);
        if (userProfileEntity !=null){
            if(1== userProfileEntity.getRole() && 1 == userProfileEntity.getStatus()){

            }else {
                userProfileEntity.setRole(1);
                userProfileEntity.setStatus(0);
                userProfileDao.updateById(userProfileEntity);

                // 发飞书通知
                FeishuService.TextBody textBody = new FeishuService.TextBody(
                        new StringBuilder().append("===创者者申请===").append("\n")
                                .append("操作link：").append(this.businessDomainPathwayBackend+"/api/admin/apps/userprofile/").append("\n")
                                .append("创作者Id：").append(userId).append("\n")
                                .append("创者者昵称：").append(userProfileEntity.getNickname()).append("\n").toString());
                feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);

            }
        }
        return null;

    }

    @Override
    public UserProfileEntity getUserProfile(Long uid) {
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",uid);
        UserProfileEntity userProfileEntity = userProfileDao.selectOne(queryWrapper);
        return userProfileEntity;
    }
    @Override
    public UserProfileEntity getUserProfileByProfileKey(String profileKey){
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("profile_key",profileKey);
        UserProfileEntity userProfileEntity = userProfileDao.selectOne(queryWrapper);
        return userProfileEntity;
    }


    @Override
    public Boolean confirmCreator(Long applyUserId, Boolean rst) {
        if (rst == null){
            throw new BaseException(500,"操作失败：请求参数里审批结果为空。");
        }
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",applyUserId);

        UserProfileEntity userProfileEntity = new UserProfileEntity();
        if (rst){
            userProfileEntity.setStatus(1);
        }else {
            userProfileEntity.setStatus(-1);
        }
        userProfileEntity.setRole(1);

        int updateCount = userProfileDao.update(userProfileEntity,queryWrapper);
        if (updateCount == 1){

            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity) {
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userProfileEntity.getUserId());

        userProfileEntity.setRole(null);
        userProfileEntity.setStatus(null);
        userProfileEntity.setCreatedAt(null);
        userProfileEntity.setPhone(null);

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
