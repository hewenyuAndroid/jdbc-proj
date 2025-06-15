package com.jdbc;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class TestConnection {

    @Test
    public void testConnection01() {
        try {
            // 获取 mysql 驱动
            // com.mysql.jdbc.Driver 为旧的驱动类
//            Driver driver = new com.mysql.jdbc.Driver();
            // com.mysql.cj.jdbc.Driver 为新的mysql驱动
            Driver driver = new com.mysql.cj.jdbc.Driver();

            // jdbc:mysql 协议
            // localhost ip协议
            // 3306 默认 mysql 的端口号
            // test 数据库名称
            String url = "jdbc:mysql://localhost:3306/test";

            // 封装用户名和密码
            Properties properties = new Properties();
            properties.setProperty("user", "root");
            properties.setProperty("password", "123456");

            Connection connect = driver.connect(url, properties);
            System.out.println(connect);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConnection02() {
        try {
            // 通过反射的方式加载对应的驱动类
            Class<?> driverClz = Class.forName("com.mysql.cj.jdbc.Driver");
            Driver driver = (Driver) driverClz.newInstance();

            String url = "jdbc:mysql://localhost:3306/test";
            Properties properties = new Properties();
            properties.setProperty("user", "root");
            properties.setProperty("password", "123456");

            Connection connection = driver.connect(url, properties);
            System.out.println(connection);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConnection03() {
        // 使用 DriverManager 替换 Driver

        try {
            Class clz = Class.forName("com.mysql.cj.jdbc.Driver");
            Driver driver = (Driver) clz.newInstance();

            // 注册驱动
            DriverManager.registerDriver(driver);

            // 获取连接对象
            String url = "jdbc:mysql://localhost:3306/test";
            String username = "root";
            String password = "123456";
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println(connection);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConnection04() {
        // 使用 DriverManager 替换 Driver
        try {
            // mysql 驱动类加载成功后，会创建一个 Driver 驱动类注册到 DriverManager 中
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 获取连接对象
            String url = "jdbc:mysql://localhost:3306/test";
            String username = "root";
            String password = "123456";
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println(connection);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConnection05() {
        // 读取配置文件的方式获取连接信息

        // 1. 读取配置文件中的信息
        InputStream resourceAsStream = TestConnection.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);

            // 读取配置信息
            String driver = (String) properties.get("driver");
            String url = (String) properties.get("url");
            String user = (String) properties.get("user");
            String password = (String) properties.get("password");

            // 加载驱动
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
