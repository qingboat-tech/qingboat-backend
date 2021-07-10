package com.qingboat.us.dao;

import com.qingboat.us.entity.LastAccessRecordsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LastAccessRecordsDao {

    @Select(" INSERT INTO apps_lastaccessrecords (user_id,type,target_id,creator_id,last_accessTime)" +
            " VALUES (#{lastAccessRecordsEntity.userId},#{lastAccessRecordsEntity.type}," +
            " #{lastAccessRecordsEntity.targetId},#{lastAccessRecordsEntity.creatorId}," +
            " NOW())")
    public Integer insertLastAccessRecords(@Param("lastAccessRecordsEntity")LastAccessRecordsEntity lastAccessRecordsEntity);

    @Select("select count(*) from apps_lastaccessrecords where user_id = #{userId} and type = #{type} and target_id = #{targetId} ")
    public Integer findRecord(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")String targetId);

//    @Select("select count(*) from apps_lastaccessrecords where user_id = #{userId} and type = #{type} and target_id = #{targetId} ")
//    public Integer findRecord(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")Integer targetId);

    @Update("update apps_lastaccessrecords set last_accessTime = NOW() where user_id = #{userId} and type = #{type} and target_id = #{targetId}")
    public Integer updateLastAccessTime(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")String targetId);
//    @Update("update apps_lastaccessrecords set last_accessTime = NOW() where user_id = #{userId} and type = #{type} and target_id = #{targetId}")
//    public Integer updateLastAccessTime(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")Integer targetId);
}
