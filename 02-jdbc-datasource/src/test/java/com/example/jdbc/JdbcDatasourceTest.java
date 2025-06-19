package com.example.jdbc;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

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

    @Test
    public void testDbcp() throws SQLException {
        BasicDataSource basicDataSource = new BasicDataSource();
        // 数据库连接配置
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/test");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("123456");

        // 数据库连接池配置
        basicDataSource.setInitialSize(10);

        Connection connection = basicDataSource.getConnection();
        System.out.println("testDbcp, connection=" + connection);
    }

    @Test
    public void testDbcpUseConfig() {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
        Properties props = new Properties();
        try {
            props.load(is);
            BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(props);

            Connection connection = dataSource.getConnection();
            System.out.println("testDbcpUseConfig, connection=" + connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Test
    public void testDruid() throws Exception {
        Properties properties = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        properties.load(is);

        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();
        System.out.println("testDruid, connection=" + connection);
    }

}
