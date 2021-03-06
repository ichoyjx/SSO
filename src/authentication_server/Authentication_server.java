/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication_server;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 *程序入口
 * @author icho
 */
public class Authentication_server extends Frame implements ActionListener {

    /**
     * @param args the command line arguments
     * @throws Exception  
     */
    TextArea text1;
    TextArea userInfo;
    TextField TextUsername;
    Button searchBtn;
    Label lb1;
    Label lb2;
    Label lb3;
    Label lb4;
    Label server1;
    Label server2;
    Label server3;
    Label server4;

    Authentication_server(String windowName) {
        //初始化主窗口
        super(windowName);
        setLayout(null);
        setSize(800, 600);
        setLocation(100, 100);
        //****************
        //初始化标签1
        lb1 = new Label("认证服务器接收到的信息：");
        lb1.setBounds(50, 50, 150, 50);
        lb1.setVisible(true);
        add(lb1);
        //****************
        //初始化标签2
        lb2 = new Label(" 各服务器当前在线人数：");
        lb2.setBounds(370, 50, 150, 50);
        lb2.setVisible(true);
        add(lb2);
        //****************
        //初始化标签3
        lb3 = new Label(" 手动查询：");
        lb3.setBounds(370, 220, 150, 30);
        lb3.setVisible(true);
        add(lb3);
        //****************
        //初始化标签4
        lb4 = new Label(" 请输入用户名：");
        lb4.setBounds(370, 250, 80, 50);
        lb4.setVisible(true);
        add(lb4);
        //****************
        //初始化各服务器标签
        server1 = new Label("Server1在线人数：123456789");
        server1.setBounds(370, 100, 200, 50);
        server1.setVisible(true);
        add(server1);
        server2 = new Label("Server2在线人数：123456789");
        server2.setBounds(570, 100, 200, 50);
        server2.setVisible(true);
        add(server2);
        server3 = new Label("Server3在线人数：123456789");
        server3.setBounds(370, 150, 200, 50);
        server3.setVisible(true);
        add(server3);
        server4 = new Label("Server4在线人数：123456789");
        server4.setBounds(570, 150, 200, 50);
        server4.setVisible(true);
        add(server4);
        //*********************
        //初始化查询按钮
        searchBtn = new Button("查询");
        searchBtn.setBounds(615, 260, 50, 25);
        searchBtn.setVisible(true);
        searchBtn.addActionListener(this);
        add(searchBtn);
        //****************
        //初始化文本域
        text1 = new TextArea();
        text1.setEditable(false);
        text1.setLocation(50, 100);
        text1.setSize(300, 450);
        text1.addTextListener(new TextListener() {

            @Override//当输出一定数量时，清空textarea，以免造成内存溢出
            public void textValueChanged(TextEvent e) {
                int length = text1.getCaretPosition();
                if (length > 3000) {
                    text1.setText("开始监听\n");
                }
            }
        });
        add(text1);
        //****************
        //初始化文本域userInfo
        userInfo = new TextArea();
        userInfo.setEditable(false);
        userInfo.setLocation(370, 300);
        userInfo.setSize(350, 250);
        add(userInfo);
        userInfo.setVisible(true);
        //****************
        //初始化文本框
        TextUsername = new TextField();
        TextUsername.setBounds(460, 265, 150, 20);
        TextUsername.setEditable(true);
        TextUsername.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                TextField text = (TextField) e.getSource();
                if (e.getKeyChar() != KeyEvent.VK_BACK_SPACE && text.getText().length() >= 20) {
                    e.consume();
                }
            }
        });
        add(TextUsername);
        setVisible(true);
        //****************
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        validate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn) {//为用户查询而做，不需要UI请注释掉下面3行
            userInfo.setText(null);
            GetUserInfo gui = new GetUserInfo(TextUsername.getText(), "Admin\n123456", this);
            gui.start();
        }
    }

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        //用户UI部分,不需要UI请注释掉下面3行
        Authentication_server win = new Authentication_server("认证服务器运行监视器");
        ShowSqlData ssd = new ShowSqlData("Admin\n123456", win);
        ssd.start();
        //*************
        /*
        //创建数据库
        sqlData sd=new sqlData("Admin\n123456");
        try
        {
        System.out.print(sd.CreateDatabase());
        }
        catch(Exception ex)
        {
        
        System.out.println("创建数据库失败!"+ex.toString());
        sd.Close();
        return;
        }
         */
        ///********************************
        //打开监听端口，建立监听套接字
        rcvMsg receiveMessage = null;
        try {
            ServerSocket serverSocket = new ServerSocket(6271);
            receiveMessage = new rcvMsg(serverSocket, "Admin\n123456", win);//不需要UI请将win参数改为null
        } catch (IOException e) {
            System.out.println(e.toString() + "初始化监听套接字失败!");
            return;
        }
        receiveMessage.Listen();
        //**************************


    }
}
