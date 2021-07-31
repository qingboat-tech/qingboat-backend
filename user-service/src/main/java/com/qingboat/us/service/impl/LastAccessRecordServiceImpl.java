package com.qingboat.us.service.impl;

import com.qingboat.base.api.ApiResponse;
import com.qingboat.us.dao.ArticleMongoDao;
import com.qingboat.us.dao.LastAccessRecordsDao;
import com.qingboat.us.dao.PathwayDao;
import com.qingboat.us.entity.ArticleEntity;
import com.qingboat.us.entity.LastAccessRecordsEntity;
import com.qingboat.us.service.LastAccessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LastAccessRecordServiceImpl implements LastAccessRecordService {
    
    @Autowired
    LastAccessRecordsDao lastAccessRecordsDao;
    @Autowired
    PathwayDao pathwayDao;
    @Autowired
    ArticleMongoDao articleMongoDao;


    /**
     *
     * @param userId 访问者自身的id
     * @param type  1 表示pathway   2表示 newsletter
     * @param targetId  内容（type）的id
     * @return
     */
    @Override
    public ApiResponse lastAccessRecord(Integer userId, Integer type, String targetId) {
        Integer creatorId = 0;
        if (type == 1){
            //pathway
            creatorId = pathwayDao.getCreatorIdByPathwayId(Integer.parseInt(targetId));
        }else if (type == 2){
            //newsletter
            ArticleEntity articleEntity = articleMongoDao.findArticleEntityById(targetId);
            String authorId = articleEntity.getAuthorId();
            creatorId = Integer.parseInt(authorId);
        }else if (type == 3){
            creatorId = Integer.parseInt(targetId);
            targetId = "";
        }
        LastAccessRecordsEntity lastAccessRecordsEntity = new LastAccessRecordsEntity(userId,type,targetId);
        lastAccessRecordsEntity.setCreatorId(creatorId);
        if (lastAccessRecordsDao.findRecord(userId,type,targetId) == 0){
            //新纪录
//            lastAccessRecordsEntity.setCreatorId();
            lastAccessRecordsDao.insertLastAccessRecords(lastAccessRecordsEntity);
        }else {
            lastAccessRecordsDao.updateLastAccessTime(userId,type,targetId);
        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(null);
        return apiResponse;
    }
}
