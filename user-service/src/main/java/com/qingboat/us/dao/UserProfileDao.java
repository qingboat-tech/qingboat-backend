package com.qingboat.us.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.vo.NewsUpdateCardVO;
import com.qingboat.us.vo.TaSubscriptionNewslettersVO;
import com.qingboat.us.vo.UserProfileVO1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserProfileDao extends BaseMapper<UserProfileEntity> {

    UserProfileEntity findByUserId(Long userId);



    /**
     * 获取所有的创作者id
     * @return
     */
    @Select("select user_id from apps_userprofile where role = 1")
    List<Integer> getCreatorIds();


    List<UserProfileVO1> getUserProfileByIds(@Param("ids")List<Integer> ids);


    List<NewsUpdateCardVO> getUserProfileByIdsForNewsUpdateCardVO(@Param("ids")List<Integer> ids);

//    @Select("select * from apps_userprofile where user_id = #{userId}")
    List<TaSubscriptionNewslettersVO> getTaSubscriptionNewsletters(@Param("ids")List<Integer> ids);

    @Update("update apps_userprofile set email = #{email} where user_id = #{userId}")
    Integer updateEmailUserprofile(@Param("email")String email,@Param("userId")Integer userId);

    @Select("select count(*) from apps_userprofile where email = #{email}")
    Integer countByEmail(@Param("email")String email);



}
