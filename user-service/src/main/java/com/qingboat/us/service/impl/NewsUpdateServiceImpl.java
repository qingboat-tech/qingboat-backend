package com.qingboat.us.service.impl;

import com.qingboat.us.dao.*;
import com.qingboat.us.entity.ArticleEntity;
import com.qingboat.us.service.NewsUpdateService;
import com.qingboat.us.vo.NewsUpdateCardVO;
import com.qingboat.us.vo.NewsUpdateCardVOList;
import com.qingboat.us.vo.UserProfileVO1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsUpdateServiceImpl implements NewsUpdateService {
    @Autowired
    ArticleMongoDao articleMongoDao;
    @Autowired
    UserSubscriptionDao userSubscriptionDao;
    @Autowired
    RelationshipBetweenUsersAndCreatorsDao rbuac;
    @Autowired
    PathwayDao pathwayDao;
    @Autowired
    UserProfileDao userProfileDao;

    @Override
    public NewsUpdateCardVOList getNewsUpdateCardVOList(Integer userId, Integer page, Integer pageSize) {
        int start =  (page - 1) * pageSize;
        int length = pageSize;
        //这里的逻辑是  需要获取所订阅/购买的 所有的作者 包括 pathway和newsletter 根据这些creator 展示这些creator的最新动态。
        List<Integer> allCreatorIdsByUserId = rbuac.getAllCreatorIdsByUserId(userId);
        if (allCreatorIdsByUserId == null || allCreatorIdsByUserId.size() == 0){
            return null;
        }
        //拿到所有创作者 （店铺）的所需信息
        List<NewsUpdateCardVO> userProfileByIdsForNewsUpdateCardVO = userProfileDao.getUserProfileByIdsForNewsUpdateCardVO(allCreatorIdsByUserId);
        //获取这些创作着的 所有的 内容，按时间倒序
        //首先 获取pathway 的 内容
        List<NewsUpdateCardVO> pathwayInfoByCreatorIds = pathwayDao.pathwayInfoByCreatorIds(allCreatorIdsByUserId);


        List<ArticleEntity> articleEntitiesByAuthorIds = articleMongoDao.findArticleProfileInfoByAuthorIds(allCreatorIdsByUserId);


        return null;
    }
}
