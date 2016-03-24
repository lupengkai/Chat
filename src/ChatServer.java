import model.*;
import service.UserService;

import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {
    boolean started = false;
    ServerSocket ss = null;
    volatile List<Client> clients = new ArrayList<Client>();
    volatile List<String> usernames = new ArrayList<>();


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
        int count = 0;
        int pCount = 0;
        private String username;
        private Socket s = null;
        private ObjectInput ois = null;
        private ObjectOutputStream oos = null;
        private boolean bConnect = false;
        private String friendName;
        private boolean login = false;


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


        public synchronized void run() {
            while (login == false) {
                try {
                    Object o = ois.readObject();
                    Message message = (Message) o;
                    System.out.println("read");

                    if (message.getType() == Type.LOG) {
                        System.out.println("check log");

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
                                login = true;


                                oos.writeObject(ack);
                                oos.flush();
                            } catch (Exception e) {
                                ack.setPermit(false);
                                ack.setMsg("Error password or error username.");
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
                        if (message.isSingle() == false) {


                            for (int i = 0; i < clients.size(); i++) {
                                Client c = clients.get(i);
                                if (c.username == message.getUsername()) {
                                    count++;
                                }

                            }


                            username = message.getUsername();
                            clients.add(this);
                            bConnect = true;
                            login = true;

                            usernames.add(message.getUsername());


                            message.setUsername("System");
                            message.setMessage(username + " Enter! Welcome!");
                            String[] uo = new String[usernames.size()];
                            for (int i = 0; i < usernames.size(); i++) {
                                uo[i] = usernames.get(i);
                            }

                            message.setUserOnline((uo));


                            for (int i = 0; i < clients.size(); i++) {

                                Client c = clients.get(i);
                                if (c.count == 0) {
                                    message.setDate(new Date());


                                    System.out.println(message.getUserOnline() + " " + c.username);
                                    c.oos.writeObject(message);
                                    c.oos.flush();
                                    System.out.println(message.getUserOnline() + " " + c.username);


                                }

                            }
                        } else {
                            login = true;
                            System.out.println("single connect");
                            username = message.getUsername();
                            bConnect = true;
                            PrivateMessage pm = (PrivateMessage) message;
                            pCount = pm.getPcount();

                            friendName = pm.getDestinationName();


                            for (int i = 0; i < clients.size(); i++) {
                                Client c = clients.get(i);
                                if (c.username.equals(username)) {
                                    count++;
                                }
                            }
                            pm.setCount(count);
                            System.out.println(count);


                            clients.add(this);


                            for (int i = 0; i < clients.size(); i++) {
                                Client c = clients.get(i);
                                if (c.count == pm.getPcount() && c.username.equals(pm.getDestinationName())) {
                                    System.out.println("Destination" + c.username + "  " + c.count + pm.getMeet());
                                    message.setDate(new Date());
                                    c.send(message);
                                    System.out.println(message.getMessage());
                                    if (pm.getMeet() == Meet.HELLO) {
                                        c.pCount = pm.getCount();
                                    }

                                    send(message);
                                    oos.flush();
                                    System.out.println(message + "enter");
                                }
                            }


                        }
                    }


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try {


                while (bConnect) {


                    Object o = ois.readObject();
                    Message message = (Message) o;


                    if (message.isSingle() == false) {


                        System.out.println(message.getMessage());
                        System.out.println(message.getUsername());

                        for (int i = 0; i < clients.size(); i++) {
                            Client c = clients.get(i);
                            if (c.count == 0) {
                                message.setDate(new Date());
                                c.send(message);
                                System.out.println(message.getUserOnline());
                            }
                        }
                    } else {
                        PrivateMessage pm = (PrivateMessage) message;
                        System.out.println(pm.getCount() + "-->" + pm.getPcount());


                        for (int i = 0; i < clients.size(); i++) {
                            Client c = clients.get(i);
                            if (c.count == pm.getPcount() && c.username.equals(pm.getDestinationName()) ||
                                    c.count == pm.getCount() && c.username.equals(pm.getUsername())) {
                                message.setDate(new Date());
                                c.send(message);
                                oos.flush();
                                System.out.println("twice Pcount" + pCount);
                            }
                        }


                    }
                }
            } catch (EOFException e) {
                System.out.println("Client Closed");
                bConnect = false;
                clients.remove(this);


                if (count == 0) {
                    usernames.remove(this.username);


                    Message message = new Message();
                    message.setType(Type.EXIT);
                    String[] uo = new String[usernames.size()];
                    for (int i = 0; i < usernames.size(); i++) {
                        uo[i] = usernames.get(i);
                    }

                    message.setUserOnline((uo));
                    message.setUsername("System");
                    message.setMessage(username + "  Exit. Bye!");

                    for (int i = 0; i < clients.size(); i++) {
                        Client c = clients.get(i);
                        if (c.count == 0) {
                            message.setDate(new Date());
                            c.send(message);
                            try {
                                oos.flush();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }


                    System.out.println(clients.size());
                    System.out.println(usernames.size());
                } else {
                    PrivateMessage pm = new PrivateMessage();
                    pm.setPcount(pCount);
                    pm.setUsername(username);
                    pm.setDestinationName(friendName);
                    pm.setMessage("Bye " + friendName);
                    pm.setType(Type.EXIT);
                    pm.setSingle(true);
                    pm.setDate(new Date());


                    for (int i = 0; i < clients.size(); i++) {
                        Client c = clients.get(i);
                        System.out.println("123 single de pcount" + pCount);
                        if (c.count == pm.getPcount() && c.username.equals(pm.getDestinationName())) {
                            pm.setDate(new Date());
                            c.send(pm);
                            System.out.println("twice");
                        }
                    }
                }
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



