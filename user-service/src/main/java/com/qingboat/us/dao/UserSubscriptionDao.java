package com.qingboat.us.dao;

import com.qingboat.us.DTO.SubscriptionAndFollowDTO;
import com.qingboat.us.vo.UserProfileVO1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *  根据创作者ID查询读者Id
 *  根据读者ID查询创作者ID
 */
@Mapper
public interface UserSubscriptionDao {

    /**
     *  pathway + newsletters
     * @param creatorId
     * @return
     */
    public List<SubscriptionAndFollowDTO> getUserIdsByCreatorId(@Param("creatorId")Integer creatorId);

    public List<UserProfileVO1> getUserIdsByCreatorIdWithStartAndEnd(@Param("creatorId")Integer creatorId,
                                                                     @Param("start") Integer start,
                                                                     @Param("end") Integer end);

    /**
     *  用于判断 某一userId 是否订阅了某一creator  (newsletters)
     * @param userId
     * @param creatorId
     * @return
     */
    @Select("select count(*) from apps_usersubscription where subscriber_id = #{userId} and creator_id =#{creatorId}")
    public Integer isSubscriptionRelationship(@Param("userId") Integer userId,@Param("creatorId") Integer creatorId);


    /**
     *  pathway + newsletters
     * @param userId
     * @param start
     * @param length
     * @return
     */
    public List<Integer> getCreatorIdsByUserIdWithStartAndEnd(@Param("userId") Integer userId,
                                                                     @Param("start") Integer start,
                                                                     @Param("length") Integer length );


    /**
     *  根据user 查询ta订阅的所有创作者 ids  （newsletters）
     * @param userId
     * @param start
     * @param length
     * @return
     */
    @Select("select creator_id from apps_usersubscription where subscriber_id = #{userId} limit #{start},#{length}")
    public List<Integer> getCreatorIdsByUserIdWithStartAndEnd_newsletters(@Param("userId") Integer userId,
                                                                          @Param("start") Integer start,
                                                                          @Param("length") Integer length);

    /**
     *  根据user 查询ta订阅的所有创作者 ids  （newsletters）
     * @param userId
     * @return
     */
    @Select("select creator_id from apps_usersubscription where subscriber_id = #{userId}")
    public List<Integer> getAllCreatorIdsByUserIdWithStartAndEnd_newsletters(@Param("userId") Integer userId);

    /**
     *  根据创作者 分页查询 订阅 此创作者的userIds  （newsletters）
     */
    @Select("select subscriber_id from apps_usersubscription where creator_id = #{creatorId} limit #{start},#{length}")
    public List<Integer> getUserIdsByCreatorIdWithStartAndEnd_newsletters(@Param("creatorId") Integer creatorId,
                                                                          @Param("start") Integer start,
                                                                          @Param("length") Integer length);



    /**
     *  根据创作者 查询 订阅 此创作者的 用户总数  （newsletters）
     */
    @Select("select count(subscriber_id) from apps_usersubscription where creator_id = #{creatorId}")
    public Integer getUserCountByCreatorId_newsletters(@Param("creatorId") Integer creatorId);




}