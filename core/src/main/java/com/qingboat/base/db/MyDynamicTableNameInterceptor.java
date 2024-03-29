package com.qingboat.base.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyDynamicTableNameInterceptor  implements InnerInterceptor {
    private Map<String, MyTableNameHandler> tableNameHandlerMap = new LinkedHashMap<>();
    private boolean useUnityRule = false;

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
            return;
        }
        mpBs.sql(this.changeTable(mpBs.sql(), parameter));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(this.changeTable(mpBs.sql(), sh.getBoundSql().getParameterObject()));
        }
    }


    protected String changeTable(String sql, Object parameter) {
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        parser.accept(names::add);
        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                String value = name.getValue();
                MyTableNameHandler handler = null;
                if (useUnityRule){
                    handler = tableNameHandlerMap.get("*");
                } else{
                    handler = tableNameHandlerMap.get(value);
                }
                if (handler != null) {
                    // 处理
                    builder.append(handler.dynamicTableName(sql, value, getEntity(parameter)));
                } else {
                    builder.append(value);
                }
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        return builder.toString();
    }

    private Object getEntity(Object parameter){
        if (parameter == null){
            return null;
        }
        if (parameter instanceof MapperMethod.ParamMap){
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;

            if (paramMap.containsKey("ew")){
                Object ew = paramMap.get("ew");
                if (ew instanceof QueryWrapper){
                    QueryWrapper queryWrapper = (QueryWrapper) ew;
                    return queryWrapper.getEntity();
                }
                if (ew instanceof UpdateWrapper){
                    UpdateWrapper updateWrapper = (UpdateWrapper) ew;
                    return updateWrapper.getEntity();
                }
            }else if (paramMap.containsKey("et")){
                Object et = paramMap.get("et");
                if (et != null){
                    return et;
                }
            }
        }
        return parameter;
    }
}