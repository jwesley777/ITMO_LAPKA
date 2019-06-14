import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class HumanClient {
    public HumanClient() {
        try {
            portGetSocket = new DatagramSocket();
            portGetSocket.setSoTimeout(5000);
            portGetSocket.send(new DatagramPacket(new byte[1], 1, InetAddress.getByName("localhost"), 4443));
            byte[] buf = new byte[2048];
            DatagramPacket packetGetSocket = new DatagramPacket(buf, buf.length);
            portGetSocket.receive(packetGetSocket);
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(buf));
            portToRespond = dataInputStream.readInt();

        } catch (SocketTimeoutException e) {
            System.out.println("Server is offline. Stopping the application...");
            System.exit(-1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    DatagramPacket packet;
    byte[] buf = new byte[20048];
    protected int portToRespond;
    DatagramSocket portGetSocket;

    public static void main (String[] args) {
        HumanClient client = new HumanClient();
        DatagramPacket packet;
        byte[] buf;


        if (args.length != 1) {
            System.out.println("Client must provide a servername");
            return;
        }
        Scanner in = new Scanner(System.in);
        Prisoners.printGuideToStdOut();
        String input = in.nextLine();
        String firstWord = input.split(" ")[0];
        try {
            InetAddress address = InetAddress.getByName(args[0]);
            DatagramChannel channel = DatagramChannel.open();
            ByteBuffer byteBuffer;
            ByteArrayOutputStream bufStream;
            ObjectOutputStream obj;
            channel.socket().setSoTimeout(5000);


            channel.socket().bind(new InetSocketAddress(client.portToRespond));
            channel.socket().setSoTimeout(5000);

            while (!firstWord.equals("stop")) {

                try {

                    Interrupter ir1 = new Interrupter(Thread.currentThread());
                    Thread th1 = new Thread(ir1);
                    th1.start();
                    // We need this so that later the wait variable
                    // can be passed in successfully
                    while (th1.getState() != Thread.State.WAITING) ;

                    ir1.wait = 5000;

                    bufStream = new ByteArrayOutputStream();
                    obj = new ObjectOutputStream(bufStream);


                    obj.writeInt(client.portToRespond);

                    JSONObject arg = Prisoners.getArgument(input);

                    try {
                        if (firstWord.equals("show") || firstWord.equals("info")
                                || firstWord.equals("help")) {
                            if (firstWord.equals("help")) {
                                Prisoners.printGuideToStdOut();
                            }
                            obj.writeObject(firstWord);

                            byteBuffer = ByteBuffer.wrap(bufStream.toByteArray());
                            channel.send(byteBuffer, new InetSocketAddress(address, 4444));
                        } else if (firstWord.equals("add") ||firstWord.equals("add_if_max") ||
                                firstWord.equals("remove") ||firstWord.equals("remove_lower")){
                            obj.writeObject(firstWord);
                            if (arg != null) {
                                Human h = Prisoners.jsonToHuman(arg);
                                String login = input.split(" ")[1];
                                String password = input.split(" ")[2];
                                if (h!=null) {
                                    obj.writeObject(h);
                                    obj.writeObject(login);
                                    obj.writeObject(password);
                                } else {
                                    System.out.println("Wrong argument");
                                    input = in.nextLine();
                                    firstWord = input.split(" ")[0];
                                    continue;
                                }
                            } else {
                                System.out.println("Wrong argument");
                                input = in.nextLine();
                                firstWord = input.split(" ")[0];
                                continue;
                            }

                            byteBuffer = ByteBuffer.wrap(bufStream.toByteArray());
                            channel.send(byteBuffer, new InetSocketAddress(address, 4444));
                        } else if (firstWord.equals("register") || firstWord.equals("clear")) {
                            obj.writeObject(firstWord);
                            String login = input.split(" ")[1];
                            String email = input.split(" ")[2];
                            obj.writeObject(login);
                            obj.writeObject(email);

                            byteBuffer = ByteBuffer.wrap(bufStream.toByteArray());
                            channel.send(byteBuffer, new InetSocketAddress(address, 4444));
                        }
                        else {
                            System.out.println("Unknown command");
                            input = in.nextLine();
                            firstWord = input.split(" ")[0];
                            continue;
                        }
                    } catch (ClosedByInterruptException e) {
                        System.out.println("Connection is lost..");
                        input = in.nextLine();
                        firstWord = input.split(" ")[0];
                        continue;
                    }
                    // Answer
                    ByteBuffer answerByteBuffer = ByteBuffer.allocate(20048);
                    channel.socket().setSoTimeout(5000);
                    try {
                        ir1.wait = 5000;
                        th1.interrupt();
                        channel.receive(answerByteBuffer);
                    } catch (ClosedByInterruptException e) {
                        System.out.println("Connection is lost...");
                        Thread.interrupted();
                        while (th1.getState()!=Thread.State.WAITING);

                        //input = in.nextLine();
                        //firstWord = input.split(" ")[0];
                        //channel.close();
                        //continue;
                    }
                    ir1.wait=-1;
                    th1.interrupt();
                    while(th1.getState()!=Thread.State.WAITING);
                    Thread.interrupted();
                    channel.close();
                    channel = DatagramChannel.open();
                    channel.socket().bind(new InetSocketAddress(client.portToRespond));
                    if (new String(answerByteBuffer.array()).length()>1)
                        System.out.println("Server: " + new String(answerByteBuffer.array()));

                    input = in.nextLine();
                    firstWord = input.split(" ")[0];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Wrong use of command or command does not exist");
                    input = in.nextLine();
                    firstWord = input.split(" ")[0];
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Connection is lost...");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    static private class Interrupter implements Runnable
    {
        private final Thread th1;
        private volatile int wait=-1;
        private Interrupter(Thread ith1)
        {
            th1=ith1;
        }
        public void run()
        {
            while(true)
            {
                try{
                    if( wait<0 ){ th1.join(); break; }
                    else{ Thread.sleep(wait); th1.interrupt(); wait=-1; }
                } catch(Exception e){}
            }
        }
    }
}

