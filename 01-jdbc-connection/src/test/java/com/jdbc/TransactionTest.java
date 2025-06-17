package com.jdbc;

import com.example.jdbc.utils.JDBCUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 事务测试
 */
public class TransactionTest {

    /**
     * 模拟转账成功，没有事务的场景
     *
     * <p>
     * 转帐前
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |    1000 |
     * | BB   | 654321   |    1000 |
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.02 sec)
     * <p>
     * <p>
     * 转账后
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |     900 |
     * | BB   | 654321   |    1100 |
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.00 sec)
     */
    @Test
    public void testTransferSuccessNoTransaction() throws Exception {
        String transferFromSql = "update user_table set balance = balance - ? where user = ?";
        String transferToSql = "update user_table set balance = balance + ? where user = ?";

        int transferAmount = 100;
        String fromUser = "AA";
        String toUser = "BB";
        // 多次提交需要使用同一个连接
        Connection connection = JDBCUtil.getConnection();
        // 更新 fromUser 账户
        update(connection, transferFromSql, transferAmount, fromUser);

        // 更新 toUser 账户
        update(connection, transferToSql, transferAmount, toUser);
        if (!connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * 模拟转账中间异常，没有事务
     * <p>
     * 转帐前
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |     900 |
     * | BB   | 654321   |    1100 |
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.00 sec)
     * <p>
     * <p>
     * 转账异常后
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |     800 |    fromUser 账户的余额减少
     * | BB   | 654321   |    1100 |    toUser 账户的余额没有变更
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.00 sec)
     */
    @Test
    public void testTransferFailureNoTransaction() throws Exception {
        String transferFromSql = "update user_table set balance = balance - ? where user = ?";
        String transferToSql = "update user_table set balance = balance + ? where user = ?";

        int transferAmount = 100;
        String fromUser = "AA";
        String toUser = "BB";

        // 多次提交需要使用同一个连接
        Connection connection = JDBCUtil.getConnection();
        // 更新 fromUser 账户
        update(connection, transferFromSql, transferAmount, fromUser);

        // 模拟异常
        int i = 10 / 0;

        // 更新 toUser 账户
        update(connection, transferToSql, transferAmount, toUser);

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * 模拟转账成功，使用事务的方式
     * <p>
     * <p>
     * 转账前
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |     800 |
     * | BB   | 654321   |    1100 |
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.00 sec)
     * <p>
     * <p>
     * 转账后
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |     700 |
     * | BB   | 654321   |    1200 |
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.00 sec)
     */
    @Test
    public void testTransferSuccessUseTransaction() throws Exception {
        String transferFromSql = "update user_table set balance = balance - ? where user = ?";
        String transferToSql = "update user_table set balance = balance + ? where user = ?";

        int transferAmount = 100;
        String fromUser = "AA";
        String toUser = "BB";

        Connection connection = JDBCUtil.getConnection();
        // 取消自动提交
        connection.setAutoCommit(false);

        // 更新 fromUser 账户
        update(connection, transferFromSql, transferAmount, fromUser);

        // 更新 toUser 账户
        update(connection, transferToSql, transferAmount, toUser);

        // 提交事务
        connection.commit();
        // 当前事务执行完毕后，恢复数据库连接自动提交功能
        connection.setAutoCommit(true);
        if (!connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * 模拟转账中间过程失败，开启事务
     * <p>
     * <p>
     * 转帐前
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |     700 |
     * | BB   | 654321   |    1200 |
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.00 sec)
     * <p>
     * <p>
     * 转账中间过程失败后
     * select * from user_table;
     * +------+----------+---------+
     * | user | password | balance |
     * +------+----------+---------+
     * | AA   | 123456   |     700 |    转账失败后，fromUser 余额没有变更
     * | BB   | 654321   |    1200 |    转账失败后，toUser 余额也没有变更
     * | CC   | abcd     |    2000 |
     * | DD   | abcder   |    3000 |
     * +------+----------+---------+
     * 4 rows in set (0.00 sec)
     */
    @Test
    public void testTransferFailureUseTransaction() throws Exception {
        String transferFromSql = "update user_table set balance = balance - ? where user = ?";
        String transferToSql = "update user_table set balance = balance + ? where user = ?";

        int transferAmount = 100;
        String fromUser = "AA";
        String toUser = "BB";

        Connection connection = JDBCUtil.getConnection();
        // 取消自动提交
        connection.setAutoCommit(false);

        // 更新 fromUser 账户
        update(connection, transferFromSql, transferAmount, fromUser);

        // 模拟异常
        int i = 10 / 0;

        // 更新 toUser 账户
        update(connection, transferToSql, transferAmount, toUser);

        // 提交事务
        connection.commit();
        // 当前事务执行完毕后，恢复数据库连接自动提交功能
        connection.setAutoCommit(true);
        if (!connection.isClosed()) {
            connection.close();
        }
    }

    private int update(Connection connection, String sql, Object... params) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            } catch (Exception e) {
                System.out.printf("关闭 PreparedStatement 异常: " + e.getMessage());
            }
        }
    }


}
