package com.example.jdbc.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class PreparedStatementUtil {

    /**
     * 针对于不同的表的通用的查询操作，返回表中的一条记录
     *
     * @param clz  要查询的目标数据类class对象
     * @param sql  sql
     * @param args 参数
     * @param <T>  目标数据类型
     * @return 查询结果
     */
    public static <T> T getInstance(Class<T> clz, String sql, Object... args) {
        try {
            Connection connection = JDBCUtil.getConnection();

            PreparedStatement ps = connection.prepareStatement(sql);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
            // 查询数据库
            ResultSet rs = ps.executeQuery();
            // 获取结果集的源数据
            ResultSetMetaData metaData = rs.getMetaData();
            // 通过 ResultSetMetaData 获取结果集中的列数
            int columnCount = metaData.getColumnCount();

            if (rs.next()) {
                T t = clz.newInstance();
                // 处理结果集中的每一列数据
                for (int i = 0; i < columnCount; i++) {
                    // 获取列值
                    Object columnValue = rs.getObject(i + 1);

                    // 获取列名
                    String columnLabel = metaData.getColumnLabel(i + 1);

                    // 反射赋值
                    Field field = clz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
