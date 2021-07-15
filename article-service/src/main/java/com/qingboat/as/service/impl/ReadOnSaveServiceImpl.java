package com.qingboat.as.service.impl;

import com.qingboat.as.dao.ArticleMongoDao;
import com.qingboat.as.dao.PathwayDao;
import com.qingboat.as.dao.ReadOnDao;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.ReadonEntity;
import com.qingboat.as.service.ReadOnSaveService;
import com.qingboat.as.vo.ReadOnListVo;
import com.qingboat.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReadOnSaveServiceImpl implements ReadOnSaveService {

    @Autowired
    PathwayDao pathwayDao;
    @Autowired
    ReadOnDao readOnDao;
    @Autowired
    ArticleMongoDao articleMongoDao;

    @Override
    public ApiResponse readOnSave(Integer userId, Integer contentType, String contentId, Integer height) {
        if (contentType == 1){  // 是pathway
            ApiResponse<Object> result = new ApiResponse<>();
            result.setData(null);
            Integer pathwayId = Integer.parseInt(contentId);
            ReadonEntity record = readOnDao.findRecord(userId, contentType, contentId);
            if (record != null){
                // 存在记录 说明访问过  走update方式
                Integer integer = readOnDao.updateRecord(record);
                if (integer == 1){
                    return result;
                }
            }else{
                // 查询创作者
                Integer creatorId = pathwayDao.getCreatorIdByPathwayId(pathwayId);
                ReadonEntity readonEntity = new ReadonEntity(userId,contentType,contentId,creatorId,height);
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
                Integer integer = readOnDao.updateRecord(record);
                if (integer == 1){
                    return result;
                }
            }else {
                ArticleEntity articleEntity = articleMongoDao.findArticleEntityById(contentId);
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

    @Override
    public ReadOnListVo readOnList(Integer userId,Integer page,Integer pageSize) {
//        readOnDao.
        return null;
    }
}
