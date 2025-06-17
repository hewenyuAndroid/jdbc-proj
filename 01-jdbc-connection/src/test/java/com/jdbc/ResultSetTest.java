package com.jdbc;

import com.example.jdbc.bean.User;
import com.example.jdbc.utils.JDBCUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class ResultSetTest {

    @Test
    public void testResultSet() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "select user as username, password, balance from user_table where user = ?";
            ps = connection.prepareStatement(sql);
            // jdbc 中的下标从 1 开始
            ps.setObject(1, "AA");
            // 得到结果集
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            // 获取查询的列数
            int columnCount = metaData.getColumnCount();
            // 获取index=1的列的别名，如果没有别名返回列名
            String columnLabel = metaData.getColumnLabel(1);
            // 返回 index=1 的列的列名
            String columnName = metaData.getColumnName(1);
            while (rs.next()) {
                User user = new User();
                // 通过 label 读取数据
                user.setUser(rs.getString(columnLabel));
                // 通过 index 读取
                user.setPassword(rs.getString(2));
                System.out.println("user=" + user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtil.closeResource(connection, ps, rs);
        }
    }

}
