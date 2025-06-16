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

}
