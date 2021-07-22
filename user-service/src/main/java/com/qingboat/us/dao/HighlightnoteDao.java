package com.qingboat.us.dao;

import com.qingboat.us.entity.HighlightnoteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HighlightnoteDao {
    public HighlightnoteEntity getNewestHighlightnoteEntityByHighlightIdsAndUserId(@Param("highlightIds")List<Integer> highlightIds,@Param("userId")Integer userId);
}
