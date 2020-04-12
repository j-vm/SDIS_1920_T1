package MulticastChannels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


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
                     byte[] buf = new byte[6400];
                     
                     while(true){
                            DatagramPacket recv = new DatagramPacket(buf, buf.length);       
                            socket.receive(recv);
                            recv.getData();
                            //parseRecived(buf);
                            System.out.println(buf);
                     }
                            
		} catch (IOException e) {
			System.out.println("Unable to create a socket");
              }
              

       }

       public void broadcast(){
              try (DatagramSocket serverSocket = new DatagramSocket()) {
                     for (int i = 0; i < 5; i++) {
                         String msg = "Sent message no " + i;
          
                         // Create a packet that will contain the data
                         // (in the form of bytes) and send it.
                         DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                                 msg.getBytes().length, group, port);
                         serverSocket.send(msgPacket);
               
                         System.out.println("Server sent packet with msg: " + msg);
                     }
                 } catch (IOException ex) {
                     ex.printStackTrace();
                 }
       }
}