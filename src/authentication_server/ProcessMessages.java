/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.net.*;
import java.io.*;

/**
 *处理接收到的消息
 * @author icho
 */
public class ProcessMessages extends Thread {//

    Socket socket;
    DataInputStream in = null;
    DataOutputStream out = null;//  
    String msg = null;
    String sqlConn = null;//数据库连接字符串
    Authentication_server win;//可视化窗口

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
            System.out.println(socket.getInetAddress() + "无法读取信息" + e.toString());
            //break;
        }
        if (msg != null) {
            String requestType = msg.substring(0, msg.indexOf("#"));
            try {
                // Socket sendSocket=new Socket(socket.getInetAddress(),6271);
                // sendMsg sM=new sendMsg(sendSocket);
                String sendMessage = null;
                if ("request".equals(requestType)) {
                    //为UI而做的，显示查询请求信息
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String username = userData.substring(userData.indexOf("\n") + 1);
                        win.text1.append("收到对用户 " + username + " 的查询请求\n");
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
                    out = new DataOutputStream(socket.getOutputStream());//提前建立会出错
                    out.writeUTF(sendMessage);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("关闭数据库失败" + e);
                    }
                } else if ("online".equals(requestType)) {
                    //为UI而做的，显示用户上线信息
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String temp = userData;
                        //String  Identifies=temp.substring(0,temp.indexOf("\n"));//标识，需要从userData中提取
                        temp = temp.substring(temp.indexOf("\n") + 1);
                        String username = temp.substring(0, temp.indexOf("\n"));//用户名，需要从userData中提取
                        temp = temp.substring(temp.indexOf("\n") + 1);
                        //String password=temp.substring(0,temp.indexOf("\n"));//密码，需要从userData中提取
                        temp = temp.substring(temp.indexOf("\n") + 1);
                        String servername = temp;//应用服务器名，需要从userData中提取    
                        win.text1.append("收到来自 " + servername + " 的用户 " + username + " 上线消息\n");
                    }
                    //****************************
                    String userInfo = msg.substring(msg.indexOf("#") + 1);
                    sqlData sD = new sqlData(sqlConn);
                    if (sD.InsertData(userInfo)) {
                        sendMessage = "success";
                    } else {
                        sendMessage = "fail";
                    }
                    out = new DataOutputStream(socket.getOutputStream());//提前建立会出错
                    out.writeUTF(sendMessage);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("关闭数据库失败" + e);
                    }
                } else if ("offline".equals(requestType)) {
                    //为UI而做的，显示查询请求信息
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String temp = userData.substring(userData.indexOf("\n") + 1);
                        String username = temp.substring(0, temp.indexOf("\n"));
                        String servername = temp.substring(temp.indexOf("\n") + 1);
                        win.text1.append("收到来自 " + servername + " 的用户 " + username + "的下线信息\n");
                    }
                    //****************************
                    String userName = msg.substring(msg.indexOf("#") + 1);
                    sqlData sD = new sqlData(sqlConn);
                    if (sD.DeleteData(userName)) {
                        sendMessage = "success";
                    } else {
                        sendMessage = "fail";
                    }
                    out = new DataOutputStream(socket.getOutputStream());//提前建立会出错
                    out.writeUTF(sendMessage);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("关闭数据库失败" + e);
                    }
                } else if ("logout".equals(requestType)) {
                    //为UI而做的，显示注销信息
                    if (win != null) {
                        String userData = msg.substring(msg.indexOf("#") + 1);
                        String temp = userData.substring(userData.indexOf("\n") + 1);
                        String username = temp.substring(0, temp.indexOf("\n"));
                        String servername = temp.substring(temp.indexOf("\n") + 1);
                        win.text1.append("收到来自 " + servername + " 的用户 " + username + "的注销信息\n");
                    }
                    //****************************
                    String userName = msg.substring(msg.indexOf("#") + 1);
                    sqlData sD = new sqlData(sqlConn);
                    sD.Logout(userName);
                    try {
                        sD.Close();
                    } catch (Exception e) {
                        System.out.println("关闭数据库失败" + e);
                    }
                }
                // socket.close();
            } catch (IOException e) {
                System.out.println("消息处理失败" + e.toString());
                return;
            }
        }
        //}
    }
}
