package MulticastChannels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Random;

public class MCchannel implements Runnable{
       private MulticastSocket socket;
       private int port;
       private InetAddress group;
       private int peerId;


       public MCchannel(String ip, int port, int peerId) {
              this.port = port;
              try {
                     this.group = InetAddress.getByName(ip);
              } catch (UnknownHostException e) {
                     System.out.println("Unknown Host:\n");
                     e.printStackTrace();
              }
              this.peerId = peerId;
       }

       @Override
       public void run() {
              try {
			socket = new MulticastSocket(port);
			socket.setTimeToLive(1);
			socket.joinGroup(group);
		} catch (IOException e) {
			System.out.println("Unable to create a socket");
              }
              byte[] buf = new byte[6400];
              DatagramPacket recv = new DatagramPacket(buf, buf.length);

                     byte[] buffer = new byte[1000];
                     String outString = "HI! I am Peer n" + peerId;
                     buffer = outString.getBytes();
                     
                     DatagramPacket out = new DatagramPacket(buffer, buffer.length);
                     
                     try {
                            socket.send(out);
                     } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                     }
                   

              while(true){
                     try {
                            socket.receive(recv);
                     } catch (IOException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                     }
                     recv.getData();
                     //parseRecived(buf);
                     System.out.println(buf);
              }
                     

       }

}