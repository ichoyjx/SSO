/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.net.*;
import java.io.*;

/**
 *������Ϣ���෽��
 * @author icho
 */
public class rcvMsg {

    ServerSocket serverSocket;//����Socket
    String sqlConn = null;//���ݿ������ַ�����ʽusername\npassword
    Authentication_server win;

    rcvMsg(ServerSocket serverSocket, String sqlConn, Authentication_server win) {
        this.win = win;
        this.serverSocket = serverSocket;
        this.sqlConn = sqlConn;
    }

    /**
     * �����׽��ֵķ���
     * @param msg
     * @return
     */
    public boolean Listen() {
        Socket you = null;//�������ӵ��׽���
        if (win != null) {
            win.text1.setText("��ʼ����\n");
        }
        while (true) {
            try {
                you = serverSocket.accept();
                new ProcessMessages(you, sqlConn, win).start();
            } catch (IOException e) {
                System.out.println("���ڵȴ��û�");
            }
        }

    }
}
