package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RelationshipBetweenUsersAndCreatorsDao {

    /**
     *  pathway + newsletters  根据用户id查询所有的 ta所订阅/购买的所有创作者id （pathway + newsletter）
     * @param userId
     * @return
     */
    public List<Integer> getAllCreatorIdsByUserId(@Param("userId") Integer userId);




}
