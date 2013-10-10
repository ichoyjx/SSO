/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.sql.*;
import java.util.*;

/**
 *ÿ��3�벻��ˢ�µ�ǰÿ���������������û�����
 * @author icho
 */
public class ShowSqlData extends Thread {

    String sqlConn;
    Connection conn;
    Authentication_server win;

    ShowSqlData(String sqlConn, Authentication_server win) {
        this.sqlConn = sqlConn;
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
        try {
            Statement stmt = conn.createStatement();
            getServerInfo(stmt);
        } catch (SQLException e) {
            System.out.println("�����Ựʧ��" + e);
        }
    }

    public void getServerInfo(Statement stmt) {
        String server1Str = "SELECT COUNT(Identifies) FROM Server1";
        String server2Str = "SELECT COUNT(Identifies) FROM Server2";
        String server3Str = "SELECT COUNT(Identifies) FROM Server3";
        String server4Str = "SELECT COUNT(Identifies) FROM Server4";
        while (true) {
            try {
                ResultSet rs = stmt.executeQuery(server1Str);
                if (rs.next()) {
                    win.server1.setText("Server1����������" + rs.getInt(1));
                } else {
                    win.server1.setText("Server1����������" + 0);
                }
                rs = stmt.executeQuery(server2Str);
                if (rs.next()) {
                    win.server2.setText("Server2����������" + rs.getInt(1));
                } else {
                    win.server2.setText("Server2����������" + 0);
                }
                rs = stmt.executeQuery(server3Str);
                if (rs.next()) {
                    win.server3.setText("Server3����������" + rs.getInt(1));
                } else {
                    win.server3.setText("Server3����������" + 0);
                }
                rs = stmt.executeQuery(server4Str);
                if (rs.next()) {
                    win.server4.setText("Server4����������" + rs.getInt(1));
                } else {
                    win.server4.setText("Server4����������" + 0);
                }
                Thread.sleep(3000);
            } catch (SQLException ex) {
                System.out.println("��ѯ���ݿ�ʧ�ܣ�" + ex);

                break;
            } catch (InterruptedException e) {
            }
        }
        try {
            conn.close();
        } catch (Exception ex) {
        }
    }
}