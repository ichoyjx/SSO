/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.net.*;
import java.io.*;

/**
 *������յ�����Ϣ
 * @author icho
 */
public class ProcessMessages extends Thread {//

    Socket socket;
    DataInputStream in = null;
    DataOutputStream out = null;//  
    String msg = null;
    String sqlConn = null;//���ݿ������ַ���
    Authentication_server win;//���ӻ�����

    ProcessMessages(Socket socket, String sqlConn, Authentication_server win) {
        this.socket = socket;
        this.sqlConn = sqlConn;
        this.win = win;
    }

    @Override
    public void run() {
        //while(true)
        //{
        try {
            in = new DataInputStream(socket.getInputStream());
            // out=new DataOutputStream(socket.getOutputStream());
            msg = in.readUTF();

            //PrintWriter wtr=new PrintWriter(socket.getOutputStream());
            // BufferedReader rdr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //msg=rdr.readLine();
        } catch (IOException e) {
            System.out.println(socket.getInetAddress() + "�޷���ȡ��Ϣ" + e.toString());
            //break;
        }
        if (msg != null) {
            String requestType = msg.substring(0, msg.indexOf("#"));
            try {
                // Socket sendSocket=new Socket(socket.getInetAddress(),6271);
                // sendMsg sM=new sendMsg(sendSocket);
                String sendMessage = null;
                if ("request".equals(requestType)) {
                    //ΪUI�����ģ���ʾ��ѯ������Ϣ
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String username = userData.substring(userData.indexOf("\n") + 1);
                        win.text1.append("�յ����û� " + username + " �Ĳ�ѯ����\n");
                    }
                    //****************************
                    String userName = msg.substring(msg.indexOf("#") + 1);
                    sqlData sD = new sqlData(sqlConn);
                    sendMessage = sD.FindData(userName);
                    if (sendMessage == null) {
                        sendMessage = "fail";
                    } else {
                        sendMessage = "success#" + sendMessage;
                    }
                    out = new DataOutputStream(socket.getOutputStream());//��ǰ���������
                    out.writeUTF(sendMessage);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("�ر����ݿ�ʧ��" + e);
                    }
                } else if ("online".equals(requestType)) {
                    //ΪUI�����ģ���ʾ�û�������Ϣ
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String temp = userData;
                        //String  Identifies=temp.substring(0,temp.indexOf("\n"));//��ʶ����Ҫ��userData����ȡ
                        temp = temp.substring(temp.indexOf("\n") + 1);
                        String username = temp.substring(0, temp.indexOf("\n"));//�û�������Ҫ��userData����ȡ
                        temp = temp.substring(temp.indexOf("\n") + 1);
                        //String password=temp.substring(0,temp.indexOf("\n"));//���룬��Ҫ��userData����ȡ
                        temp = temp.substring(temp.indexOf("\n") + 1);
                        String servername = temp;//Ӧ�÷�����������Ҫ��userData����ȡ    
                        win.text1.append("�յ����� " + servername + " ���û� " + username + " ������Ϣ\n");
                    }
                    //****************************
                    String userInfo = msg.substring(msg.indexOf("#") + 1);
                    sqlData sD = new sqlData(sqlConn);
                    if (sD.InsertData(userInfo)) {
                        sendMessage = "success";
                    } else {
                        sendMessage = "fail";
                    }
                    out = new DataOutputStream(socket.getOutputStream());//��ǰ���������
                    out.writeUTF(sendMessage);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("�ر����ݿ�ʧ��" + e);
                    }
                } else if ("offline".equals(requestType)) {
                    //ΪUI�����ģ���ʾ��ѯ������Ϣ
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String temp = userData.substring(userData.indexOf("\n") + 1);
                        String username = temp.substring(0, temp.indexOf("\n"));
                        String servername = temp.substring(temp.indexOf("\n") + 1);
                        win.text1.append("�յ����� " + servername + " ���û� " + username + "��������Ϣ\n");
                    }
                    //****************************
                    String userName = msg.substring(msg.indexOf("#") + 1);
                    sqlData sD = new sqlData(sqlConn);
                    if (sD.DeleteData(userName)) {
                        sendMessage = "success";
                    } else {
                        sendMessage = "fail";
                    }
                    out = new DataOutputStream(socket.getOutputStream());//��ǰ���������
                    out.writeUTF(sendMessage);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("�ر����ݿ�ʧ��" + e);
                    }
                } else if ("logout".equals(requestType)) {
                    //ΪUI�����ģ���ʾע����Ϣ
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String temp = userData.substring(userData.indexOf("\n") + 1);
                        String username = temp.substring(0, temp.indexOf("\n"));
                        String servername = temp.substring(temp.indexOf("\n") + 1);
                        win.text1.append("�յ����� " + servername + " ���û� " + username + "��ע����Ϣ\n");
                    }
                    //****************************
                    String userName = msg.substring(msg.indexOf("#") + 1);
                    sqlData sD = new sqlData(sqlConn);
                    sD.Logout(userName);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("�ر����ݿ�ʧ��" + e);
                    }
                }
                // socket.close();
            } catch (IOException e) {
                System.out.println("��Ϣ����ʧ��" + e.toString());
                return;
            }
        }
        //}
    }
}
