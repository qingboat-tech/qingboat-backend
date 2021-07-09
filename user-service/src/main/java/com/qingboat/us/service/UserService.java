package com.qingboat.us.service;

import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.vo.TaSubscriptionNewslettersVO;
import com.qingboat.us.vo.UserProfileVO1;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {


    UserProfileEntity applyCreator(Long uid);

    UserProfileEntity getUserProfile(Long uid);

    UserProfileEntity getUserProfileByProfileKey(String profileKey);

    UserProfileEntity getUserProfileByLuckyCode(String profileKey);


    Boolean confirmCreator(Long applyUserId,Boolean rst);

    UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity);

    CreatorApplyFormEntity saveCreatorApplyForm(CreatorApplyFormEntity creatorApplyFormEntity);

    CreatorApplyFormEntity getCreatorApplyForm(Long userId);

    Integer getCount_UserIdsByCreatorOnNewslettersAndPathway(Integer creatorId);

    List<UserProfileVO1> getUserProfileByCreatorOnNewslettersAndPathway(Integer creatorId, Integer page, Integer pageSize);
    //获取用户订阅的所有的创作者Id
    List<Integer> getCreatorsIdsByUserOnNewslettersAndPathwayWithStartAndEnd(Integer userId, Integer start, Integer length);
    List<UserProfileVO1> getUserProfileByIds(List<Integer> ids);

    List<TaSubscriptionNewslettersVO> getTaSubscriptionNewslettersVO(Integer loginId,Integer userId,Integer page,Integer pageSize);

}
