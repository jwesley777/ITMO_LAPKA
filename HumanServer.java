import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class HumanServer extends Thread {
    DatagramSocket socket = new DatagramSocket(4444);
    byte[] buf = new byte[20048];
    public HumanServer() throws IOException {
        new PortSender().start();
    }

    public void run(){
        DatagramPacket packet;
        while (true) {
            packet = new DatagramPacket(buf, buf.length);
            try {

                socket.receive(packet);
                new RespondThread(packet,socket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        try {
            new HumanServer().run();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
