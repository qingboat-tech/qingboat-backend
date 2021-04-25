package com.qingboat.as.config;

import com.qingboat.as.entity.ArticleEntity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoDbConfig {

    final MongoTemplate mongoTemplate;
    public MongoDbConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        mongoTemplate.indexOps(ArticleEntity.class).ensureIndex(new Index().on("authorId", Sort.Direction.ASC));
        mongoTemplate.indexOps(ArticleEntity.class).ensureIndex(new Index().on("parentId", Sort.Direction.ASC));

    }

}
