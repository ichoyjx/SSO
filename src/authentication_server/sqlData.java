/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.sql.*;
import java.text.DateFormat;
import java.util.*;

/**
 *有关数据库的各种操作
 * @author icho
 */
public class sqlData {

    String sqlConn;
    Connection conn;

    sqlData(String sqlConn) {
        this.sqlConn = sqlConn;
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            /**
             * 直接连接access文件。
             */
            String dbur1 = "jdbc:odbc:AuthenDB;";//需要设置数据源
            String user = sqlConn.substring(0, sqlConn.indexOf("\n"));
            String psw = sqlConn.substring(sqlConn.indexOf("\n") + 1);
            Properties prop = new Properties();
            prop.put("charSet", "gb2312");
            prop.put("user", user);
            prop.put("password", psw);
            conn = DriverManager.getConnection(dbur1, prop);
        } catch (Exception e) {
            System.out.println(e.toString() + "连接数据库失败！");
        }
    }

    /**
     * 创建数据库
     * @return
     * @throws Exception
     */
    public boolean CreateDatabase() throws Exception {//创建数据库
        //标识，用户名，密码，cnt，上线时间
        //标识，用户名，密码，cnt，上线时间，下线时间
        //应用服务器1,2,3,4
        String createTableOnlineUser = "CREATE TABLE OnlineUser"
                + "(Identifies VARCHAR(200) NOT NULL,"
                + "UserName VARCHAR(50) NOT NULL,"
                + "Pass_word VARCHAR(50) NOT NULL,"
                + "Cnt INT NOT NULL,"
                + "OnlineTime datetime NOT NULL,"
                + "PRIMARY KEY(Identifies));";
        String createTableOfflineUser = "CREATE TABLE OfflineUser"
                + "(Identifies VARCHAR(200) NOT NULL, "
                + "UserName VARCHAR(50) NOT NULL,"
                + "Pass_word VARCHAR(50) NOT NULL,"
                + "Cnt INT NOT NULL,"
                + "OnlineTime datetime NOT NULL,"
                + "OfflineTime datetime NOT NULL);";
        String createTableServer1 = "CREATE TABLE Server1"
                + "(Identifies VARCHAR(200) NOT NULL,"
                + "UserName VARCHAR(50) NOT NULL,"
                + "PRIMARY KEY(Identifies));";
        String createTableServer2 = "CREATE TABLE Server2"
                + "(Identifies VARCHAR(200) NOT NULL,"
                + "UserName VARCHAR(50) NOT NULL,"
                + "PRIMARY KEY(Identifies));";
        String createTableServer3 = "CREATE TABLE Server3"
                + "(Identifies VARCHAR(200) NOT NULL,"
                + "UserName VARCHAR(50) NOT NULL,"
                + "PRIMARY KEY(Identifies));";
        String createTableServer4 = "CREATE TABLE Server4"
                + "(Identifies VARCHAR(200) NOT NULL,"
                + "UserName VARCHAR(50) NOT NULL,"
                + "PRIMARY KEY(Identifies));";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(createTableOnlineUser);
        stmt.executeUpdate(createTableOfflineUser);
        stmt.executeUpdate(createTableServer1);
        stmt.executeUpdate(createTableServer2);
        stmt.executeUpdate(createTableServer3);
        stmt.executeUpdate(createTableServer4);
        //System.out.println(rs);
        stmt.close();
        conn.close();
        return true;
    }

    /**
     * 收到用户开始新会话的消息的后的处理(cnt++或者建立新项,同时在对应服务器表添加该用户)
     * @param userData
     * @return
     * userData数据格式:Identifies\nUsername\nPassword\nServername
     */
    public boolean InsertData(String userData) {//插入新用户
        String temp = userData;
        String Identifies = temp.substring(0, temp.indexOf("\n"));//标识，需要从userData中提取
        temp = temp.substring(temp.indexOf("\n") + 1);
        String username = temp.substring(0, temp.indexOf("\n"));//用户名，需要从userData中提取
        temp = temp.substring(temp.indexOf("\n") + 1);
        String password = temp.substring(0, temp.indexOf("\n"));//密码，需要从userData中提取
        temp = temp.substring(temp.indexOf("\n") + 1);
        String servername = temp;//应用服务器名，需要从userData中提取
        String date;
        //上线的用户添加到server表中，标识用户在访问哪些应用服务器
        int executeUpdate = 0;
        String insertServerStr = "INSERT INTO " + servername + "(Identifies,UserName)"
                + "VALUES('" + Identifies + "','" + username + "')";
        try {
            Statement stmt = conn.createStatement();
            executeUpdate = stmt.executeUpdate(insertServerStr);
            stmt.close();
        } catch (SQLException e) {
            //System.out.println(e);
        }
        //****************************************************************
        //添加到server表成功才能做cnt++操作，保证正确性
        if (executeUpdate == 1) {
            if (FindData(Identifies + "\n" + username) == null) {
                //没有该用户,做插入操作
                int Cnt = 1;
                java.util.Date now = new java.util.Date();
                DateFormat day = DateFormat.getDateTimeInstance();
                date = day.format(now);
                String insertStr = "INSERT INTO OnlineUser(Identifies,UserName,Pass_word,Cnt,OnlineTime)"
                        + "VALUES('" + Identifies + "','" + username + "','" + password + "'," + Cnt + ",'" + date + "')";
                try {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(insertStr);
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("添加数据失败!" + e);
                    return false;
                }
            } else {
                //该用户已经存在，做cnt++操作
                String updateStr = "UPDATE OnlineUser SET Cnt=Cnt+1"
                        + " WHERE Identifies='" + Identifies + "'";
                try {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(updateStr);
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("更新数据失败!" + e);
                    return false;
                }
            }

            return true;
        } else {
            //该服务器已经存在该用户，不做其他操作
            return false;
        }
    }

    /**
     * 接收到查询请求的操作，返回密码
     * @param userData
     * @return
     * userData数据格式: Identifies\nUsername
     */
    public String FindData(String userData) {//查找用户信息
        String Identifies = userData.substring(0, userData.indexOf("\n"));//需要从userData中提取
        String username = userData.substring(userData.indexOf('\n') + 1, userData.length());//需要从userData中提取
        try {
            Statement stmt = conn.createStatement();
            String searchStr = "select Pass_word from OnlineUser where UserName= '" + username
                    + "' and  Identifies='" + Identifies + "'";
            ResultSet rs = stmt.executeQuery(searchStr);

            if (rs.next()) {
                return rs.getString(1);
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("查询数据库失败!" + e);

        }

        return null;
    }

    /**
     * 接收到用户断开的消息时的操作（cnt--）同时在对应服务器表删除该用户，当cnt==0时做延时删除，
     * @param userData
     * userData数据格式: Identifies\nUsername\nServername
     * @return
     */
    public boolean DeleteData(String userData) {//仅当cnt=0时延时删除用户
        String Identifies = userData.substring(0, userData.indexOf("\n"));//需要从userData中提取
        String temp = userData.substring(userData.indexOf("\n") + 1);
        String servername = temp.substring(temp.indexOf("\n") + 1);
        String deStr = "UPDATE OnlineUser SET Cnt=Cnt-1"
                + " WHERE Identifies='" + Identifies + "'";//cnt--操作
        String deleteServer = "DELETE FROM " + servername + " WHERE Identifies='" + Identifies + "'";//将用户从对应的server表中移除
        String getCnt = "SELECT Cnt FROM OnlineUser WHERE Identifies='" + Identifies + "'";//将用户放到下线列表中
        int Cnt = 0;
        int executeUpdate = 0;
        try {
            //只有先从server表中成功删除才能cnt--，保证正确性
            Statement stmt = conn.createStatement();
            executeUpdate = stmt.executeUpdate(deleteServer);
            stmt.close();
        } catch (SQLException e) {
            //System.out.println("更新数据失败!"+e);
            //return false;
        }
        if (executeUpdate == 1) {
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(deStr);
                ResultSet rs = stmt.executeQuery(getCnt);
                if (rs.next()) {
                    Cnt = rs.getInt(1);
                }
                if (Cnt == 0) {//做延迟删除操作
                    Thread.sleep(1000);
                    rs = stmt.executeQuery(getCnt);
                    if (rs.next()) {
                        Cnt = rs.getInt(1);
                    }
                    if (Cnt == 0) {//等待时间过了，从OnlineUser和所有server表中删除该用户，并且放到OfflineUser表中
                        String deleteStr = "DELETE FROM OnlineUser WHERE Identifies='" + Identifies + "'";
                        String getData = "SELECT UserName,Pass_word,OnlineTime "
                                + "FROM OnlineUser WHERE Identifies='" + Identifies + "'";
                        String date;
                        java.util.Date now = new java.util.Date();
                        DateFormat day = DateFormat.getDateTimeInstance();
                        date = day.format(now);
                        String username = null;
                        String password = null;
                        String datetime = null;
                        rs = stmt.executeQuery(getData);
                        if (rs.next()) {
                            username = rs.getString(1);
                            password = rs.getString(2);
                            datetime = rs.getString(3);
                        }
                        //如果offlineuser表中已经存在，则更新它
                        String insertStr = "INSERT INTO OfflineUser(Identifies,UserName,Pass_word,Cnt,OnlineTime,OfflineTime)"
                                + "VALUES('" + Identifies + "','" + username + "','" + password + "'," + 0 + ",'" + datetime + "','" + date + "')";
                        try {
                            stmt.executeUpdate(insertStr);
                        } catch (SQLException e) {
                        }
                        stmt.executeUpdate(deleteStr);
                        //**********************************************************************************************
                    }
                }
                stmt.close();
            } catch (SQLException e) {
                System.out.println("更新数据失败!" + e);
                return false;
            } catch (InterruptedException e) {
            }
            return true;
        } else {
            return false;
        }
    }
    /**
     * 接收到用户注销的消息时的操作在所有服务器表中删除该用户，在online表中删除该用户，移至offline表
     * @param userData
     * userData数据格式: Identifies\nUsername\nServername
     * @return
     */
    public void Logout(String userData) {
        String Identifies = userData.substring(0, userData.indexOf("\n"));//需要从userData中提取
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for (int i = 1; i < 5; i++) {
                String servername = "Server" + i;
                String deleteServer = "DELETE FROM " + servername + " WHERE Identifies='" + Identifies + "'";//将用户从对应的server表中移除
                try {
                    stmt.executeUpdate(deleteServer);
                } catch (SQLException e) {
                }
            }

            //从OnlineUser和所有server表中删除该用户，并且放到OfflineUser表中
            String deleteStr = "DELETE FROM OnlineUser WHERE Identifies='" + Identifies + "'";
            String getData = "SELECT UserName,Pass_word,OnlineTime "
                    + "FROM OnlineUser WHERE Identifies='" + Identifies + "'";
            String date;
            java.util.Date now = new java.util.Date();
            DateFormat day = DateFormat.getDateTimeInstance();
            date = day.format(now);
            String username = null;
            String password = null;
            String datetime = null;
            ResultSet rs = stmt.executeQuery(getData);
            if (rs.next()) {
                username = rs.getString(1);
                password = rs.getString(2);
                datetime = rs.getString(3);
            }
            String insertStr = "INSERT INTO OfflineUser(Identifies,UserName,Pass_word,Cnt,OnlineTime,OfflineTime)"
                    + "VALUES('" + Identifies + "','" + username + "','" + password + "'," + 0 + ",'" + datetime + "','" + date + "')";
            try {
                stmt.executeUpdate(insertStr);
            } catch (SQLException e) {
            }
            stmt.executeUpdate(deleteStr);
            stmt.close();
            //**********************************************************************************************
        } catch (SQLException e) {
            //System.out.println("更新数据失败!"+e);
            //return false;
        }
    }

    /**
     * 关闭数据库连接
     * @throws Exception
     */
    public void Close() throws Exception {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("关闭数据库失败" + e);
        }

    }
}
