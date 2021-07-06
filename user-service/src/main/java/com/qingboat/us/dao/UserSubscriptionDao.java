package com.qingboat.us.dao;

import com.qingboat.us.DTO.SubscriptionAndFollowDTO;
import com.qingboat.us.vo.UserProfileVO1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *  根据创作者ID查询读者Id
 *  根据读者ID查询创作者ID
 */
@Mapper
public interface UserSubscriptionDao {
    public List<SubscriptionAndFollowDTO> getUserIdsByCreatorId(@Param("creatorId")Integer creatorId);

    public List<UserProfileVO1> getUserIdsByCreatorIdWithStartAndEnd(@Param("creatorId")Integer creatorId,
                                                                     @Param("start") Integer start,
                                                                     @Param("end") Integer end);

    public List<Integer> getCreatorIdsByUserIdWithStartAndEnd(@Param("userId") Integer userId,
                                                                     @Param("start") Integer start,
                                                                     @Param("end") Integer end );
}
