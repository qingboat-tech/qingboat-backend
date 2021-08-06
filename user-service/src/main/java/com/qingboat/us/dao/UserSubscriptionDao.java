package com.qingboat.us.dao;

import com.qingboat.us.DTO.SubscriptionAndFollowDTO;
import com.qingboat.us.DTO.UserSubscriptionDTO;
import com.qingboat.us.vo.UserProfileVO1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;

import java.util.Date;
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
    List<SubscriptionAndFollowDTO> getUserIdsByCreatorId(@Param("creatorId")Integer creatorId);

    List<UserProfileVO1> getUserIdsByCreatorIdWithStartAndEnd(@Param("creatorId")Integer creatorId,
                                                              @Param("start") Integer start,
                                                              @Param("length") Integer length);

    int countUsersByCreatorId(@Param("creatorId")Integer creatorId);

    /**
     *  用于判断 某一userId 是否订阅了某一creator  (newsletters)
     * @param userId
     * @param creatorId
     * @return
     */
    @Select("select count(*) from apps_usersubscription where subscriber_id = #{userId} and creator_id =#{creatorId}")
    Integer isSubscriptionRelationship(@Param("userId") Integer userId,@Param("creatorId") Integer creatorId);


    /**
     *  pathway + newsletters
     * @param userId
     * @param start
     * @param length
     * @return
     */
    List<Integer> getCreatorIdsByUserIdWithStartAndEnd(@Param("userId") Integer userId,
                                                                     @Param("start") Integer start,
                                                                     @Param("length") Integer length );


    /**
     *  pathway + newsletters  根据用户id查询所有的 ta所订阅/购买的所有创作者id （pathway + newsletter）
     * @param userId
     * @return
     */

    List<Integer> getCreatorIdsByUserId(@Param("userId") Integer userId);

    Integer countCreatorIdsByUserId(@Param("userId")Integer userId);








    /**
     *  根据user 查询ta订阅的所有创作者 ids  （newsletters）
     * @param userId
     * @param start
     * @param length
     * @return
     */
    @Select("select creator_id from apps_usersubscription where subscriber_id = #{userId} limit #{start},#{length}")
    List<Integer> getCreatorIdsByUserIdWithStartAndEnd_newsletters(@Param("userId") Integer userId,
                                                                          @Param("start") Integer start,
                                                                          @Param("length") Integer length);

    /**
     *  根据user 查询ta订阅的所有创作者 ids  （newsletters）
     * @param userId
     * @return
     */
    @Select("select creator_id from apps_usersubscription where subscriber_id = #{userId}")
    List<Integer> getAllCreatorIdsByUserIdWithStartAndEnd_newsletters(@Param("userId") Integer userId);

    /**
     *  根据创作者 分页查询 订阅 此创作者的userIds  （newsletters）
     */
    @Select("select subscriber_id from apps_usersubscription where creator_id = #{creatorId} limit #{start},#{length}")
    List<Integer> getUserIdsByCreatorIdWithStartAndEnd_newsletters(@Param("creatorId") Integer creatorId,
                                                                          @Param("start") Integer start,
                                                                          @Param("length") Integer length);



    /**
     *  根据创作者 查询 订阅 此创作者的 用户总数  （newsletters）
     */
    @Select("select count(subscriber_id) from apps_usersubscription where creator_id = #{creatorId}")
    Integer getUserCountByCreatorId_newsletters(@Param("creatorId") Integer creatorId);

    @Select("select count(*) from apps_usersubscription where subscriber_id = #{userId} and TIMESTAMPDIFF(second,cast(NOW() as datetime),expire_date) > 0 and TIMESTAMPDIFF(second,cast(NOW() as datetime),start_date) < 0")
    Integer countAllNewsletterByUserId(@Param("userId") Integer userId);

    @Select("select * from apps_usersubscription where subscriber_id = #{subscriberId} and creator_id = #{creatorId} and TIMESTAMPDIFF(second,cast(NOW() as datetime),expire_date) > 0 and TIMESTAMPDIFF(second,cast(NOW() as datetime),start_date) < 0")
    List<UserSubscriptionDTO> listUserSubscription(@Param("subscriberId")Integer subscriberId,@Param("creatorId") Integer creatorId);

    @Select("select start_date from apps_usersubscription where subscriber_id = #{subscriberId} and creator_id = #{creatorId}")
    Date getNewestDate(@Param("subscriberId")Integer subscriberId,@Param("creatorId")Integer creatorId);




}
