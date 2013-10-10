/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.sql.*;
import java.text.DateFormat;
import java.util.*;

/**
 *�й����ݿ�ĸ��ֲ���
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

    /**
     * �������ݿ�
     * @return
     * @throws Exception
     */
    public boolean CreateDatabase() throws Exception {//�������ݿ�
        //��ʶ���û��������룬cnt������ʱ��
        //��ʶ���û��������룬cnt������ʱ�䣬����ʱ��
        //Ӧ�÷�����1,2,3,4
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
     * �յ��û���ʼ�»Ự����Ϣ�ĺ�Ĵ���(cnt++���߽�������,ͬʱ�ڶ�Ӧ�����������Ӹ��û�)
     * @param userData
     * @return
     * userData���ݸ�ʽ:Identifies\nUsername\nPassword\nServername
     */
    public boolean InsertData(String userData) {//�������û�
        String temp = userData;
        String Identifies = temp.substring(0, temp.indexOf("\n"));//��ʶ����Ҫ��userData����ȡ
        temp = temp.substring(temp.indexOf("\n") + 1);
        String username = temp.substring(0, temp.indexOf("\n"));//�û�������Ҫ��userData����ȡ
        temp = temp.substring(temp.indexOf("\n") + 1);
        String password = temp.substring(0, temp.indexOf("\n"));//���룬��Ҫ��userData����ȡ
        temp = temp.substring(temp.indexOf("\n") + 1);
        String servername = temp;//Ӧ�÷�����������Ҫ��userData����ȡ
        String date;
        //���ߵ��û����ӵ�server���У���ʶ�û��ڷ�����ЩӦ�÷�����
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
        //���ӵ�server���ɹ�������cnt++��������֤��ȷ��
        if (executeUpdate == 1) {
            if (FindData(Identifies + "\n" + username) == null) {
                //û�и��û�,���������
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
                    System.out.println("��������ʧ��!" + e);
                    return false;
                }
            } else {
                //���û��Ѿ����ڣ���cnt++����
                String updateStr = "UPDATE OnlineUser SET Cnt=Cnt+1"
                        + " WHERE Identifies='" + Identifies + "'";
                try {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(updateStr);
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("��������ʧ��!" + e);
                    return false;
                }
            }

            return true;
        } else {
            //�÷������Ѿ����ڸ��û���������������
            return false;
        }
    }

    /**
     * ���յ���ѯ����Ĳ�������������
     * @param userData
     * @return
     * userData���ݸ�ʽ: Identifies\nUsername
     */
    public String FindData(String userData) {//�����û���Ϣ
        String Identifies = userData.substring(0, userData.indexOf("\n"));//��Ҫ��userData����ȡ
        String username = userData.substring(userData.indexOf('\n') + 1, userData.length());//��Ҫ��userData����ȡ
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
            System.out.println("��ѯ���ݿ�ʧ��!" + e);

        }

        return null;
    }

    /**
     * ���յ��û��Ͽ�����Ϣʱ�Ĳ�����cnt--��ͬʱ�ڶ�Ӧ��������ɾ�����û�����cnt==0ʱ����ʱɾ����
     * @param userData
     * userData���ݸ�ʽ: Identifies\nUsername\nServername
     * @return
     */
    public boolean DeleteData(String userData) {//����cnt=0ʱ��ʱɾ���û�
        String Identifies = userData.substring(0, userData.indexOf("\n"));//��Ҫ��userData����ȡ
        String temp = userData.substring(userData.indexOf("\n") + 1);
        String servername = temp.substring(temp.indexOf("\n") + 1);
        String deStr = "UPDATE OnlineUser SET Cnt=Cnt-1"
                + " WHERE Identifies='" + Identifies + "'";//cnt--����
        String deleteServer = "DELETE FROM " + servername + " WHERE Identifies='" + Identifies + "'";//���û��Ӷ�Ӧ��server�����Ƴ�
        String getCnt = "SELECT Cnt FROM OnlineUser WHERE Identifies='" + Identifies + "'";//���û��ŵ������б���
        int Cnt = 0;
        int executeUpdate = 0;
        try {
            //ֻ���ȴ�server���гɹ�ɾ������cnt--����֤��ȷ��
            Statement stmt = conn.createStatement();
            executeUpdate = stmt.executeUpdate(deleteServer);
            stmt.close();
        } catch (SQLException e) {
            //System.out.println("��������ʧ��!"+e);
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
                if (Cnt == 0) {//���ӳ�ɾ������
                    Thread.sleep(1000);
                    rs = stmt.executeQuery(getCnt);
                    if (rs.next()) {
                        Cnt = rs.getInt(1);
                    }
                    if (Cnt == 0) {//�ȴ�ʱ����ˣ���OnlineUser������server����ɾ�����û������ҷŵ�OfflineUser����
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
                        //���offlineuser�����Ѿ����ڣ��������
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
                System.out.println("��������ʧ��!" + e);
                return false;
            } catch (InterruptedException e) {
            }
            return true;
        } else {
            return false;
        }
    }
    /**
     * ���յ��û�ע������Ϣʱ�Ĳ��������з���������ɾ�����û�����online����ɾ�����û�������offline��
     * @param userData
     * userData���ݸ�ʽ: Identifies\nUsername\nServername
     * @return
     */
    public void Logout(String userData) {
        String Identifies = userData.substring(0, userData.indexOf("\n"));//��Ҫ��userData����ȡ
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for (int i = 1; i < 5; i++) {
                String servername = "Server" + i;
                String deleteServer = "DELETE FROM " + servername + " WHERE Identifies='" + Identifies + "'";//���û��Ӷ�Ӧ��server�����Ƴ�
                try {
                    stmt.executeUpdate(deleteServer);
                } catch (SQLException e) {
                }
            }

            //��OnlineUser������server����ɾ�����û������ҷŵ�OfflineUser����
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
            //System.out.println("��������ʧ��!"+e);
            //return false;
        }
    }

    /**
     * �ر����ݿ�����
     * @throws Exception
     */
    public void Close() throws Exception {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("�ر����ݿ�ʧ��" + e);
        }

    }
}