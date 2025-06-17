package com.jdbc.case_test;

import com.example.jdbc.utils.JDBCUtil;
import com.example.jdbc.utils.PreparedStatementUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

public class Case03Test {

    /**
     * 向数据库的表customers中插入一条数据
     * desc customers;
     * +-------+-------------+------+-----+---------+----------------+
     * | Field | Type        | Null | Key | Default | Extra          |
     * +-------+-------------+------+-----+---------+----------------+
     * | id    | int         | NO   | PRI | NULL    | auto_increment |
     * | name  | varchar(15) | YES  |     | NULL    |                |
     * | email | varchar(20) | YES  |     | NULL    |                |
     * | birth | date        | YES  |     | NULL    |                |
     * | photo | mediumblob  | YES  |     | NULL    |                |
     * +-------+-------------+------+-----+---------+----------------+
     * 5 rows in set (0.01 sec)
     */
    @Test
    public void test01() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            String name = "zhangsan";
            String email = "zhangsan@qq.com";
            String birth = "2010-01-01";
            connection = JDBCUtil.getConnection();
            String sql = "insert into customers(name, email, birth) values(?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setObject(3, birth);

            ps.execute();
            int updateCount = ps.getUpdateCount();
            System.out.println("test01, updateCount = " + updateCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

    /**
     * 立数据库表 examstudent
     * desc examstudent2;
     * +-------------+-------------+------+-----+---------+-------+
     * | Field       | Type        | Null | Key | Default | Extra |
     * +-------------+-------------+------+-----+---------+-------+
     * | FlowID      | int         | YES  |     | NULL    |       |
     * | Type        | int         | YES  |     | NULL    |       |
     * | IDCard      | varchar(18) | YES  |     | NULL    |       |
     * | ExamCard    | varchar(15) | YES  |     | NULL    |       |
     * | StudentName | varchar(20) | YES  |     | NULL    |       |
     * | Location    | varchar(20) | YES  |     | NULL    |       |
     * | Grade       | int         | YES  |     | NULL    |       |
     * +-------------+-------------+------+-----+---------+-------+
     * 7 rows in set (0.00 sec)
     */
    @Test
    public void test02() {
        String sql = "create table if not exists examstudent2(" +
                "FlowID int(10) comment '流水号', " +
                "Type int(5) comment '四级/六级', " +
                "IDCard varchar(18) comment '身份证号', " +
                "ExamCard varchar(15) comment '准考证号', " +
                "StudentName varchar(20) comment '学生姓名', " +
                "Location varchar(20) comment '区域', " +
                "Grade int(10) comment '成绩'" +
                ")";
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);
            ps.execute();
            // create table 这里返回的是0
            int updateCount = ps.getUpdateCount();
            System.out.println("test02, updateCount = " + updateCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

    /**
     * 插入一个新的student 信息
     * desc examstudent;
     * +-------------+-------------+------+-----+---------+----------------+
     * | Field       | Type        | Null | Key | Default | Extra          |
     * +-------------+-------------+------+-----+---------+----------------+
     * | FlowID      | int         | NO   | PRI | NULL    | auto_increment |
     * | Type        | int         | YES  |     | NULL    |                |
     * | IDCard      | varchar(18) | YES  |     | NULL    |                |
     * | ExamCard    | varchar(15) | YES  |     | NULL    |                |
     * | StudentName | varchar(20) | YES  |     | NULL    |                |
     * | Location    | varchar(20) | YES  |     | NULL    |                |
     * | Grade       | int         | YES  |     | NULL    |                |
     * +-------------+-------------+------+-----+---------+----------------+
     * 7 rows in set (0.00 sec)
     * <p>
     * select * from examstudent;
     * +--------+------+--------------------+-----------------+-------------+----------+-------+
     * | FlowID | Type | IDCard             | ExamCard        | StudentName | Location | Grade |
     * +--------+------+--------------------+-----------------+-------------+----------+-------+
     * |      1 |    4 | 412824195263214584 | 200523164754000 | 张锋        | 郑州     |    85 |
     * |      2 |    4 | 222224195263214584 | 200523164755000 | 孙鹏        | 大连     |    56 |
     * |      3 |    6 | 555554195263214584 | 555555164755000 | 六名        | 沈阳     |    72 |
     * |      4 |    6 | 666664195263214584 | 999995164755000 | 招呼        | 哈尔滨   |    95 |
     * |      5 |    4 | 777774195263214584 | 888888164755000 | 杨丽        | 北京     |    64 |
     * |      6 |    4 | 888888195263214584 | 777773164755000 | 王晓红      | 太原     |    60 |
     * +--------+------+--------------------+-----------------+-------------+----------+-------+
     * 6 rows in set (0.00 sec)
     */
    @Test
    public void test03() {
        String sql = "insert into examStudent(FlowID, Type, IDCard, ExamCard, StudentName, Location, Grade) values" +
                "(1, 4, '412824195263214584', '200523164754000', '张锋', '郑州', 85), " +
                "(2, 4, '222224195263214584', '200523164755000', '孙鹏', '大连', 56), " +
                "(3, 6, '555554195263214584', '555555164755000', '六名', '沈阳', 72), " +
                "(4, 6, '666664195263214584', '999995164755000', '招呼', '哈尔滨', 95), " +
                "(5, 4, '777774195263214584', '888888164755000', '杨丽', '北京', 64), " +
                "(6, 4, '888888195263214584', '777773164755000', '王晓红', '太原', 60)";

        int update = PreparedStatementUtil.update(sql);
        System.out.println("test03, update = " + update);
    }

    @Test
    public void test04() {
        String examCard = "200523164754000";
        String sql = "select * from examstudent where ExamCard = ?";
        Map<String, Object> map = PreparedStatementUtil.getInstanceToMap(sql, examCard);
        System.out.println("test04, map=" + map);
    }

    @Test
    public void test05() {
        String examCard = "888888164755000";
        String sql = "delete from examstudent where ExamCard = ?";
        int update = PreparedStatementUtil.update(sql, examCard);
        System.out.println("test05, update = " + update);
    }

}
