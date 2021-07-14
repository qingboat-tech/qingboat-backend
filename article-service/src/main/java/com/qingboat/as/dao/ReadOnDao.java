package com.qingboat.as.dao;

import com.qingboat.as.entity.ReadonEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ReadOnDao {
    @Insert("insert into apps_readon (user_id,content_type,content_id,creator_id,height,updated_at) " +
            "values(#{readEntity.userId},#{readEntity.contentType},#{readEntity.contentId},#{readEntity.creatorId},#{readEntity.height},NOW())")
    public Integer insertRecord(@Param("readEntity")ReadonEntity readonEntity);

    @Select("select * from apps_readon where user_id = #{userId} and content_type = #{contentType} and content_id = #{contentId}")
    public ReadonEntity findRecord(@Param("userId")Integer userId,@Param("contentType")Integer contentType,@Param("contentId")String contentId);

    @Update("update apps_readon set height = #{readEntity.height} , updated_at = #{readEntity.updatedAt} where user_id = #{userId} and content_type = #{contentType} and content_id = #{contentId}")
    public Integer updateRecord(@Param("readEntity")ReadonEntity readonEntity);

}
