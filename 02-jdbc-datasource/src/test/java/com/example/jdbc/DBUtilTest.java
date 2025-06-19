package com.example.jdbc;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.example.jdbc.bean.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

public class DBUtilTest {

    @Test
    public void testInsert() throws Exception {
        Properties properties = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        properties.load(is);

        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "insert into user_table(user, password, balance) values(?,?,?)";
        int update = queryRunner.update(connection, sql, "ABC", "abc123", 1000);
        System.out.println("testInsert(): updateCount=" + update);
    }

    @Test
    public void testDelete() throws Exception {
        Properties properties = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        properties.load(is);

        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "delete from user_table where user = ?";
        int update = queryRunner.update(connection, sql, "ABC");
        System.out.println("testDelete(): updateCount=" + update);
    }

    @Test
    public void testQuerySingleData() throws Exception {
        Properties properties = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        properties.load(is);

        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();

        QueryRunner queryRunner = new QueryRunner();

        String sql = "select * from user_table where user = ?";
        BeanHandler<User> beanHandler = new BeanHandler<>(User.class);
        User user = queryRunner.query(connection, sql, beanHandler, "AA");
        System.out.println("testQuerySingleData(): user=" + user);
    }

    @Test
    public void testQueryMultiData() throws Exception {
        Properties properties = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        properties.load(is);

        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();

        QueryRunner queryRunner = new QueryRunner();

        String sql = "select * from user_table where user in (?,?)";
        BeanListHandler<User> beanHandler = new BeanListHandler<>(User.class);
        List<User> list = queryRunner.query(connection, sql, beanHandler, "AA", "BB");
        System.out.println("testQueryMultiData(): list=" + list);
    }

}
