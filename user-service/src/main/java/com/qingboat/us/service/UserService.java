package com.qingboat.us.service;

import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.vo.*;
import org.springframework.data.domain.Page;

import java.util.Date;
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

    UserProfileVO1List getUserProfileByCreatorOnNewslettersAndPathway(Integer creatorId, Integer page, Integer pageSize);
    //获取用户订阅的所有的创作者Id
    List<Integer> getCreatorsIdsByUserOnNewslettersAndPathwayWithStartAndEnd(Integer userId, Integer start, Integer length);

    List<UserProfileVO1> getUserProfileByIds(List<Integer> ids);

    TaSubscriptionNewslettersWithTotalVO getTaSubscriptionNewslettersVO(Integer loginId, Integer userId, Integer page, Integer pageSize);

    AccountInfoVO getAccountInfo(Integer userId);

    Boolean sendEmailVerificationCode(Integer userId,String email);

    Boolean verificationCodeWhitEmail(Integer userId,String email,String code);

    CountVO countCreateContent(Integer userId,Integer type);

    //最早关注或订阅的时间
    Date firstFollowTimeByUserIdAndCreatorId(Integer userId,Integer creatorId);

    TaSubscriptionNewslettersWithTotalVO getCreatorContent(Integer userId,Integer page,Integer pageSize);





}
