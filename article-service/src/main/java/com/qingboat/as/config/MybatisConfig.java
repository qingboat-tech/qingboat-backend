package com.qingboat.as.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.qingboat.as.config.db.MyDynamicTableNameInterceptor;
import com.qingboat.as.config.db.MyTableNameHandler;
import com.qingboat.as.entity.ArticleCommentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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
//            put("test_student", (sql, tableName, student) -> tableName + "_" +((SysStudent)student).getDepartCode());

            put("apps_article_comment", new MyTableNameHandler() {
                @Override
                public String dynamicTableName(String sql, String tableName,Object param) {
                    log.debug(" SQL: "+ sql);
                    log.debug(" tableName: "+ tableName);
                    if (param instanceof  ArticleCommentEntity){
                        String articleId = ((ArticleCommentEntity) param).getArticleId();
                        int tableIndex = articleId.hashCode()%4 +1;
                        System.err.println("======================");
                        System.err.println("======================" +tableName +"_"+tableIndex);
                        return tableName +"_"+tableIndex;
                    }
                    return tableName;
                }
            });
        }};

//        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        myDynamicTableNameInterceptor.setTableNameHandlerMap(map);
        interceptor.addInnerInterceptor(myDynamicTableNameInterceptor);

        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }
}
