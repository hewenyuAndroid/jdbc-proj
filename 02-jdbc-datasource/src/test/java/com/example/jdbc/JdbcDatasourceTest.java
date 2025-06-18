package com.example.jdbc;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接池单元测试类
 */
public class JdbcDatasourceTest {

    /**
     * 使用C3P0数据库连接池的方式，获取数据库的连接 (不推荐)
     *
     * @throws PropertyVetoException
     * @throws SQLException
     */
    @Test
    public void testC3p0() throws PropertyVetoException, SQLException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUser("root");
        dataSource.setPassword("123456");

        dataSource.setMaxPoolSize(100);

        Connection connection = dataSource.getConnection();
        System.out.println("testC3p0, connection=" + connection);
    }

    /**
     * 使用C3P0数据库连接池的配置文件方式，获取数据库的连 (推荐)
     */
    @Test
    public void testC3p0UseConfig() throws SQLException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource("firstC3p0");
        Connection connection = dataSource.getConnection();
        System.out.println("testC3p0UseConfig, connection=" + connection);
    }

}
