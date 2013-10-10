/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.sql.*;
import java.util.*;

/**
 *返回用户手动查询的用户信息
 * @author icho
 */
public class GetUserInfo extends Thread {

    String username;
    String sqlConn;
    Connection conn;
    Authentication_server win;

    GetUserInfo(String username, String sqlConn, Authentication_server win) {
        this.sqlConn = sqlConn;
        this.username = username;
        this.win = win;
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

    @Override
    public void run() {
        String OnlineStr = "SELECT OnlineTime FROM OnlineUser"
                + " WHERE UserName='" + username + "'";
        String OfflineStr = "SELECT OnlineTime,OfflineTime FROM OfflineUser"
                + " WHERE UserName='" + username + "'";
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(OnlineStr);
            if (rs.next()) {
                win.userInfo.append("用户" + username + "在线，上线时间：" + rs.getString(1) + "\n");
            } else {
                win.userInfo.append("用户" + username + "不在线\n");
            }
            while (rs.next()) {
                win.userInfo.append("用户" + username + "在线，上线时间：" + rs.getString(1) + "\n");
            }
            win.userInfo.append("用户" + username + "过去上下线信息:");
            rs = stmt.executeQuery(OfflineStr);
            if (rs.next()) {
                win.userInfo.append("\n用户" + username + "于" + rs.getString(1) + "上线,于" + rs.getString(2) + "下线\n");
            } else {
                win.userInfo.append("无");
            }
            while (rs.next()) {
                win.userInfo.append("用户" + username + "于" + rs.getString(1) + "上线,于" + rs.getString(2) + "下线\n");
            }
        } catch (Exception e) {
            win.userInfo.append("查询失败" + e);
        }
        try {
            stmt.close();
            conn.close();
        } catch (Exception e) {
        }

    }
}
