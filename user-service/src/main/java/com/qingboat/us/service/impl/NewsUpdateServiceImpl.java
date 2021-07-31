package com.qingboat.us.service.impl;

import com.qingboat.base.baseEnum.PathwayActionEnum;
import com.qingboat.us.dao.*;
import com.qingboat.us.entity.*;
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
    ArticlePrefaceDao articlePrefaceDao;
    @Autowired
    ArticlesummaryDao articlesummaryDao;
    @Autowired
    NodeDao nodeDao;
    @Autowired
    ArticletodocellDao articletodocellDao;
    @Autowired
    HighlightDao highlightDao;
    @Autowired
    HighlightnoteDao highlightnoteDao;


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
            String profileName = "";    //店铺名称
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
            //填充 更新动作信息
            Date pathwayUpdateTime = newsUpdateCardVO.getUpdateTime();   //如果这个最大就代表是添加内容(添加章节，添加课次，添加内容)
            //先判断该pathway中是否有article
            List<Integer> articleIds = nodeDao.getAllArticleIdByPathwayId(pathwayId);
            if (articleIds ==  null || articleIds.size() == 0){
                newsUpdateCardVO.setAction(PathwayActionEnum.PUBLISH.ordinal());
            }else {
                Date defaultDate = new Date(newsUpdateCardVO.getUpdateTime().getTime());
                Date prefaceTime = new Date(0l);    //推荐
                Date summaryTime = new Date(0l);    //总结
                Date articlecellTime = new Date(0l); //行动建议
                Date highlightTime = new Date(0l);  //划重点
                Date ideaTime = new Date(0l);       //想法
                //推荐语
                ArticlePrefaceEntity newestArticlePrefaceByPathwayId = articlePrefaceDao.getNewestArticlePrefaceByPathwayId(pathwayId);
                if (newestArticlePrefaceByPathwayId != null){
                    prefaceTime = newestArticlePrefaceByPathwayId.getUpdatedAt();
                }
                //总结  (根据userId 和 pathwayId 找寻 最新的总结)
                ArticlesummaryEntity articlesummaryEntity = articlesummaryDao.getNewestArticleSummaryByUserIdAndPathwayId(creatorId, pathwayId);
                if (articlesummaryEntity != null){
                    summaryTime = articlesummaryEntity.getUpdatedAt();
                }
                //行动建议
                ArticletodocellEntity articlecellEntity = articletodocellDao.getNewestArticletodocellEntityByArticlesAndUserId(articleIds, creatorId);
                if (articlecellEntity != null){
                    articlecellTime = articlecellEntity.getUpdatedAt();
                }

                //重点
                HighlightEntity highlightEntity = highlightDao.getNewestHighlightEntity(articleIds, creatorId);
                if (highlightEntity != null){
                    highlightTime = highlightEntity.getUpdatedAt();
                }
                //想法
                List<Integer> allHighlightIds = highlightDao.getAllHighlightIds(articleIds, creatorId); //得到本pathyway的本用户的所有重点id。从着里面找最新想法
                HighlightnoteEntity highlightnoteEntity = null;
                if (allHighlightIds != null && allHighlightIds.size() != 0 ){
                    highlightnoteEntity  = highlightnoteDao.getNewestHighlightnoteEntityByHighlightIdsAndUserId(allHighlightIds,creatorId);
                    if (highlightnoteEntity != null){
                        ideaTime = highlightnoteEntity.getUpdatedAt();
                    }
                }
                List<Date> dateList = new ArrayList<>();
                dateList.add(prefaceTime);
                dateList.add(summaryTime);
                dateList.add(articlecellTime);
                dateList.add(highlightTime);
                dateList.add(ideaTime);
                dateList.add(defaultDate);
                Date newestDate = dateComparison(dateList);
                if (newestDate == prefaceTime){
                    //推荐语
                    if (isEqual(newestArticlePrefaceByPathwayId.getCreatedAt(),newestArticlePrefaceByPathwayId.getUpdatedAt())){
                        newsUpdateCardVO.setAction(PathwayActionEnum.NEWPREFACE.ordinal());
                    }else {
                        newsUpdateCardVO.setAction(PathwayActionEnum.UPDATEPREFACE.ordinal());
                    }
                    newsUpdateCardVO.setActionContent(newestArticlePrefaceByPathwayId.getPrefaceText());
                    newsUpdateCardVO.setUpdateTime(newestArticlePrefaceByPathwayId.getUpdatedAt());
                }else if (newestDate == summaryTime){
                    newsUpdateCardVO.setAction(PathwayActionEnum.NEWSUMMARY.ordinal());
                    newsUpdateCardVO.setUpdateTime(articlesummaryEntity.getUpdatedAt());
                    newsUpdateCardVO.setActionContent(articlesummaryEntity.getSummaryText());
                }else if (newestDate == articlecellTime){
                    newsUpdateCardVO.setAction(PathwayActionEnum.NEWTODO.ordinal());
                    newsUpdateCardVO.setUpdateTime(articlecellEntity.getUpdatedAt());
                    newsUpdateCardVO.setActionContent(articlecellEntity.getTodoText());
                }else if (newestDate == highlightTime){
                    newsUpdateCardVO.setAction(PathwayActionEnum.NEWHIGHLIGHT.ordinal());
                    newsUpdateCardVO.setUpdateTime(highlightEntity.getUpdatedAt());
                    newsUpdateCardVO.setActionContent(highlightEntity.getText());
                }else if (newestDate == ideaTime){
                    newsUpdateCardVO.setAction(PathwayActionEnum.NEWIDEA.ordinal());
                    newsUpdateCardVO.setUpdateTime(highlightnoteEntity.getUpdatedAt());
                    newsUpdateCardVO.setActionContent(highlightnoteEntity.getNoteText());
                }else if (newestDate == defaultDate ){
                    newsUpdateCardVO.setAction(PathwayActionEnum.PUBLISH.ordinal());
                    newsUpdateCardVO.setActionContent("");
                }
            }

            //

        }
        //获取newsletter的信息
        List<String> list = new ArrayList<>();
        for (Integer i: allCreatorIdsByUserId) {
            list.add(i+ "");
        }
        //这里做了一步转换，把原来的int 类型转换成了String  因为int类型 查不到数据
        List<ArticleEntity> articleEntitiesByAuthorIds = articleMongoDao.findPublishArticleProfileInfoByAuthorIds(list);
        for (int i = 0; i < allCreatorIdsByUserId.size(); i++) {
            System.out.println(allCreatorIdsByUserId.get(i));
        }
        for (int i = 0; i < articleEntitiesByAuthorIds.size(); i++) {
            System.out.println(articleEntitiesByAuthorIds.get(i).getTitle());
        }
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
            newsUpdateCardVO.setTitle(articleEntity.getTitle());
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
        result.setTotal(pathwayInfoByCreatorIds.size());
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
            Integer creatorId = userProfileVO1.getUserId();  //注意  这里是getUserId  不是getId  id 没什么毛用
            Date lastUpdateTimeByCreator = pathwayDao.getLastUpdateTimeByCreator(creatorId);
            userProfileVO1.setHaveUpdate(false);
            if (lastUpdateTimeByCreator != null){
                Date lastAccessTimeByUserIdAndCreatorId = lastAccessRecordsDao.findLastRecordTimeWithCreatorId(userId,creatorId);
                if (lastAccessTimeByUserIdAndCreatorId == null || lastUpdateTimeByCreator.compareTo(lastAccessTimeByUserIdAndCreatorId) == 1 ){
                    userProfileVO1.setHaveUpdate(true);
                    continue;
                }
            }
            List<ArticleEntity> articleEntities = articleMongoDao.findPublishArticleProfileInfoByAuthorId(creatorId + "");
            if (articleEntities != null && articleEntities.size() != 0){
                LocalDateTime updatedTime = articleEntities.get(0).getUpdatedTime();   //取第一个值当作初始值  循环 获取到最新的时间  比较并交换
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

    public Date dateComparison(List<Date> list){
        if (list == null || list.size() == 0){
            return null;
        }
        Date maxDate = list.get(0);
        for (Date temp:list) {
            if (temp.compareTo(maxDate) > 0){
                maxDate = temp;
            }
        }
        return maxDate;
    }

    public boolean isEqual(Date d1,Date d2){
        long time = d1.getTime();
        long time1 = d2.getTime();
        if (Math.abs(time - time1) < 1000){
            return true;
        }
        return false;
    }



}
