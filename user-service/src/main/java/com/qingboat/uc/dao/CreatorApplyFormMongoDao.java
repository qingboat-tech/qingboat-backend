package com.qingboat.uc.dao;

import com.qingboat.uc.entity.CreatorApplyFormEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CreatorApplyFormMongoDao extends MongoRepository<CreatorApplyFormEntity, String> {

}
