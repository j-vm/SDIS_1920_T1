package Server.MulticastChannels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

public class MDRchannel implements Runnable{

       private MulticastSocket socket;
       private int port;
       private InetAddress group;
       private int peerId;
       private  MCchannel controlChannel;

       public MDRchannel(String ip, int port, int peetId, MCchannel controlChannel) {
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
                     
                     while(true){
                            DatagramPacket recv = new DatagramPacket(buf, buf.length);       
                            socket.receive(recv);
                            recv.getData();
                            //TODO: MANAGE CONTROL MESSAGES [3.1]
                            //parseRecived(buf);
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
                     System.out.println("MDR sent packet with msg: " + msg);
              } catch (IOException ex) {
                     ex.printStackTrace();
              }
       }

       //To Process CHUNK message
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
              body = Arrays.copyOfRange(msg, (sizesplit + 1), (msg.length - 1));
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

              Random rand = new Random();
              int tempo = rand.nextInt(400);
              try {
                     Thread.sleep(tempo);
              } catch (InterruptedException e) {
                     e.printStackTrace();
              }
              System.out.println("MDB KEY ====" + argsNew[3]+argsNew[4] + "===");
              System.out.println("VALUE ====" + controlChannel.chunksStored.get(argsNew[3]+argsNew[4]) + "===");
              if (controlChannel.chunksStored.get(argsNew[3]+argsNew[4]) == null) sendChunk(argsNew, body);
              else if (controlChannel.chunksStored.get(argsNew[3]+argsNew[4]) < Integer.parseInt(argsNew[5])) sendChunk(argsNew, body);
              
       }

       // Function to send CHUNK message after receiving PUTCHUNK
       private void sendChunk(String argsNew[], byte body[]){
              byte[] storedMsg = String
                    .format("%s CHUNK %d %s %s \r\n \r\n", argsNew[0], peerId, argsNew[3], argsNew[4])
                    .getBytes();
                     controlChannel.broadcast(storedMsg);
                     String nomeNovoFicheiro = (argsNew[3]+"."+argsNew[4]);
                     String path = "Peers/" + Integer.toString(peerId);
                     File novoFicheiro = new File(path,nomeNovoFicheiro);
                     try{
                            OutputStream outst = new FileOutputStream(novoFicheiro);
                            outst.write(body);
                            outst.close();

                     }catch(Exception e){
                            e.printStackTrace();
                     }
       }

}