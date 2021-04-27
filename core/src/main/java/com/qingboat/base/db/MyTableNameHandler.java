package com.qingboat.base.db;

public interface MyTableNameHandler {
    /**
     * 生成动态表名后的sql
     * @param sql 原始sql
     * @param tableName 新表表名
     * @param parameter 参数
     * @return 替换表名后的sql语句
     */
    String dynamicTableName(String sql, String tableName, Object parameter);
}
