package com.jdbc;

import com.example.jdbc.bean.User;
import com.example.jdbc.utils.PreparedStatementUtil;
import org.junit.Test;

import java.util.Scanner;

public class PreparedStatementTest {

    @Test
    public void testLogin() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入用户名：");
        String user = scanner.nextLine();
        System.out.print("请输入密码：");
        String password = scanner.nextLine();
        //SELECT user,password FROM user_table WHERE user = '1' or ' AND password = '=1 or '1' = '1'
        String sql = "SELECT user,password FROM user_table WHERE user = ? and password = ?";
        User userBean = PreparedStatementUtil.getInstance(User.class, sql, user, password);
        System.out.println("user=" + userBean);
    }

}
