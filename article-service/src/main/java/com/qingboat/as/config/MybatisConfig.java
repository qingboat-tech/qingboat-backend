package com.qingboat.as.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.MessageEntity;
import com.qingboat.as.entity.ReplyCommentEntity;
import com.qingboat.base.db.MyDynamicTableNameInterceptor;
import com.qingboat.base.db.MyTableNameHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Slf4j
@Configuration
public class MybatisConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 取消MyBatis Plus的最大分页500条的限制
        paginationInnerInterceptor.setMaxLimit(100000L);


        MyDynamicTableNameInterceptor myDynamicTableNameInterceptor = new MyDynamicTableNameInterceptor();

        HashMap<String, MyTableNameHandler> map = new HashMap<String, MyTableNameHandler>(2) {{
            //整个函数返回的结果就是替换后的新表名，这个生成的表名的规则可以自己随便指定
            put("apps_article_comment", new MyTableNameHandler() {
                @Override
                public String dynamicTableName(String sql, String tableName,Object param) {

                    log.info(" SQL: "+ sql);
                    log.info(" SQL.param: "+ param);
                    if (param instanceof  ArticleCommentEntity){
                        String articleId = ((ArticleCommentEntity) param).getArticleId();
                        StringBuilder dynamicTableName = new StringBuilder(tableName);
                        dynamicTableName.append("_");
                        dynamicTableName.append(Math.abs(articleId.hashCode()) %4 +1);

                        log.info(" dynamicTableName: "+ dynamicTableName);
                        return dynamicTableName.toString();
                    }
                    return tableName;
                }
            });
            put("apps_reply_comment", new MyTableNameHandler() {
                @Override
                public String dynamicTableName(String sql, String tableName,Object param) {
                    log.info(" SQL: "+ sql);
                    if (param instanceof ReplyCommentEntity){
                        String articleId = ((ReplyCommentEntity) param).getArticleId();
                        StringBuilder dynamicTableName = new StringBuilder(tableName);
                        dynamicTableName.append("_");
                        dynamicTableName.append(Math.abs(articleId.hashCode()) %4 +1);

                        log.info(" dynamicTableName: "+ dynamicTableName);
                        return dynamicTableName.toString();
                    }
                    return tableName;
                }
            });

            put("apps_msg", new MyTableNameHandler() {
                @Override
                public String dynamicTableName(String sql, String tableName,Object param) {

                    log.info(" SQL: "+ sql);
                    log.info(" SQL.param: "+ param);
                    if (param instanceof MessageEntity){
                        Long to = ((MessageEntity) param).getTo();
                        StringBuilder dynamicTableName = new StringBuilder(tableName);
                        dynamicTableName.append("_");
                        dynamicTableName.append(Math.abs(to.hashCode()) %16+1);

                        log.info(" dynamicTableName: "+ dynamicTableName);
                        return dynamicTableName.toString();
                    }
                    return tableName;
                }
            });


        }};

        myDynamicTableNameInterceptor.setTableNameHandlerMap(map);
        interceptor.addInnerInterceptor(myDynamicTableNameInterceptor);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
