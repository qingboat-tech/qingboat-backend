package com.qingboat.as.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserSubscriptionDao extends BaseMapper<UserSubscriptionEntity> {

    @Select("select * from apps_usersubscription where subscriber_id = #{subscriberId} and creator_id = #{creatorId} and TIMESTAMPDIFF(second,expire_date,NOW()) < 0 ")
    public UserSubscriptionEntity getUserSubscriptionEntity(@Param("subscriberId")Integer subscriberId,@Param("creatorId")Integer creatorId);

}