import model.Meet;
import model.Message;
import model.PrivateMessage;
import model.Type;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class ChatSingle extends Frame implements Runnable {
    TextField tfText = new TextField();
    TextArea taContent = new TextArea();


    Socket s = null;
    String str = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    private boolean bConnected = false;

    private String name = null;
    private String friendName = null;
    private int count;
    private int pcount;
    private boolean active;


    public ChatSingle(String name, String friendName, boolean active, int pcount) {
        this.name = name;
        this.friendName = friendName;
        this.active = active;
        this.pcount = pcount;
    }

    public static void main(String[] args) {
        new Thread(new ChatSingle("123", "abc", true, 0)).start();
    }

    public void run() {
        launchFrame();
    }

    public void launchFrame() {

        setLocation(400, 300);
        setSize(800, 900);
        setTitle("User: " + name + "  Friend: " + friendName);
        add(tfText, BorderLayout.SOUTH);

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
                //System.exit(0);
                dispose();
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
            PrivateMessage message = new PrivateMessage();
            message.setType(model.Type.MESSAGE);
            message.setUsername(name);
            message.setDestinationName(friendName);
            message.setMessage(str);
            message.setCount(count);
            message.setPcount(pcount);

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

            PrivateMessage m = new PrivateMessage();
            m.setType(model.Type.ENTER);


            if (active == true) {
                m.setMeet(Meet.HI);
                m.setPcount(0);
            } else {
                m.setMeet(Meet.HELLO);
            }
            m.setPcount(pcount);
            System.out.println("123de pcount" + pcount);


            m.setUsername(name);
            m.setMessage("HI");

            m.setDestinationName(friendName);
            try {
                oos.writeObject(m);
                oos.flush();
                System.out.println("send enter");
            } catch (IOException e) {
                e.printStackTrace();
            }


            while (bConnected) {
                try {
                    //System.out.println("xunhusn");
                    PrivateMessage message = (PrivateMessage) ois.readObject();


                    if (message.getType() == model.Type.ENTER && message.getUsername().equals(name)) {

                        count = message.getCount();
                        System.out.println(" shou dao ziji enter bing she zhi " + count);

                    }

                    if (message.getType() == model.Type.ENTER && message.getUsername().equals(friendName)) {
                        System.out.println("shou dao dui fang enter");
                        pcount = message.getPcount();
                        System.out.println(" shou dao duifang enter bing she zhi " + pcount);
                    }


                    System.out.println("shoudaoyigexioaxi");
                    taContent.setText(taContent.getText() + message + "\n");
                } catch (SocketException e) {

                } catch (EOFException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }


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