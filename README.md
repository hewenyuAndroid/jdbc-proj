[toc]

# JDBC概述

- JDBC(Java Database Connectivity)是一个**独立于特定数据库管理系统、通用的SQL数据库存取和操作的公共接口**
  （一组API），定义了用来访问数据库的标准Java类库，（**java.sql,javax.sql**）使用这些类库可以以一种**标准**的方法、方便地访问数据库资源。
- JDBC为访问不同的数据库提供了一种**统一的途径**，为开发者屏蔽了一些细节问题。
- JDBC的目标是使Java程序员使用JDBC可以连接任何**提供了JDBC驱动程序**的数据库系统，这样就使得程序员无需对特定的数据库系统的特点有过多的了解，从而大大简化和加快了开发过程。
- 如果没有JDBC，那么Java程序访问数据库时是这样的：

![java no jdbc](./imgs/java-no-jdbc-img.png)

- 有了JDBC，Java程序访问数据库时是这样的:

![java use jdbc](./imgs/java-use-jdbc-img.png)

> 总结如下:

![java jdbc img](./imgs/java-jdbc-img.png)

## jdbc 体系结构

JDBC接口（API）包括两个层次：

- **面向应用的API**：Java API，抽象接口，供应用程序开发人员使用（连接数据库，执行SQL语句，获得结果）。
- **面向数据库的API**：Java Driver API，供开发商开发数据库驱动程序用。

## jdbc 程序开发步骤

![jdbc dev step](./imgs/jdbc-dev-step.png)

注: ODBC(**Open Database Connectivity**，开放式数据库连接)，是微软在Windows平台下推出的。使用者在程序中只需要调用ODBC API，由
ODBC 驱动程序将调用转换成为对特定的数据库的调用请求。

# 获取数据库连接

## 获取数据库连接要素: Driver 接口实现类

- `java.sql.Driver` 接口是所有 JDBC 驱动程序需要实现的接口。这个接口是提供给数据库厂商使用的，不同数据库厂商提供不同的实现。
- 在程序中不需要直接去访问实现了 Driver 接口的类，而是由驱动程序管理器类(java.sql.DriverManager)去调用这些Driver实现。
    - Oracle的驱动： `oracle.jdbc.driver.OracleDriver`
    - mySql的驱动： `com.mysql.jdbc.Driver` 或 `com.mysql.cj.jdbc.Driver`

```xml
<!--- 添加 mysql 驱动依赖 ->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.31</version>
</dependency>
```

### 加载与注册 jdbc 驱动

- 加载驱动：加载 JDBC 驱动需调用 Class 类的静态方法 forName()，向其传递要加载的 JDBC 驱动的类名
    - **Class.forName(“com.mysql.jdbc.Driver”);**
- 注册驱动：`DriverManager` 类是驱动程序管理器类，负责管理驱动程序
    - **使用DriverManager.registerDriver(com.mysql.jdbc.Driver)来注册驱动**
    - 通常不用显式调用 DriverManager 类的 registerDriver() 方法来注册驱动程序类的实例，因为 Driver 接口的驱动程序类**都**
      包含了静态代码块，在这个静态代码块中，会调用 DriverManager.registerDriver() 方法来注册自身的一个实例。

mysql 驱动静态代码块:

```java
package com.mysql.cj.jdbc;

public class Driver extends NonRegisteringDriver implements java.sql.Driver {
    ...

    static {
        try {
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }
    ...
}
```

## jdbc url

- JDBC URL 用于标识一个被注册的驱动程序，驱动程序管理器通过这个 URL 选择正确的驱动程序，从而建立到数据库的连接。
- JDBC URL的标准由三部分组成，各部分间用冒号分隔。
    - **jdbc:子协议:子名称**
    - **协议**：JDBC URL中的协议总是jdbc
    - **子协议**：子协议用于标识一个数据库驱动程序
    - **子名称**：一种标识数据库的方法。子名称可以依不同的子协议而变化，用子名称的目的是为了**定位数据库**提供足够的信息。包含
      **主机名**(对应服务端的ip地址)**，端口号，数据库名**

## jdbc 连接数据库

使用反射加载驱动类

```java
Class clz = Class.forName("com.mysql.cj.jdbc.Driver");
Driver driver = (Driver) clz.newInstance();

// 注册驱动
DriverManager.

registerDriver(driver);

// 获取连接对象
String url = "jdbc:mysql://localhost:3306/test";
String username = "root";
String password = "123456";
Connection connection = DriverManager.getConnection(url, username, password);
```

