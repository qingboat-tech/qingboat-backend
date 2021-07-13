package com.qingboat.us.service.impl;

import com.qingboat.us.dao.*;
import com.qingboat.us.entity.ArticleEntity;
import com.qingboat.us.redis.RedisUtil;
import com.qingboat.us.service.NewsUpdateService;
import com.qingboat.us.vo.NewsUpdateCardVO;
import com.qingboat.us.vo.NewsUpdateCardVOList;
import com.qingboat.us.vo.UserProfileVO1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

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
    @Autowired
    LikePathwayDao likePathwayDao;
    @Autowired
    FollowPathwayDao followPathwayDao;
    @Autowired
    LastAccessRecordsDao lastAccessRecordsDao;


    @Autowired
    private RedisUtil redisUtil;
    private static final String USER_STAR_PRE ="USER_STAR_";

    @Override
    public NewsUpdateCardVOList getNewsUpdateCardVOList(Integer userId, Integer page, Integer pageSize) {
        int start =  (page - 1) * pageSize;
        int length = pageSize;
        NewsUpdateCardVOList result = new NewsUpdateCardVOList();
        //这里的逻辑是  需要获取所订阅/购买的 所有的作者 包括 pathway和newsletter 根据这些creator 展示这些creator的最新动态。
        List<Integer> allCreatorIdsByUserId = rbuac.getAllCreatorIdsByUserId(userId);
        if (allCreatorIdsByUserId.contains(userId)){
            allCreatorIdsByUserId.remove(userId);
        }

        if (allCreatorIdsByUserId == null || allCreatorIdsByUserId.size() == 0){
            result.setTotal(0);
            result.setNewsUpdateCardVOList(null);
            return result;
        }
        //拿到所有创作者 （店铺）的所需信息
        List<NewsUpdateCardVO> userProfileByIdsForNewsUpdateCardVO = userProfileDao.getUserProfileByIdsForNewsUpdateCardVO(allCreatorIdsByUserId);

        //获取这些创作着的 所有的 内容，按时间倒序
        //首先 获取pathway 的 内容
        List<NewsUpdateCardVO> pathwayInfoByCreatorIds = pathwayDao.pathwayInfoByCreatorIds(allCreatorIdsByUserId);
        for (NewsUpdateCardVO newsUpdateCardVO: pathwayInfoByCreatorIds) {
            // contentType 1 表示 pathway
            newsUpdateCardVO.setContentType(1);
            Integer pathwayId = Integer.parseInt(newsUpdateCardVO.getContentId());
            newsUpdateCardVO.setLikeCount(likePathwayDao.likeCountByPathwayId(pathwayId));
            newsUpdateCardVO.setIsPurchase(followPathwayDao.judgeUserIsFollowSomePathway(pathwayId,userId) >= 1 ? true : false);
            newsUpdateCardVO.setIsLiked(likePathwayDao.judgeSomeUserIsLiked(userId,Integer.parseInt(newsUpdateCardVO.getContentId())) == 0 ? false : true );
            String profileName = "";  //店铺名称
            String creatorImgUrl = "" ; //店铺头像
            String nickname = "";
            String headimgUrl = "";
            Integer creatorId = newsUpdateCardVO.getCreatorId();
            for (NewsUpdateCardVO temp :userProfileByIdsForNewsUpdateCardVO) {
                if (temp.getCreatorId() == creatorId){
                    profileName = temp.getProfileName();
                    creatorImgUrl = temp.getCreatorImgUrl();
                    nickname = temp.getNickname();
                    headimgUrl = temp.getHeadimgUrl();
                }
            }
            newsUpdateCardVO.setProfileName(profileName);
            newsUpdateCardVO.setCreatorImgUrl(creatorImgUrl);
            newsUpdateCardVO.setNickname(nickname);
            newsUpdateCardVO.setHeadimgUrl(headimgUrl);
        }
        //获取newsletter的信息
        List<ArticleEntity> articleEntitiesByAuthorIds = articleMongoDao.findPublishArticleProfileInfoByAuthorIds(allCreatorIdsByUserId);
        List<NewsUpdateCardVO> newsletterInfoByCreatorIds = new ArrayList<>();
        for (ArticleEntity articleEntity: articleEntitiesByAuthorIds) {
            Integer creatorId = Integer.parseInt(articleEntity.getAuthorId());
            NewsUpdateCardVO newsUpdateCardVO = new NewsUpdateCardVO();  //往这里填入信息

            String profileName = "";  //店铺名称
            String creatorImgUrl = "" ; //店铺头像
            String nickname = "";
            String headimgUrl = "";
            for (NewsUpdateCardVO temp :userProfileByIdsForNewsUpdateCardVO) {
                if (temp.getCreatorId() == creatorId){
                    profileName = temp.getProfileName();
                    creatorImgUrl = temp.getCreatorImgUrl();
                    nickname = temp.getNickname();
                    headimgUrl = temp.getHeadimgUrl();
                }
            }
            newsUpdateCardVO.setNickname(nickname);
            newsUpdateCardVO.setHeadimgUrl(headimgUrl);
            newsUpdateCardVO.setProfileName(profileName);
            newsUpdateCardVO.setCreatorImgUrl(creatorImgUrl);
            newsUpdateCardVO.setContentType(2);
            newsUpdateCardVO.setUpdateTime( Date.from(articleEntity.getUpdatedTime().minusMonths(8).atZone(ZoneId.systemDefault()).toInstant()));  //这里的时间可能会有问题，有可能会进行两次 +8 处理
            newsUpdateCardVO.setContentId(articleEntity.getId());
            newsUpdateCardVO.setCoverImgUrl(articleEntity.getImgUrl());
            newsUpdateCardVO.setDescription(articleEntity.getDesc());
            newsUpdateCardVO.setIsLiked(redisUtil.sHasKey(USER_STAR_PRE+userId,articleEntity.getId())); // 判断是否已经点赞
            newsUpdateCardVO.setLikeCount(Long.valueOf(redisUtil.size(USER_STAR_PRE+userId)).intValue());
            newsUpdateCardVO.setCreatorId(creatorId);
            newsUpdateCardVO.setIsPurchase(userSubscriptionDao.isSubscriptionRelationship(userId,creatorId) == 0 ? false : true);
            newsletterInfoByCreatorIds.add(newsUpdateCardVO);
        }
        boolean b = pathwayInfoByCreatorIds.addAll(newsletterInfoByCreatorIds);
//        Collections.addAll(pathwayInfoByCreatorIds,newsletterInfoByCreatorIds)

        Collections.sort(pathwayInfoByCreatorIds);
        if (start >= pathwayInfoByCreatorIds.size()){
            result.setTotal(0);
            result.setNewsUpdateCardVOList(null);
            return result;
        }
        List<NewsUpdateCardVO> returnList = pathwayInfoByCreatorIds.subList(start, start + length > pathwayInfoByCreatorIds.size() ? pathwayInfoByCreatorIds.size() : start + length  );
        result.setNewsUpdateCardVOList(returnList);
        result.setTotal(returnList.size());
        return result;
    }

    /**
     *
     * @param userId
     * @param list
     * @return
     */
    @Override
    public List<UserProfileVO1> haveUpdate(Integer userId, List<UserProfileVO1> list) {
        for (UserProfileVO1 userProfileVO1: list) {
            Integer creatorId = userProfileVO1.getId();
            Date lastUpdateTimeByCreator = pathwayDao.getLastUpdateTimeByCreator(creatorId);
            userProfileVO1.setHaveUpdate(false);
            if (lastUpdateTimeByCreator != null){
                Date lastAccessTimeByUserIdAndCreatorId = lastAccessRecordsDao.findLastRecordTimeWithCreatorId(userId,creatorId);
                if (lastAccessTimeByUserIdAndCreatorId == null || lastUpdateTimeByCreator.compareTo(lastAccessTimeByUserIdAndCreatorId) == 1 ){
                    userProfileVO1.setHaveUpdate(true);
                    continue;
                }
            }
            List<ArticleEntity> articleEntities = articleMongoDao.findPublishArticleProfileInfoByAuthorId(creatorId);
            if (articleEntities != null && articleEntities.size() != 0){
                LocalDateTime updatedTime = articleEntities.get(0).getUpdatedTime();
                for (ArticleEntity temp:articleEntities) {
                    updatedTime = temp.getUpdatedTime().compareTo(updatedTime) > 0 ? temp.getUpdatedTime() : updatedTime;
                }
                Date lastAccessTimeByUserIdAndCreatorId = lastAccessRecordsDao.findLastRecordTimeWithCreatorId(userId,creatorId);
                Date date = Date.from(updatedTime.atZone(ZoneId.systemDefault()).toInstant());
                if (lastAccessTimeByUserIdAndCreatorId == null || date.compareTo(lastAccessTimeByUserIdAndCreatorId) == 1 ){
                    userProfileVO1.setHaveUpdate(true);
                }
            }
        }
        return list;
    }


}
