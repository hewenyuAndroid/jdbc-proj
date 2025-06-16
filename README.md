[toc]

# JDBC概述

- JDBC(Java Database Connectivity)是一个**独立于特定数据库管理系统、通用的SQL数据库存取和操作的公共接口**（一组API），定义了用来访问数据库的标准Java类库，（**java.sql,javax.sql**）使用这些类库可以以一种**标准**的方法、方便地访问数据库资源。
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

注: ODBC(**Open Database Connectivity**，开放式数据库连接)，是微软在Windows平台下推出的。使用者在程序中只需要调用ODBC API，由 ODBC 驱动程序将调用转换成为对特定的数据库的调用请求。

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
  - 通常不用显式调用 DriverManager 类的 registerDriver() 方法来注册驱动程序类的实例，因为 Driver 接口的驱动程序类**都**包含了静态代码块，在这个静态代码块中，会调用 DriverManager.registerDriver() 方法来注册自身的一个实例。

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
  - **子名称**：一种标识数据库的方法。子名称可以依不同的子协议而变化，用子名称的目的是为了**定位数据库**提供足够的信息。包含**主机名**(对应服务端的ip地址)**，端口号，数据库名**

## jdbc 连接数据库

使用反射加载驱动类

```java
Class clz = Class.forName("com.mysql.cj.jdbc.Driver");
Driver driver = (Driver) clz.newInstance();

// 注册驱动
DriverManager.registerDriver(driver);

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
System.out.println(connection);
```

