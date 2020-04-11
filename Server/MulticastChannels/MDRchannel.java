package MulticastChannels;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MDRchannel implements Runnable{

       private MulticastSocket socket;
       private int port;
       private InetAddress group;

       public MDRchannel(String ip, int port) {
              this.port = port;
              try {
                     this.group = InetAddress.getByName(ip);
              } catch (UnknownHostException e) {
                     System.out.println("Unknown Host:\n");
                     e.printStackTrace();
              }
       }

       @Override
       public void run() {
              // TODO Auto-generated method stub

       }

}