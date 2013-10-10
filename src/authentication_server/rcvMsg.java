/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.net.*;
import java.io.*;

/**
 *接收消息的类方法
 * @author icho
 */
public class rcvMsg {

    ServerSocket serverSocket;//监听Socket
    String sqlConn = null;//数据库连接字符串格式username\npassword
    Authentication_server win;

    rcvMsg(ServerSocket serverSocket, String sqlConn, Authentication_server win) {
        this.win = win;
        this.serverSocket = serverSocket;
        this.sqlConn = sqlConn;
    }

    /**
     * 监听套接字的方法
     * @param msg
     * @return
     */
    public boolean Listen() {
        Socket you = null;//接收连接的套接字
        if (win != null) {
            win.text1.setText("开始监听\n");
        }
        while (true) {
            try {
                you = serverSocket.accept();
                new ProcessMessages(you, sqlConn, win).start();
            } catch (IOException e) {
                System.out.println("正在等待用户");
            }
        }

    }
}
