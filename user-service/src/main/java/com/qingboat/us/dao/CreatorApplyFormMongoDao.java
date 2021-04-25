package com.qingboat.us.dao;

import com.qingboat.us.entity.CreatorApplyFormEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CreatorApplyFormMongoDao extends MongoRepository<CreatorApplyFormEntity, String> {

    CreatorApplyFormEntity findFirstByUserId(Long userId);

}
