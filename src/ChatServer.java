import model.ACK;
import model.Message;
import model.Type;
import service.UserService;

import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {
    boolean started = false;
    ServerSocket ss = null;
    List<Client> clients = new ArrayList<Client>();
    List<String> usernames = new ArrayList<>();


    public static void main(String[] args) {
        new ChatServer().start();
    }

    public void start() {
        try {
            ss = new ServerSocket(6666);
            started = true;
        } catch (BindException e) {
           e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {


            while (started) {
                Socket s = ss.accept();
                Client c = new Client(s);
                new Thread(c).start();


                System.out.println("connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Client implements Runnable {
        private String username;
        private Socket s = null;
        private ObjectInput ois = null;
        private ObjectOutputStream oos = null;
        private boolean bConnect = false;


        public Client(Socket s) {

            this.s = s;

            try {
                ois = new ObjectInputStream(s.getInputStream());
                oos = new ObjectOutputStream(s.getOutputStream());
                bConnect = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void send(Message message) {
            try {
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void run() {

            try {
                Object o = ois.readObject();
                Message message = (Message) o;
                if (message.getType() == Type.LOG) {

                    ACK ack = new ACK();

                    if (usernames.contains(message.getUsername())) {
                        ack.setPermit(false);
                        ack.setMsg("RepeatLog");
                        oos.writeObject(ack);
                    } else {
                        try {
                            UserService.getInstance().validate(message.getUsername(), message.getPassword());

                            ack.setPermit(true);

                            username = message.getUsername();


                            oos.writeObject(ack);
                            oos.flush();
                        } catch (Exception e) {
                            ack.setPermit(false);
                        }
                        oos.writeObject(ack);
                        oos.flush();
                    }
                } else if (message.getType() == Type.REGISTER) {


                    ACK ack = new ACK();


                    if (UserService.getInstance().loadByName(message.getUsername()) != null) {
                        ack.setPermit(true);
                        clients.add(this);
                        System.out.println(usernames);
                    } else {
                        ack.setPermit(false);
                    }
                    oos.writeObject(ack);
                    oos.flush();

                } else if (message.getType() == Type.ENTER) {
                    username = message.getUsername();
                    clients.add(this);
                    bConnect = true;

                    usernames.add(message.getUsername());
                    message.setUsername("System");
                    message.setMessage(username + " Enter! Welcome!");
                    for (int i = 0; i < clients.size(); i++) {
                        Client c = clients.get(i);
                        message.setDate(new Date());
                        c.send(message);
                        oos.flush();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }






            try {



                while (bConnect) {

                    Object o = ois.readObject();
                    Message message = (Message) o;


                        System.out.println(message.getMessage());
                        System.out.println(message.getUsername());

                        for (int i = 0; i < clients.size(); i++) {
                            Client c = clients.get(i);
                            message.setDate(new Date());
                            c.send(message);
                            oos.flush();
                        }
                    }
                }
             catch (EOFException e) {
                System.out.println("Client Closed");
                bConnect = false;
                clients.remove(this);
                usernames.remove(this.username);
                System.out.println(clients.size());
                System.out.println(usernames.size());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) ois.close();
                    if (oos != null) oos.close();
                    if (s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }

    }
}



