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
 *�������
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
        //��ʼ��������
        super(windowName);
        setLayout(null);
        setSize(800, 600);
        setLocation(100, 100);
        //****************
        //��ʼ����ǩ1
        lb1 = new Label("��֤���������յ�����Ϣ��");
        lb1.setBounds(50, 50, 150, 50);
        lb1.setVisible(true);
        add(lb1);
        //****************
        //��ʼ����ǩ2
        lb2 = new Label(" ����������ǰ����������");
        lb2.setBounds(370, 50, 150, 50);
        lb2.setVisible(true);
        add(lb2);
        //****************
        //��ʼ����ǩ3
        lb3 = new Label(" �ֶ���ѯ��");
        lb3.setBounds(370, 220, 150, 30);
        lb3.setVisible(true);
        add(lb3);
        //****************
        //��ʼ����ǩ4
        lb4 = new Label(" �������û�����");
        lb4.setBounds(370, 250, 80, 50);
        lb4.setVisible(true);
        add(lb4);
        //****************
        //��ʼ������������ǩ
        server1 = new Label("Server1����������123456789");
        server1.setBounds(370, 100, 200, 50);
        server1.setVisible(true);
        add(server1);
        server2 = new Label("Server2����������123456789");
        server2.setBounds(570, 100, 200, 50);
        server2.setVisible(true);
        add(server2);
        server3 = new Label("Server3����������123456789");
        server3.setBounds(370, 150, 200, 50);
        server3.setVisible(true);
        add(server3);
        server4 = new Label("Server4����������123456789");
        server4.setBounds(570, 150, 200, 50);
        server4.setVisible(true);
        add(server4);
        //*********************
        //��ʼ����ѯ��ť
        searchBtn = new Button("��ѯ");
        searchBtn.setBounds(615, 260, 50, 25);
        searchBtn.setVisible(true);
        searchBtn.addActionListener(this);
        add(searchBtn);
        //****************
        //��ʼ���ı���
        text1 = new TextArea();
        text1.setEditable(false);
        text1.setLocation(50, 100);
        text1.setSize(300, 450);
        text1.addTextListener(new TextListener() {

            @Override//�����һ������ʱ�����textarea����������ڴ����
            public void textValueChanged(TextEvent e) {
                int length = text1.getCaretPosition();
                if (length > 3000) {
                    text1.setText("��ʼ����\n");
                }
            }
        });
        add(text1);
        //****************
        //��ʼ���ı���userInfo
        userInfo = new TextArea();
        userInfo.setEditable(false);
        userInfo.setLocation(370, 300);
        userInfo.setSize(350, 250);
        add(userInfo);
        userInfo.setVisible(true);
        //****************
        //��ʼ���ı���
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
        if (e.getSource() == searchBtn) {//Ϊ�û���ѯ����������ҪUI��ע�͵�����3��
            userInfo.setText(null);
            GetUserInfo gui = new GetUserInfo(TextUsername.getText(), "Admin\n123456", this);
            gui.start();
        }
    }

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        //�û�UI����,����ҪUI��ע�͵�����3��
        Authentication_server win = new Authentication_server("��֤���������м�����");
        ShowSqlData ssd = new ShowSqlData("Admin\n123456", win);
        ssd.start();
        //*************
        /*
        //�������ݿ�
        sqlData sd=new sqlData("Admin\n123456");
        try
        {
        System.out.print(sd.CreateDatabase());
        }
        catch(Exception ex)
        {
        
        System.out.println("�������ݿ�ʧ��!"+ex.toString());
        sd.Close();
        return;
        }
         */
        ///********************************
        //�򿪼����˿ڣ����������׽���
        rcvMsg receiveMessage = null;
        try {
            ServerSocket serverSocket = new ServerSocket(6271);
            receiveMessage = new rcvMsg(serverSocket, "Admin\n123456", win);//����ҪUI�뽫win������Ϊnull
        } catch (IOException e) {
            System.out.println(e.toString() + "��ʼ�������׽���ʧ��!");
            return;
        }
        receiveMessage.Listen();
        //**************************


    }
}
