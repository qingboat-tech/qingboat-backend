package com.qingboat.us.dao;

import com.qingboat.us.entity.LastAccessRecordsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface LastAccessRecordsDao {

    @Select(" INSERT INTO apps_lastaccessrecords (user_id,type,target_id,creator_id,last_accessTime)" +
            " VALUES (#{lastAccessRecordsEntity.userId},#{lastAccessRecordsEntity.type}," +
            " #{lastAccessRecordsEntity.targetId},#{lastAccessRecordsEntity.creatorId}," +
            " NOW())")
    public Integer insertLastAccessRecords(@Param("lastAccessRecordsEntity")LastAccessRecordsEntity lastAccessRecordsEntity);

    @Select("select count(*) from apps_lastaccessrecords where user_id = #{userId} and type = #{type} and target_id = #{targetId} ")
    public Integer findRecord(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")String targetId);

    @Select("select last_accessTime from apps_lastaccessrecords where  user_id = #{userId}  and target_id = #{targetId} order by last_accessTime desc limit 0,1")
    public Date findLastRecordTimeWithTargetId(@Param("userId")Integer userId,@Param("targetId")String targetId);

    @Select("select last_accessTime from apps_lastaccessrecords where  user_id = #{userId}  and creator_id = #{creatorId} order by last_accessTime desc limit 0,1")
    public Date findLastRecordTimeWithCreatorId(@Param("userId")Integer userId,@Param("creatorId")Integer creatorId);


//    @Select("select count(*) from apps_lastaccessrecords where user_id = #{userId} and type = #{type} and target_id = #{targetId} ")
//    public Integer findRecord(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")Integer targetId);

    @Update("update apps_lastaccessrecords set last_accessTime = NOW() where user_id = #{userId} and type = #{type} and target_id = #{targetId}")
    public Integer updateLastAccessTime(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")String targetId);
//    @Update("update apps_lastaccessrecords set last_accessTime = NOW() where user_id = #{userId} and type = #{type} and target_id = #{targetId}")
//    public Integer updateLastAccessTime(@Param("userId")Integer userId,@Param("type")Integer type,@Param("targetId")Integer targetId);
}
