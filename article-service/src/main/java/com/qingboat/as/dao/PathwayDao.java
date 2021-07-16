package com.qingboat.as.dao;

import com.qingboat.api.vo.UserProfileVo;
import com.qingboat.as.vo.UserProfileAndPathwayInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface PathwayDao {

    @Select("select author_id  from apps_pathway where id = #{targetId}")
    public Integer getCreatorIdByPathwayId(@Param("targetId")Integer targetId);

    @Select("select apps_userprofile.nickname,apps_userprofile.profile_name profileName" +
            ",apps_userprofile.headimg_url headimgUrl,apps_userprofile.creator_img_url creatorImgUrl" +
            ",apps_userprofile.profile_img_url profileImgUrl" +
            ",apps_pathway.title pathwayName,apps_userprofile.user_id creatorId  from apps_pathway,apps_userprofile where apps_pathway.id = #{pathwayId} and apps_pathway.author_id = apps_userprofile.user_id")
    public UserProfileAndPathwayInfoVo getUserProfileInfoAndPathwayInfoByPathwayId(@Param("pathwayId")Integer pathwayId);



//


//    public List<NewsUpdateCardVO> pathwayInfoByCreatorIds(@Param("creatorIds")List creatorIds);

    // 根据creator 获取 此creator的最新的pathway的时间
    @Select("select updated_at from apps_pathway where author_id = #{authorId} order by updated_at desc limit 0,1 ")
    public Date getLastUpdateTimeByCreator(@Param("authorId") Integer authorId);
}
