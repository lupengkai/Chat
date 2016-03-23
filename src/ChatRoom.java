import model.Message;
import model.Type;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class ChatRoom extends Frame {
    TextField tfText = new TextField();
    TextArea taContent = new TextArea();
    Panel membersPanel = new Panel();
    List memberList = new List();


    Socket s = null;
    String str = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    private boolean bConnected = false;

    private String name = null;


    public static void main(String[] args) {


    }

    public ChatRoom(String name) {
        this.name = name;
    }


    public void launchFrame() {

        setLocation(400, 300);
        setSize(800, 900);
        setTitle("User: " + name);
        add(tfText, BorderLayout.SOUTH);
        membersPanel.setLayout(new GridLayout(10, 1));
        membersPanel.setSize(60, 100);

        memberList.addActionListener(new ListLisener());
        membersPanel.add(memberList);
        add(membersPanel, BorderLayout.EAST);
        add(taContent, BorderLayout.CENTER);
        pack();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    oos.close();
                    ois.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                disconnect();
                System.exit(0);
            }

        });
        tfText.addActionListener(new TFListener());
        setVisible(true);
        connect();
        bConnected = true;
        Show sw = new Show();
        new Thread(sw).start();
    }


    public void connect() {
        try {
            s = new Socket("localhost", 6666);
            oos = new ObjectOutputStream(s.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("connected");
    }

    public void disconnect() {
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            Message message = new Message();
            message.setType(model.Type.MESSAGE);
            message.setUsername(name);
            message.setMessage(str);

            oos.writeObject(message);
            oos.flush();
            //dos.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private class Show implements Runnable {
        public Show() {
            try {
                ois = new ObjectInputStream(s.getInputStream());
                System.out.println("init show");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void run() {
            System.out.println("client run");
            Message m = new Message();
            m.setType(model.Type.ENTER);
            m.setUsername(name);
            try {
                oos.writeObject(m);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (bConnected) {
                try {
                    //System.out.println("xunhusn");
                    Message message = (Message) ois.readObject();
                    System.out.println(456);
                    String str = message.getMessage();
                    taContent.setText(taContent.getText() + message +"\n");
                } catch (SocketException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }


        }

    }


    private class ListLisener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String friend = memberList.getSelectedItem();

        }
    }


    private class TFListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            str = tfText.getText().trim();
            //taContent.setText(str);
            sendMessage();
            tfText.setText("");


        }

    }

}