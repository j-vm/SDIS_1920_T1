import java.io.*;
import java.net.*;

public class TestApp {

    public static void main(String[] args) throws IOException {

        //Checks if there are too many arguments
        if(args.length > 4 ){
	        System.out.println("Too many arguments it should be: $ java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>\n");
            System.exit(1);
        }

        //If no arguments, acts as a peer, else it acts as a client
        if(args.length == 0){
            peer();
            System.exit(0);
        }else{
            client(args);
            fileToChunks(new File("C:\\Users\\andre\\Desktop\\IMG_2767.jpg"));
            System.exit(0);
        }


    }


    public static void peer(){

        try{
            InetAddress grupo = InetAddress.getByName("225.4.5.6");
            MulticastSocket multiSocket = new MulticastSocket(3456);
            multiSocket.joinGroup(grupo);

            byte [] buffer = new byte[100];
            DatagramPacket  packet = new DatagramPacket (buffer, buffer.length);

            multiSocket.receive(packet);

            System.out.println(new String(buffer));

            multiSocket.close();


        }
        catch(Exception e){e.printStackTrace();}

    }


    public static void client(String[] args){


        try{
            InetAddress grupo = InetAddress.getByName("225.4.5.6");
            MulticastSocket multiSocket = new MulticastSocket();
            String mensagem = "";
        
        
            //Checks if the second argument is a valid one
            if(!(args[1].equals("BACKUP")) && !(args[1].equals("RESTORE")) && !(args[1].equals("DELETE")) && !(args[1].equals("RECLAIM")) && !(args[1].equals("STATE"))) {
                mensagem  = "The second Argument should be 'BACKUP', 'RESTORE', 'DELETE', 'RECLAIM' or 'STATE'.\n";
            }

            if (args[1].equals("BACKUP")){
                mensagem = "Start BACKUP - this is a placeholder\n";
            }

            if (args[1].equals("RESTORE")){
                mensagem ="Start RESTORE - this is a placeholder\n";
            }

            if (args[1].equals("DELETE")){
                mensagem ="Start DELETE - this is a placeholder\n";
            }

            if (args[1].equals("RECLAIM")){
                mensagem ="Start RECLAIM - this is a placeholder\n";
            }

            if (args[1].equals("STATE")){
                mensagem ="Start STATE - this is a placeholder\n";
            }

            DatagramPacket  packet = new DatagramPacket (mensagem.getBytes(), mensagem.length(), grupo, 3456);
            multiSocket.send(packet);
            multiSocket.close();
        }
        catch(Exception e){e.printStackTrace();}
    }

}