import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MCchannel implements Runnable{
       private MulticastSocket socket;
       private static int port;
       private InetAddress group;


       public MCchannel(String ip, int port) {
              this.port = port;
              this.group = InetAddress.getByName(ip);
              try {
			socket = new MulticastSocket(port);
			socket.setTimeToLive(1);
			socket.joinGroup(group);
		} catch (IOException e) {
			System.out.println("Unable to create a socket");
              }
              byte[] buf = new byte[6400];
              DatagramPacket recv = new DatagramPacket(buf, buf.length);
              while(true){
                     socket.receive(recv);
                     recv.getData();
                     //parseRecived(buf);
              }
                     

       }

       @Override
       public void run() {
              // TODO Auto-generated method stub

       }

}