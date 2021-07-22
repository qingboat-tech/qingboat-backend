package com.qingboat.us.dao;

import com.qingboat.us.entity.ArticletodocellEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//行动建议表
@Mapper
public interface ArticletodocellDao {

    public ArticletodocellEntity getNewestArticletodocellEntityByArticlesAndUserId(@Param("articleIds")List<Integer> articleIds,@Param("userId")Integer userId);
}
