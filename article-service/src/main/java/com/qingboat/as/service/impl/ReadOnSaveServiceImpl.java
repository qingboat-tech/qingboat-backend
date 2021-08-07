package com.qingboat.as.service.impl;

import com.qingboat.api.vo.UserProfileVo;
import com.qingboat.as.dao.*;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.ReadonEntity;
import com.qingboat.as.service.ReadOnSaveService;
import com.qingboat.as.vo.ReadOnListVo;
import com.qingboat.as.vo.ReadOnVo;
import com.qingboat.as.vo.UserProfileAndPathwayInfoVo;
import com.qingboat.as.vo.UserProfileInfoVo;
import com.qingboat.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReadOnSaveServiceImpl implements ReadOnSaveService {

    @Autowired
    PathwayDao pathwayDao;
    @Autowired
    ReadOnDao readOnDao;
    @Autowired
    ArticleMongoDao articleMongoDao;
    @Autowired
    ArticleDao articleDao;
    @Autowired
    ArticleMarkCompletedDao articleMarkCompletedDao;
    @Autowired
    UserProfileDao userProfileDao;

    @Override
    public ApiResponse readOnSave(Integer userId, Integer contentType, String contentId, Integer height,Integer pathwayId) {
        if (contentType == 1){  // 是pathway
            ApiResponse<Object> result = new ApiResponse<>();
            result.setData(null);
//            Integer articleId = Integer.parseInt(contentId);
            ReadonEntity record = readOnDao.findRecordWithPathwayId(userId, contentType,contentId,pathwayId);
            if (record != null){
                // 存在记录 说明访问过  走update方式
                record.setHeight(height);
                Integer integer = readOnDao.updateRecord(record);
                if (integer == 1){
                    return result;
                }
            }else{
                // 查询创作者
                Integer creatorId = pathwayDao.getCreatorIdByPathwayId(pathwayId);
                ReadonEntity readonEntity = new ReadonEntity(userId,contentType,contentId,creatorId,height,pathwayId);
                int i = readOnDao.insertRecord(readonEntity);
                if (i == 1){
                    return result;
                }
            }
        }else if (contentType == 2){
            // 是newsletter
            ApiResponse<Object> result = new ApiResponse<>();
            result.setData(null);
            ReadonEntity record = readOnDao.findRecord(userId, contentType, contentId);
            if (record != null){
                // 存在记录 说明访问过  走update方式
                record.setHeight(height);
                Integer integer = readOnDao.updateRecord(record);
                if (integer == 1){
                    return result;
                }
            }else {
                ArticleEntity articleEntity = articleMongoDao.findArticleEntityById(contentId);
                if (articleEntity == null){
                    result.setData("异常数据");
                    return result;
                }
                Integer authorId = Integer.parseInt(articleEntity.getAuthorId());
                ReadonEntity readonEntity = new ReadonEntity(userId,contentType,contentId,authorId,height);
                int i = readOnDao.insertRecord(readonEntity);
                if (i == 1){
                    return result;
                }
            }
        }
        ApiResponse<Object> result = new ApiResponse<>();
        result.setData("异常数据");
        return result;
    }

    //继续阅读卡片list
    @Override
    public ReadOnListVo readOnList(Integer userId,Integer page,Integer pageSize) {
        List<ReadonEntity> readonEntities = readOnDao.selectAllReadOnListByUserId(userId);  //
        List<ReadOnVo> list = new ArrayList(readonEntities.size() << 1);
        for (ReadonEntity readonEntity: readonEntities) {
            int type = readonEntity.getContentType();
            ReadOnVo readOnVo = new ReadOnVo();
            readOnVo.setContentType(type);
            readOnVo.setCreatorId(readonEntity.getCreatorId());
            readOnVo.setContentId(readonEntity.getContentId());
            readOnVo.setPathwayId(readonEntity.getPathwayId());
            readOnVo.setLastReadTime(readonEntity.getUpdatedAt());
            if (type == 1){  //填充pathway信息
                Integer contentId = Integer.parseInt(readonEntity.getContentId());  //去掉 pathway中标记已完成的
                Integer integer = articleMarkCompletedDao.judgeMarkCompleted(userId, contentId);
                if (integer == 1){
                    // 表示已标记完成  那么就在继续阅读list中删除(不显示)
                    continue;
                }
                ArticleEntity articleEntity = articleDao.selectArticleById(contentId);
                readOnVo.setTitle(articleEntity.getTitle());
                readOnVo.setDesc(articleEntity.getDesc());
                //地址添加
                System.out.println("查找pathway信息");
                System.out.println("articleEntity.getEntryUrl()" + articleEntity.getEntryUrl());
                readOnVo.setArticleAddress(articleEntity.getEntryUrl());
                UserProfileAndPathwayInfoVo temp = pathwayDao.getUserProfileInfoAndPathwayInfoByPathwayId(readonEntity.getPathwayId());


                readOnVo.setProfileName(temp.getProfileName());
                readOnVo.setNickname(temp.getNickname());
                readOnVo.setHeadimgUrl(temp.getHeadimgUrl());
                readOnVo.setPathwayName(temp.getPathwayName());
                readOnVo.setHeight(readonEntity.getHeight());
                readOnVo.setCreatorimgUrl(temp.getCreatorImgUrl());
            }else if (type == 2){
                //填充newsletter 信息
                String contentId = readonEntity.getContentId();
                ArticleEntity articleEntity = articleMongoDao.findBaseInfoById(contentId);
                readOnVo.setTitle(articleEntity.getTitle());
                Integer authorId = Integer.parseInt(articleEntity.getAuthorId());
                readOnVo.setDesc(articleEntity.getDesc());
                readOnVo.setHeight(readonEntity.getHeight());
                UserProfileInfoVo userProfileInfoById = userProfileDao.getUserProfileInfoById(authorId);
                readOnVo.setNickname(userProfileInfoById.getNickname());
                readOnVo.setProfileName(userProfileInfoById.getProfileName());
                readOnVo.setCreatorimgUrl(userProfileInfoById.getCreatorImgUrl());
                readOnVo.setHeadimgUrl(userProfileInfoById.getHeadimgUrl());
            }
            list.add(readOnVo);
        }
        ReadOnListVo result = new ReadOnListVo();
        int start = (page - 1) * pageSize;
        int end = start + pageSize;
        int total = list.size();
        if (start > total){
            result.setList(null);
            result.setTotal(0);
            return result;
        }
        result.setList(list.subList(start,end > total ? total : end));
        result.setTotal(total);
        return result;
    }
}
