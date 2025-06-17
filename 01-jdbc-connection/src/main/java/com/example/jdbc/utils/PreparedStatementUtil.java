package com.example.jdbc.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class PreparedStatementUtil {

    /**
     * 通用的增删改操作
     *
     * @param sql    操作的目标sql
     * @param params 参数列表
     * @return 受影响的行数
     */
    public static int update(String sql, Object... params) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            // 1. 获取数据库连接
            connection = JDBCUtil.getConnection();
            // 2. 获取 preparedStatement 实例 (预编译sql)
            ps = connection.prepareStatement(sql);
            if (params != null && params.length > 0) {
                // 3. 填充占位符
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }

            // 4. 执行sql
            // execute() 返回 true 表示有结果集，需要通过 getResultSet() 获取结果集
            // execute() 返回 false 表示没有结果集，需要通过 getUpdateCount() 获取受影响的函数
            ps.execute();
            return ps.getUpdateCount();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

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
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtil.getConnection();

            ps = connection.prepareStatement(sql);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
            // 查询数据库
            rs = ps.executeQuery();
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
        } finally {
            JDBCUtil.closeResource(connection, ps, rs);
        }
        return null;
    }

}
