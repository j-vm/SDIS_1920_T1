package Server.MulticastChannels;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.HashMap;
import java.util.Random;




public class MCchannel implements Runnable{
       private MulticastSocket socket;
       private int port;
       private InetAddress group;
       private int peerId;
       public volatile HashMap<String, Integer> chunksStored = new HashMap<String, Integer>();
       private MDRchannel restoreChannel;
       


       public MCchannel(String ip, int port, int peerId, MDRchannel restoreChannel) {
              this.port = port;
              try {
                     this.group = InetAddress.getByName(ip);
              } catch (UnknownHostException e) {
                     System.out.println("Unknown Host:\n");
                     e.printStackTrace();
              }
              this.peerId = peerId;
              this.restoreChannel = restoreChannel;
       }

       @Override
       public void run() {
              try {
			socket = new MulticastSocket(port);
			socket.setTimeToLive(1);
                     socket.joinGroup(group);
                     byte[] buf = new byte[100];
                     
                     while(true){
                            DatagramPacket recv = new DatagramPacket(buf, buf.length);       
                            socket.receive(recv);
                            byte msg[] = recv.getData();
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
                     System.out.println("MC sent packet with msg: " + msg);
              } catch (IOException ex) {
                     ex.printStackTrace();
              }
       }
       private void receivedMessage(byte[] msg) {

              String headerString = new String(msg);

              String[] argsNew = headerString.split(" ");

              if (!argsNew[0].equals("1.0")) {
                     System.out.println("Message version not recognized");
                     return;
              }
              if (Integer.parseInt(argsNew[2]) == peerId) {
                     return;
              }

              File folder = new File("Peers/" + Integer.toString(peerId));
              
              switch (argsNew[1]){
                     case "STORED":
                            String key = argsNew[3]+argsNew[4];
                            System.out.println("MC KEY ==="+key+ "===");
                            if(chunksStored.containsKey(key)) chunksStored.put(key, chunksStored.get(key) + 1);
                            else chunksStored.put(key,1);
                            break;             
                     
                     case "DELETE":
                            File[] savedChunks = folder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                   return name.startsWith(argsNew[3]);
                            }
                            });
                            int numSavedChunks = savedChunks.length;
                            for (int j = 0; j< numSavedChunks; j++){
                                   savedChunks[j].delete();
                            }
                            
                     case "GETCHUNK":
                            System.out.println("GETCHUNK: " + argsNew[3]+ " " + argsNew[4]);
                            File[] savedChunk = folder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                   return name.startsWith(argsNew[3] + "." + argsNew[4]);
                            }
                            });
                            if (savedChunk.length < 1) return; //Only procede if has chunk

                            restoreChannel.setReceivedChunk(false);
                            
                            Random rand = new Random();
                            int tempo = rand.nextInt(400);
                            try {
                                   Thread.sleep(tempo);
                            } catch (InterruptedException e) {
                                   e.printStackTrace();
                            }
                            if(!restoreChannel.getReceivedChunk()){
                                   byte[] header = String
                                          .format("%s CHUNK %s %s %s  \r\n \r\n", argsNew[0], peerId, argsNew[3], argsNew[4])
                                          .getBytes();
                                   byte body[] = new byte[64000];

                                   try {
                                          body = Files.readAllBytes(savedChunk[0].toPath());
                                   } catch (IOException e) {
                                          e.printStackTrace();
                                   }

                                   byte[] msg2 = new byte[header.length + body.length];
                                   System.arraycopy(header, 0, msg2, 0, header.length);
                                   System.arraycopy(body, 0, msg2, header.length, body.length);
                                   restoreChannel.broadcast(msg2);
                            }
                     default:
                            System.out.println("Unrecognized message type: " + argsNew[1]);
                            break;
              }
              
       }
}