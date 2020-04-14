package Server.MulticastChannels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MDRchannel implements Runnable{

       private MulticastSocket socket;
       private int port;
       private InetAddress group;
       private int peerId;
       private volatile boolean receivedChunk = false;
       private volatile boolean readingChunks = false;
       private volatile byte[] restoredChunk = new byte[64000];
       private volatile String restoredChunkName;

       public byte[] getRestoredChunk() {
              return this.restoredChunk;
       }

       public void setRestoredChunk(byte[] restoredChunk) {
              this.restoredChunk = restoredChunk;
       }

       public String getRestoredChunkName() {
              return this.restoredChunkName;
       }

       public void setRestoredChunkName(String restoredChunkNumber) {
              this.restoredChunkName = restoredChunkNumber;
       }


       public boolean getReadingChunks() {
              return this.readingChunks;
       }

       public void setReadingChunks(boolean readingChunks) {
              this.readingChunks = readingChunks;
       }
       
       public boolean getReceivedChunk() {
              return this.receivedChunk;
       }

       public void setReceivedChunk(boolean recivedChunk) {
              this.receivedChunk = recivedChunk;
       }

       public MDRchannel(String ip, int port, int peetId) {
              this.port = port;
              try {
                     this.group = InetAddress.getByName(ip);
              } catch (UnknownHostException e) {
                     System.out.println("Unknown Host:\n");
                     e.printStackTrace();
              }
              //this.peerId = peerId;
       }

       @Override
       public void run() {
              try {
			socket = new MulticastSocket(port);
			socket.setTimeToLive(1);
                     socket.joinGroup(group);
                     byte[] buf = new byte[65000];
                     
                     while(true){
                            DatagramPacket recv = new DatagramPacket(buf, buf.length);       
                            socket.receive(recv);
                            byte msg[] = Arrays.copyOfRange(buf, 0, recv.getLength());
                            receivedMessage(msg);
                     }
                            
		} catch (IOException e) {
			System.out.println("Unable to create a socket");
              }
              

       }

       public void broadcast(byte[] msg){
              try (DatagramSocket serverSocket = new DatagramSocket()) {
                     DatagramPacket msgPacket = new DatagramPacket(msg, msg.length, group, port);
                     serverSocket.send(msgPacket);
                     System.out.println("MDR sent packet with msg: " + msg);
              } catch (IOException ex) {
                     ex.printStackTrace();
              }
       }


       private void receivedMessage(byte[] msg) {
              
              
              String tempSplit = "\r\n \r\n";
              byte[] split = tempSplit.getBytes();
              int sizesplit = 0;
              byte[] header = null;
              int indice = 0;
              // spliting header and body
              for (byte b : msg) {

                     if (split[0] == b) {
                            sizesplit = indice + (split.length - 1);
                            header = Arrays.copyOfRange(msg, 0, sizesplit);
                            break;
                     } else {
                            indice++;
                            continue;
                     }
              }
              String headerString = new String(header);

              String[] argsNew = headerString.split(" ");

              if (!argsNew[0].equals("1.0")) {
                     System.out.println("Message version not recognized");
                     return;
              }
              if (!argsNew[1].equals("CHUNK")) {
                     System.out.println("Message Type not recognized");
                     return;
              }
              if (Integer.parseInt(argsNew[2]) == peerId) {
                     return;
              }

              if(!getReceivedChunk()) setReceivedChunk(true);
              if(getReadingChunks()){
                     restoredChunk = Arrays.copyOfRange(msg, (sizesplit + 1), msg.length);
                     restoredChunkName = argsNew[3] + "." + argsNew[4];
              }
              
       }
}