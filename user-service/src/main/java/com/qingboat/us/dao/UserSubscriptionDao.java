package com.qingboat.us.dao;

import com.qingboat.us.DTO.SubscriptionAndFollowDTO;
import com.qingboat.us.entity.UserSubscriptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserSubscriptionDao {
    public List<SubscriptionAndFollowDTO> getUserIdsByCreatorId(@Param("creatorId")Integer creatorId);
}
