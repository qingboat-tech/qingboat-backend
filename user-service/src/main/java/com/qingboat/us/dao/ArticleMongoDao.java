package com.qingboat.us.dao;


import com.qingboat.us.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMongoDao extends MongoRepository<ArticleEntity, String> {

    @Query(value = "{'authorId':?0 ,'status':{'$eq':?1} , 'parentId': {'$eq':''} }" ,
            fields = "{title:1,desc:1,imgUrl:1,top:1,authorId:1,createdTime:1,updatedTime:1,starCount:1,commentCount:1,readCount:1,status:1,type:1,scope:1,benefit:1,suggestion:1}")
    Page<ArticleEntity> findByAuthorIdAndStatus(String authorId,Integer status,  Pageable pageable);

    @Query(value = "{'authorId':?0 ,'status':{'$eq':?1} , 'parentId': {'$eq':''} }", count = true)
    Long countByAuthorIdAndStatus(String authorId,Integer status);

    @Query(value = "{'authorId':{$in:?0} ,'scope':{$in:?1} ,'status':{'$eq':?2} , 'parentId': {'$eq':''} }" ,
            fields = "{title:1,desc:1,imgUrl:1,top:1,authorId:1,createdTime:1,updatedTime:1,starCount:1,commentCount:1,readCount:1,status:1,type:1,scope:1}")
    Page<ArticleEntity> findByAuthorIdsAndScopeAndStatus(List<String> authorId,List<Integer> scope, Integer status, Pageable pageable);

    @Query(value = "{'authorId':?0 ,'status':{'$eq':4} , 'parentId': {'$eq':''} }" ,
            fields = "{title:1,desc:1,imgUrl:1,top:1,authorId:1,createdTime:1,updatedTime:1,starCount:1,commentCount:1,readCount:1}",sort = "{readCount : -1,createdTime:-1 }")
    Page<ArticleEntity> findByAuthorIdByReadCountDesc(String authorId, Pageable pageable);

    @Query(value = "{'authorId':?0 , 'parentId': {'$eq':''} }" ,
            fields = "{title:1,desc:1,imgUrl:1,top:1,authorId:1,createdTime:1,updatedTime:1,starCount:1,commentCount:1,readCount:1,status:1,type:1,scope:1}")
    Page<ArticleEntity> findByAuthorId(String authorId, Pageable pageable);

    List<ArticleEntity> findByParentId(String parentId);

    ArticleEntity findArticleEntityById(String articleId);

    @Query(value = "{'id':?0 }" ,fields = "{title:1 ,desc:1 ,imgUrl:1,benefit:1, top:1,authorId:1,status:1, createdTime:1,starCount:1,commentCount:1,readCount:1 }")
    ArticleEntity findBaseInfoById(String articleId);

    @Query(value = "{'authorId':{$in:?0},'status':4}",fields = "{title:1,desc:1,imgUrl:1,authorId:1,starCount:1,createdTime:1,updatedTime:1}")
    List<ArticleEntity> findPublishArticleProfileInfoByAuthorIds(List<String> authorIds);

    @Query(value = "{'authorId':?0,'status':4}",fields = "{title:1,desc:1,imgUrl:1,authorId:1,starCount:1,createdTime:1,updatedTime:1}")
    List<ArticleEntity> findPublishArticleProfileInfoByAuthorId(String authorIds);



}
