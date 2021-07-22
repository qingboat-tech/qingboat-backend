package com.qingboat.us.dao;

import com.qingboat.us.entity.ArticlesummaryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticlesummaryDao {

    @Select("select articleSummary.id id,articleSummary.summary_text summaryText" +
            ",articleSummary.created_at createdAt,articleSummary.updated_at updatedAt" +
            ",articleSummary.article_id articleId,articleSummary.summary_user_id summaryUserId" +
            ",articleSummary.private _private" +
            "  from apps_articlesummary articleSummary,apps_node node " +
            "where node.pathway_id = #{pathwayId} and node.referrence_type = 1 " +
            "and node.referrence_id = articleSummary.article_id " +
            "and articleSummary.summary_user_id = #{userId} and articleSummary.private = 0 order by updatedAt desc limit 0,1")
    public ArticlesummaryEntity getNewestArticleSummaryByUserIdAndPathwayId(@Param("userId")Integer userId,@Param("pathwayId")Integer pathwayId);
}
