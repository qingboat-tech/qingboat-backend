package com.qingboat.as.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.qingboat.as.config.db.MyDynamicTableNameInterceptor;
import com.qingboat.as.config.db.MyTableNameHandler;
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
//            put("test_student", (sql, tableName, student) -> tableName + "_" +((SysStudent)student).getDepartCode());

            put("apps_article_comment", new MyTableNameHandler() {
                @Override
                public String dynamicTableName(String sql, String tableName, Object parameter) {
                    log.debug(" SQL: "+ sql);
                    log.debug(" tableName: "+ tableName);
                    log.debug(" parameter: "+ parameter.toString());
                    return tableName;
                }
            });
        }};

        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        myDynamicTableNameInterceptor.setTableNameHandlerMap(map);
        interceptor.addInnerInterceptor(myDynamicTableNameInterceptor);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }
}
