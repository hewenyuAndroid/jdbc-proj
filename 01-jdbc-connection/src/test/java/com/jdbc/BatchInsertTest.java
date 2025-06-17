package com.jdbc;

import com.example.jdbc.utils.JDBCUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 批量插入案例
 */
public class BatchInsertTest {

    /**
     * PreparedStatement 单条数据插入循环插入
     * <p>
     * testPreparedStatementInsert() cost=73851ms
     */
    @Test
    public void testPreparedStatementInsert() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            String sql = "insert into goods(name) values(?)";
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);

            long startTime = System.currentTimeMillis();
            for (int i = 1; i <= 20000; i++) {
                ps.setString(1, "name_" + i);
                ps.executeUpdate();
            }

            long endTime = System.currentTimeMillis();
            System.out.println("testPreparedStatementInsert() cost=" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

    /**
     * PreparedStatement 使用批量插入功能，自动提交
     * <p>
     * testPreparedStatementBatchInsertAutoCommit() cost=70427ms
     */
    @Test
    public void testPreparedStatementBatchInsertAutoCommit() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            String sql = "insert into goods(name) values(?)";
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);

            long startTime = System.currentTimeMillis();
            for (int i = 1; i <= 20000; i++) {
                ps.setString(1, "name_" + i);

                ps.addBatch();
                if (i % 1000 == 0) {
                    // 每1000条执行一次批量插入
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println("testPreparedStatementBatchInsertAutoCommit() cost=" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

    /**
     * PreparedStatement 批量插入，不使用自动提交方式
     *
     * testPreparedStatementBatchInsertNotAutoCommit() cost=3619ms
     */
    @Test
    public void testPreparedStatementBatchInsertNotAutoCommit() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            String sql = "insert into goods(name) values(?)";
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);

            // 设置不自动提交数据
            connection.setAutoCommit(false);

            long startTime = System.currentTimeMillis();
            for (int i = 1; i <= 20000; i++) {
                ps.setString(1, "name_" + i);

                ps.addBatch();
                if (i % 1000 == 0) {
                    // 每1000条执行一次批量插入
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            // 提交数据
            connection.commit();

            long endTime = System.currentTimeMillis();
            System.out.println("testPreparedStatementBatchInsertNotAutoCommit() cost=" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

}
