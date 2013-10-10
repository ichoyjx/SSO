/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.sql.*;
import java.util.*;

/**
 *�����û��ֶ���ѯ���û���Ϣ
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
             * ֱ������access�ļ���
             */
            String dbur1 = "jdbc:odbc:AuthenDB;";//��Ҫ��������Դ
            String user = sqlConn.substring(0, sqlConn.indexOf("\n"));
            String psw = sqlConn.substring(sqlConn.indexOf("\n") + 1);
            Properties prop = new Properties();
            prop.put("charSet", "gb2312");
            prop.put("user", user);
            prop.put("password", psw);
            conn = DriverManager.getConnection(dbur1, prop);
        } catch (Exception e) {
            System.out.println(e.toString() + "�������ݿ�ʧ�ܣ�");
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
                win.userInfo.append("�û�" + username + "���ߣ�����ʱ�䣺" + rs.getString(1) + "\n");
            } else {
                win.userInfo.append("�û�" + username + "������\n");
            }
            while (rs.next()) {
                win.userInfo.append("�û�" + username + "���ߣ�����ʱ�䣺" + rs.getString(1) + "\n");
            }
            win.userInfo.append("�û�" + username + "��ȥ��������Ϣ:");
            rs = stmt.executeQuery(OfflineStr);
            if (rs.next()) {
                win.userInfo.append("\n�û�" + username + "��" + rs.getString(1) + "����,��" + rs.getString(2) + "����\n");
            } else {
                win.userInfo.append("��");
            }
            while (rs.next()) {
                win.userInfo.append("�û�" + username + "��" + rs.getString(1) + "����,��" + rs.getString(2) + "����\n");
            }
        } catch (Exception e) {
            win.userInfo.append("��ѯʧ��" + e);
        }
        try {
            stmt.close();
            conn.close();
        } catch (Exception e) {
        }

    }
}