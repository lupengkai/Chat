import model.ACK;
import model.Message;
import model.Type;
import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tage on 3/23/16.
 */
public class Register extends JFrame {

    Socket s = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;




    //创建面板对象
    JPanel jpanel = new JPanel();

    //创建控件对象
    JLabel labZc = new JLabel("Register");
    JLabel labName = new JLabel("User Name:");
    JLabel labPassword1 = new JLabel("Password:");
    JLabel labPassword2 = new JLabel("Password:");
    JButton jbOk = new JButton("OK");
    JButton jbQx = new JButton("Cancel");

    JTextField txtName = new JTextField();
    JTextField txtPassword1= new JTextField();
    JTextField txtPassword2 = new JTextField();


    public Register(){

        this.setResizable(false);
        this.setTitle("User Register");
        this.setBounds(300, 300, 300, 400);

        jpanel.setLayout(null);

        labZc.setBounds(100, 20, 100, 20);
        jpanel.add(labZc);
        labName.setBounds(10, 60, 120, 20);
        jpanel.add(labName);
        txtName.setBounds(120, 60, 100, 20);
        jpanel.add(txtName);
        labPassword1.setBounds(10, 100, 120, 20);

        jpanel.add(labPassword1);

        txtPassword1.setBounds(120, 100, 100, 20);

        jpanel.add(txtPassword1);
        labPassword2.setBounds(10, 140, 120, 20);
        jpanel.add(labPassword2);
        txtPassword2.setBounds(120, 140, 100, 20);
        jpanel.add(txtPassword2);

        jbOk.setBounds(60, 260, 80, 20);
        jpanel.add(jbOk);
        Button_Qd qd = new Button_Qd();
        jbOk.addActionListener(qd);
        jbQx.setBounds(160, 260, 100, 20);
        jpanel.add(jbQx);
        Button_Qx qx = new Button_Qx();
        jbQx.addActionListener(qx);
        this.add(jpanel);
        connect();
    }
    class Button_Qd implements ActionListener {


        @Override
        public void actionPerformed(ActionEvent e) {
            String name = txtName.getText().trim();
            String password1 = txtPassword1.getText().trim();
            String password2 = txtPassword2.getText().trim();

            if (password1 == null || password1.equals("") || password2 == null || password2.equals("")) {
                JOptionPane.showMessageDialog(null, "Passwords is empty !", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (txtPassword1 == txtPassword2) {
                JOptionPane.showMessageDialog(null, "Passwords are not same!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Message m = new Message();
                m.setPassword(password1);
                m.setUsername(name);
                m.setType(model.Type.REGISTER);
                sendMessage(m);

                ACK ack = null;
                try {
                    ack = (ACK)ois.readObject();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

                if (ack.isPermit()){


                    JOptionPane.showMessageDialog(null, "Register Success");
                    dispose();





                } else {
                    JOptionPane.showMessageDialog(null, "Name repeat! ","Error", JOptionPane.ERROR_MESSAGE);
                }




                dispose();
            }


        }

    }
    class Button_Qx implements ActionListener{


        @Override
        public void actionPerformed(ActionEvent e) {

            txtName.setText("");
            txtPassword1.setText("");
            txtPassword2.setText("");
        }

    }



    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Register().setVisible(true);
            }
        });
    }



    public void connect() {
        try {
            s = new Socket("localhost",6666);
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




}