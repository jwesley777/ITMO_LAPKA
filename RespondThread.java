import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;

public class RespondThread extends Thread {

    protected DatagramSocket socket;
    protected BufferedReader in = null;
    DatagramPacket packet;
    byte[] buf;
    Prisoners<Human> collection;
    HashMap<Integer, Prisoners<Human>> collections;

    public RespondThread(DatagramPacket packet,  DatagramSocket socket) throws IOException {
        this.packet = packet;
        this.socket = socket;
    }

    public void run(){
        int portToRespond = 0;
        buf = packet.getData();
        String answer = "Empty answer";
        try {
            ObjectInputStream inp = new ObjectInputStream(new ByteArrayInputStream(buf));
            portToRespond = inp.readInt();

            String command = (String) inp.readObject();
            String login;
            String password;
            Human h;
            switch (command) {
                case "remove":
                    h = (Human)inp.readObject();
                    login = (String) inp.readObject();
                    password = (String) inp.readObject();
                    if (!(new DBController(new JDBCConnector()).passwordIsCorrect(login,password))) {
                        answer = "login/password incorrect";
                        break;
                    }
                    answer = new DBController(new JDBCConnector()).removeSomethingFromDB(h, login, password);
                    //answer = collection.remove(h);

                    break;
                case "help":
                    answer = "help";
                    break;
                case "show":
                    answer = new DBController(new JDBCConnector()).showDB();
                    break;
                case "add_if_max":
                    h = (Human)inp.readObject();
                    login = (String) inp.readObject();
                    password = (String) inp.readObject();
                    if (!(new DBController(new JDBCConnector()).passwordIsCorrect(login,password))) {
                        answer = "login/password incorrect";
                        break;
                    }
                    answer = new DBController(new JDBCConnector()).addIfMaxToDB(h, login, password);
                    break;
                case "remove_lower":
                    h = (Human)inp.readObject();
                    login = (String) inp.readObject();
                    password = (String) inp.readObject();
                    if (!(new DBController(new JDBCConnector()).passwordIsCorrect(login,password))) {
                        answer = "login/password incorrect";
                        break;
                    }
                    answer = new DBController(new JDBCConnector()).removeLowerFromDB(h, login, password);
                    break;
                //case "info":
                //    answer = collection.info();
                //    break;
                case "clear":
                    login = (String) inp.readObject();
                    password = (String) inp.readObject();
                    if (!(new DBController(new JDBCConnector()).passwordIsCorrect(login,password))) {
                        answer = "login/password incorrect";
                        break;
                    }
                    answer = new DBController(new JDBCConnector()).clearDB(login,password);
                    break;
                case "add":
                    h = (Human)inp.readObject();
                    login = (String) inp.readObject();
                    password = (String) inp.readObject();
                    if (!(new DBController(new JDBCConnector()).passwordIsCorrect(login,password))) {
                        answer = "login/password incorrect";
                        break;
                    }
                    answer = new DBController(new JDBCConnector()).addSomethingToDB(h, login, password);
                    break;
                case "register":
                    login = (String)inp.readObject();
                    String email = (String)inp.readObject();
                    answer = new DBController(new JDBCConnector()).addPasswordToDB(login, email);
                    break;
                default:
                    answer = "Wrong command";

            }
            // Answer
            buf = answer.getBytes();
            InetAddress address = packet.getAddress();
            int port = portToRespond;//packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);

        } catch (ClassCastException e) {
            answer = "Something wrong with command";
            buf = answer.getBytes();
            InetAddress address = packet.getAddress();
            int port = portToRespond;//packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            try {
                socket.send(packet);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            answer = "Something is wrong with command";
            buf = answer.getBytes();
            InetAddress address = packet.getAddress();
            int port = portToRespond;//packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            try {
                socket.send(packet);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getArgument(ObjectInputStream inp) throws IOException, ClassNotFoundException {
        String arg = "";
        boolean cont = true;
        while (cont) {
            try {
                Object obj = inp.readObject();
                if (obj != null) {
                    arg = arg + (String)obj;
                } else {
                    cont = false;
                }
            } catch (EOFException | StreamCorruptedException e) {
                // Vse idet po planu
                return arg;
            }
        }
        return arg;
    }


}
