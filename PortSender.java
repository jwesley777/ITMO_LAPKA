import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PortSender extends Thread {
    DatagramSocket socket;
    DatagramPacket packet;
    int counter = 1;
    byte[] buf;
    public PortSender() throws SocketException {
        socket = new DatagramSocket(4443);
        counter = (int)(Math.random()*100);
    }
    public void run() {
        while (true) {
            buf = new byte[2048];
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                InetAddress addr = packet.getAddress();
                int port = packet.getPort();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream dataStream = new DataOutputStream(byteStream);
                dataStream.writeInt(4444+counter++);
                buf = byteStream.toByteArray();
                packet = new DatagramPacket(buf, buf.length, addr, port);
                socket.send(packet);

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

}
