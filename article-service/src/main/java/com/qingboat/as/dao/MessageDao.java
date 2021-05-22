package com.qingboat.as.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.MessageEntity;
import com.qingboat.as.entity.TierEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface MessageDao extends BaseMapper<MessageEntity> {


    List<Map<String,Integer>> getGroupUnreadCountByUserId(MessageEntity messageEntity);

}