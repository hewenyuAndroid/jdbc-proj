package com.jdbc;

import com.example.jdbc.bean.User;
import com.example.jdbc.utils.PreparedStatementUtil;
import org.junit.Test;

public class PreparedStatementTest {

    @Test
    public void testLogin() {
        String user = "AA";
        String password = "123456";
        //SELECT user,password FROM user_table WHERE user = '1' or ' AND password = '=1 or '1' = '1'
        String sql = "SELECT user,password FROM user_table WHERE user = ? and password = ?";
        User userBean = PreparedStatementUtil.getInstance(User.class, sql, user, password);
        System.out.println("user=" + userBean);
    }

    @Test
    public void testInsert() {
        String user = "ZhangSan";
        String password = "123456";
        int balance = 100000;
        // insert into user_table values('user', 'password', balance);
        String sql = "insert into user_table(user,password,balance) values(?,?,?)";
        int updateCount = PreparedStatementUtil.update(sql, user, password, balance);
        System.out.println("testInsert, updateCount=" + updateCount);
    }

    @Test
    public void testUpdate() {
        String user = "ZhangSan";
        int balance = 200000;
        // update user_table set balance=20000 where user = 'ZhangSan';
        String sql = "update user_table set balance = ? where user = ?";
        int updateCount = PreparedStatementUtil.update(sql, balance, user);
        System.out.println("testUpdate, updateCount=" + updateCount);
    }

    @Test
    public void testDelete() {
        String user = "ZhangSan";
        // delete from user_table where user = 'ZhangSan'
        String sql = "delete from user_table where user = ?";
        int updateCount = PreparedStatementUtil.update(sql, user);
        System.out.println("testDelete, updateCount=" + updateCount);
    }

}
