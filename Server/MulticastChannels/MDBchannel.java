package Server.MulticastChannels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

public class MDBchannel implements Runnable {

       private MulticastSocket socket;
       private int port;
       private InetAddress group;
       private int peerId;
       private MCchannel controlChannel;

       public MDBchannel(String ip, int port, int peerId, MCchannel controlChannel) {
              this.port = port;
              try {
                     this.group = InetAddress.getByName(ip);
              } catch (UnknownHostException e) {
                     System.out.println("Unknown Host:\n");
                     e.printStackTrace();
              }
              this.peerId = peerId;
              this.controlChannel = controlChannel;
       }

       @Override
       public void run() {
              try {
                     socket = new MulticastSocket(port);
                     socket.setTimeToLive(1);
                     socket.joinGroup(group);
                     byte[] buf = new byte[65000];

                     while (true) {
                            DatagramPacket recv = new DatagramPacket(buf, buf.length);
                            socket.receive(recv);
                            byte msg[] = Arrays.copyOfRange(buf, 0, recv.getLength());
                            receivedMessage(msg);
                     }

              } catch (IOException e) {
                     System.out.println("Unable to create a socket");
              }

       }

       public void broadcast(byte[] msg) {
              try (DatagramSocket serverSocket = new DatagramSocket()) {
                     DatagramPacket msgPacket = new DatagramPacket(msg, msg.length, group, port);
                     serverSocket.send(msgPacket);
                     //System.out.println("MDB sent packet with msg: " + msg);
              } catch (IOException ex) {
                     ex.printStackTrace();
              }
       }

       private void receivedMessage(byte[] msg) {
              String tempSplit = "\r\n \r\n";
              byte[] split = tempSplit.getBytes();
              int sizesplit = 0;
              byte[] header = null;
              byte[] body = new byte[64000];
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
              body = Arrays.copyOfRange(msg, (sizesplit + 1), msg.length);
              String headerString = new String(header);

              String[] argsNew = headerString.split(" ");

              if (!argsNew[0].equals("1.0")) {
                     System.out.println("Message version not recognized");
                     return;
              }
              if (!argsNew[1].equals("PUTCHUNK")) {
                     System.out.println("Message Type not recognized");
                     return;
              }
              if (Integer.parseInt(argsNew[2]) == peerId) {
                     return;
              }
              controlChannel.chunksStored.put(argsNew[3]+argsNew[4],0);
              Random rand = new Random();
              int tempo = rand.nextInt(400);
              try {
                     Thread.sleep(tempo);
              } catch (InterruptedException e) {
                     e.printStackTrace();
              }
              if (controlChannel.chunksStored.get(argsNew[3]+argsNew[4]) == null) sendChunk(argsNew, body);
              else if (controlChannel.chunksStored.get(argsNew[3]+argsNew[4]) < Integer.parseInt(argsNew[5])) sendChunk(argsNew, body);
              
       }

       private void sendChunk(String argsNew[], byte body[]){
              byte[] storedMsg = String
                    .format("%s STORED %d %s %s \r\n \r\n", argsNew[0], peerId, argsNew[3], argsNew[4])
                    .getBytes();
                     controlChannel.broadcast(storedMsg);
                     String nomeNovoFicheiro = (argsNew[3]+"."+argsNew[4]);
                     String path = "Peers/" + Integer.toString(peerId);
                     File novoFicheiro = new File(path,nomeNovoFicheiro);
                     try{
                            OutputStream outst = new FileOutputStream(novoFicheiro);
                            outst.write(body);
                            outst.close();
                            System.out.println("Stored chunk number: " + argsNew[4]);
                     }catch(Exception e){
                            e.printStackTrace();
                     }
       }
}