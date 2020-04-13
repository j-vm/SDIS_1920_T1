package MulticastChannels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class MDBchannel implements Runnable{

       private MulticastSocket socket;
       private int port;
       private InetAddress group;
       private int peerId;

       public MDBchannel(String ip, int port) {
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
                            byte msg[] = recv.getData();
                            recivedMessage(msg);
                            System.out.println(buf);
                     }
                            
		} catch (IOException e) {
			System.out.println("Unable to create a socket");
              }
              

       }

       public void broadcast(byte[] msg){
              try (DatagramSocket serverSocket = new DatagramSocket()) {
                     DatagramPacket msgPacket = new DatagramPacket(msg, msg.length, group, port);
                     serverSocket.send(msgPacket);
                     System.out.println("Server sent packet with msg: " + msg);
              } catch (IOException ex) {
                     ex.printStackTrace();
              }
       }


       private void recivedMessage(byte[] msg){
              String tempSplit = "\r\n \r\n";
              byte[] split = tempSplit.getBytes();
              byte[] header;
              byte[] body;
              int match = 0;
              //spliting header and body
              for (byte b : msg) {
                     if(split[match] == b) match++;
                     else{
                            match = 0;
                            //TODO: push to header  
                     }
                     if(match == tempSplit.length()){
                            //TODO: push rest to body
                            break;
                     }
              }
       }
}