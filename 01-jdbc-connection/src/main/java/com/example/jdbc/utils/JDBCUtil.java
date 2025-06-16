package com.example.jdbc.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库操作工具类
 */
public class JDBCUtil {


    /**
     * 获取一个数据库连接
     *
     * @return 数据库连接对象
     */
    public static Connection getConnection() throws Exception {
        // 1. 加载数据库连接信息的配置文件
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        // 2. 解析配置文件，获取数据库连接信息
        Properties properties = new Properties();
        properties.load(is);
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String driver = properties.getProperty("driver");
        // 3. 加载驱动
        Class.forName(driver);
        // 4. 获取连接
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * 关闭连接和statement操作
     *
     * @param connection 数据库连接对象
     * @param statement  statement对象
     */
    public static void closeResource(Connection connection, Statement statement) {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            System.out.println("数据连接关闭失败, " + e.getMessage());
        }
    }

    /**
     * 关闭连接和statement操作
     *
     * @param connection 数据库连接对象
     * @param statement  statement对象
     * @param resultSet  数据集
     */
    public static void closeResource(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
        } catch (Exception e) {
            System.out.println("数据连接关闭失败, " + e.getMessage());
        }
    }

}