使用 DriverManager 加载驱动类

```java
// mysql 驱动类加载成功后，会创建一个 Driver 驱动类注册到 DriverManager 中
Class.forName("com.mysql.cj.jdbc.Driver");

// 获取连接对象
String url = "jdbc:mysql://localhost:3306/test";
String username = "root";
String password = "123456";
Connection connection = DriverManager.getConnection(url, username, password);
System.out.

println(connection);
```

# 使用 `PreparedStatement` 实现 `CRUD` 操作

## 操作和访问数据库

- 数据库连接被用于向数据库服务器发送命令和 SQL 语句，并接受数据库服务器返回的结果。其实一个数据库连接就是一个Socket连接。
- 在 `java.sql` 包中有 3 个接口分别定义了对数据库的调用的不同方式：
    - `Statement`：用于执行静态 SQL 语句并返回它所生成结果的对象。
    - `PreparedStatement`：SQL 语句被预编译并存储在此对象中，可以使用此对象多次高效地执行该语句。
    - `CallableStatement`：用于执行 SQL 存储过程

![jdbc statement](./imgs/java-jdbc-statement.png)

## 使用 `Statement` 操作数据表

- 通过调用 `Connection` 对象的 `createStatement()` 方法创建该对象。该对象用于执行静态的 SQL 语句，并且返回执行结果;
- `Statement` 接口中定义了下列方法用于执行 SQL 语句

```java
// 执行更新操作INSERT、UPDATE、DELETE
int excuteUpdate(String sql);

// 执行查询操作SELECT
ResultSet executeQuery(String sql);
```

### `Statement` 操作数据表的弊端

1. 存在拼串操作，繁琐;
2. 存在 sql 注入问题；
    - SQL 注入是利用某些系统没有对用户输入的数据进行充分的检查，而在用户输入数据中注入非法的 SQL 语句段或命令(
      如：`SELECT user, password FROM user_table WHERE user='a' OR 1 = ' AND password = ' OR '1' = '1'`) ，从而利用系统的
      SQL 引擎完成恶意行为的做法。
    - 对于 Java 而言，要防范 SQL 注入，只要用 `PreparedStatement` ( 从 `Statement` 扩展而来 ) 取代 `Statement` 就可以了。

## `PreparedStatement`

- 可以通过调用 Connection 对象的 `preparedStatement(String sql)` 方法获取 PreparedStatement 对象;
- `PreparedStatement` 接口是 `Statement` 的子接口，它表示一条预编译过的 SQL 语句;
- `PreparedStatement` 对象所代表的 SQL 语句中的参数用问号(?)来表示，调用 `PreparedStatement` 对象的 `setXxx()`
  方法来设置这些参数. `setXxx()` 方法有两个参数，第一个参数是要设置的 SQL 语句中的参数的索引(从 1 开始)，第二个是设置的
  SQL 语句中的参数的值;

### `PreparedStatement` VS `Statement`

- 代码的可读性和可维护性；
- PreparedStatement 能最大可能提高性能：
    - DBServer会对 **预编译**
      语句提供性能优化。因为预编译语句有可能被重复调用，所以语句在被DBServer的编译器编译后的执行代码被缓存下来，那么下次调用时只要是相同的预编译语句就不需要编译，只要将参数直接传入编译过的语句执行代码中就会得到执行。
    - 在statement语句中,即使是相同操作但因为数据内容不一样,所以整个语句本身不能匹配,没有缓存语句的意义.事实是没有数据库会对普通语句编译后的执行代码缓存。这样
      每执行一次都要对传入的语句编译一次。
    - (语法检查，语义检查，翻译成二进制命令，缓存)
- PreparedStatement 可以防止 SQL 注入;

### Java 与 sql 对应数据类型转换表

| Java类型               | SQL类型                          |
|----------------------|--------------------------------|
| `boolean`            | `BIT`                          |
| `byte`               | `TINYINT`                      |
| `short`              | `SMALLINT`                     |
| `int`                | `INTEGER`                      |
| `long`               | `BIGINT`                       |
| `String`             | `CHAR`,`VARCHAR`,`LONGVARCHAR` |
| `byte`   `array`     | `BINARY`,`VAR BINARY`          |
| `java.sql.Date`      | `DATE`                         |
| `java.sql.Time`      | `TIME`                         |
| `java.sql.Timestamp` | `TIMESTAMP`                    |

### 使用 `PreparedStatement` 实现 增删改操作


