import model.ACK;
import model.Message;
import model.Type;
import service.UserService;
import util.DB;
import util.UserNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;

/**
 * Created by tage on 3/23/16.
 */
public class Login extends Frame {
    Socket s = null;
    String str = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;



    TextField textField1;
    TextField textField2;
    public Login(){
        this.setTitle("Log in");
        this.setLayout(null);
        this.setSize(400, 300);
        this.setLocation(200, 300);
        setVisible(true);
        this.setResizable(false);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
        Panel panel = new Panel();
        panel.setBounds(40, 30, 320, 240);
        panel.setLayout(null);

        Label label1 = new Label("User Name:");
        label1.setBounds(40, 80, 100, 20);
        textField1 = new TextField();
        textField1.setBounds(150, 80, 140, 20);

        Label label2 = new Label("Password:");

        label2.setBounds(40, 130, 100, 20);
        textField2 = new TextField();
        textField2.setBounds(150, 130, 140, 20);

        Button btnOk = new Button("OK");
        Button btnCancel = new Button("Cancel");
        Button btnRegister = new Button("Register");
        btnOk.setBounds(30, 180, 60, 20);
        btnCancel.setBounds(105, 180, 80, 20);
        btnRegister.setBounds(200, 180, 80, 20);

//添加事件
        BtnOk ok= new BtnOk();
        btnOk.addActionListener(ok);
        BtnCancel cancel = new BtnCancel();
        btnCancel.addActionListener(cancel);
        BtnRegister register = new BtnRegister();
        btnRegister.addActionListener(register);

        System.out.print("123");
        panel.add(label1);
        panel.add(label2);
        panel.add(textField1);
        panel.add(textField2);
        panel.add(btnOk);
        panel.add(btnCancel);
        panel.add(btnRegister);

        this.add(panel);
        connect();


    }
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                new Login().setVisible(true);
            }
        });
//new Login().setVisible(true);

    }

    public void connect() {
        try {
            s = new Socket("localhost", 6666);
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("connected");
    }

    public void sendMessage(Message message) {
        try {
            oos.writeObject(message);
            oos.flush();
            //dos.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    //ok事件
    class BtnOk implements ActionListener {


        @Override
        public void actionPerformed(ActionEvent e) {

            String username = textField1.getText().trim();
            String password = textField2.getText().trim();
            if (username == null || username.equals("")) {
                JOptionPane.showMessageDialog(null, "User name is Empty! ", "Error", JOptionPane.ERROR_MESSAGE);

            } else if (password == null || password.equals("")) {

                JOptionPane.showMessageDialog(null, "Password id Empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {


                // UserService.getInstance().validate(username,password);
                Message message = new Message();
                message.setType(model.Type.LOG);
                message.setUsername(username);
                message.setPassword(password);
                sendMessage(message);
                ACK ack = null;
                try {
                    ack = (ACK) ois.readObject();
                    System.out.println(ack.getMsg());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

                if (ack.isPermit()) {


                    JOptionPane.showMessageDialog(null, "Login Success");
                    dispose();
                    new Thread(new ChatRoom(username)).start();


                } else {


                    if (ack.getMsg().equals("RepeatLog")) {
                        JOptionPane.showMessageDialog(null, "Repeat Log", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error User Name or error password ", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }


            }

        }
    }

    //cancel事件
    class BtnCancel implements ActionListener {


        @Override
        public void actionPerformed(ActionEvent e) {
            textField1.setText("");
            textField2.setText("");

        }

    }

    //register事件
    class BtnRegister implements ActionListener {


        @Override
        public void actionPerformed(ActionEvent e) {
            new Register().setVisible(true);

        }

    }

}