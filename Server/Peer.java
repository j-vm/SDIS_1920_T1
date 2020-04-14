package Server;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import Server.MulticastChannels.*;

public class Peer implements BackupService {

    static final int MAX_CHUNK_SIZE = 64000; // Bytes

    private static int id;
    private static String version;
    private static int service_access_point;
    private static String ips[] = new String[3];
    private static int ports[] = new int[3];
    private static MCchannel controlChannel;
    private static MDBchannel backupChannel;
    private static MDRchannel restoreChannel;

    public static String hash256(String toHash) {
        String hashedString = null;
        try {
            hashedString = Hashing.toHexString(Hashing.getSHA(toHash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hashedString;

    }

    public int backup(String filePath, int replicationDegree) {

        byte[] buffer = new byte[MAX_CHUNK_SIZE]; // maximum size of chunk

        File ficheiro = new File(filePath);
        String fileName = ficheiro.getName();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(ficheiro);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        String fileId = String.format("%s_%s", fileName, ficheiro.lastModified());
        String fileIdName = String.format("%s", hash256(fileId));
        int chunkNumber = 1;

        try {
            while (fis.read(buffer) != -1) {
                byte[] header = String.format("%s PUTCHUNK %d %s %d %d \r\n \r\n", version, id, fileIdName, chunkNumber,
                        replicationDegree).getBytes();
                byte body[] = buffer;
                byte[] msg = new byte[header.length + body.length];
                System.arraycopy(header, 0, msg, 0, header.length);
                System.arraycopy(body, 0, msg, header.length, body.length);
                System.out.println(msg.length);
                backupChannel.broadcast(msg);
                String key = String.format("%s%d", fileIdName, chunkNumber);
                int waitTime = 0;
                while (controlChannel.chunksStored.get(key) == null
                        || controlChannel.chunksStored.get(key) < replicationDegree) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (waitTime > 500) {
                        System.out.println("Error backing up file in chunk number:" + chunkNumber);
                        return -1;
                    }
                    waitTime += 10;
                }
                chunkNumber++;
                // <Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg>
                // <CRLF><CRLF><Body>
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[FILE BACKEDUP] : " + filePath);
        return 0;
    }

    public int restore(String filePath) {

        File ficheiro = new File(filePath);
        String fileName = ficheiro.getName();
        String fileId = String.format("%s_%s", fileName, ficheiro.lastModified());

        String fileIdName = String.format("%s", hash256(fileId));

        int chunkNo = 1;
        byte[] header = null;
        byte[] chunkReceived = new byte[64000];
        FileOutputStream restoredFile = null;
        try {
            restoredFile = new FileOutputStream(filePath + "_1");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        restoreChannel.setReadingChunks(true);
        while(true) {
            System.out.println("ChunkNo. " +chunkNo);
            restoreChannel.setReceivedChunk(false);
            header = String.format("%s GETCHUNK %d %s %d\r\n \r\n", version, id, fileIdName, chunkNo).getBytes();
            controlChannel.broadcast(header);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!restoreChannel.getReceivedChunk()) {
                System.out.println("Error restoring chunk number: " + Integer.toString(chunkNo));
                break;
            }
            if (restoreChannel.getRestoredChunkName().equals(fileIdName + "." + chunkNo)) {
                chunkReceived = restoreChannel.getRestoredChunk();
                try {
                    restoredFile.write(chunkReceived);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(chunkReceived.length < MAX_CHUNK_SIZE) break;
            }
            chunkNo++;
        }
        restoreChannel.setReadingChunks(false);
        try {
            restoredFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[FILE RESTORED] : " + filePath + "_1" );

        // <Version> GETCHUNK <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>

        return 0;
    }

    public int delete(String filePath) {
        File ficheiro = new File(filePath);
        String fileName = ficheiro.getName();
        String fileId = String.format("%s_%s", fileName, ficheiro.lastModified());

        String fileIdName = String.format("%s",hash256(fileId));


        byte[] header = String
                    .format("%s DELETE %d %s \r\n \r\n", version, id, fileIdName)
                    .getBytes();

        controlChannel.broadcast(header);
        
        //<Version> DELETE <SenderId> <FileId> <CRLF><CRLF>

        return 0;
    }
    
    public int manage(int maximumDiskSpace) {
        //TODO: manage service
        return 1;
    }

    public void information() {
        //TODO: information service
    }
        
    public static void main(String args[]) {
        if(!parseArgs(args)) {
            System.out.println(
                    "Error parsing arguments.\nUsage: protocol_version peer_id service_access_point MC_name MDB_name MDR_name\n");
            System.exit(0);
        }

        try {
            Peer obj = new Peer();
            BackupService backupService = (BackupService) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(id);
            registry.bind("BackupService", backupService);

            System.out.println("Peer " + id + " [Connected to RMI]");
        } catch (Exception e) {
            System.out.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }

        Thread threadMC = new Thread(controlChannel);
        Thread threadMDB = new Thread(backupChannel);
        Thread threadMDR = new Thread(restoreChannel);

        File dir = new File("Peers/" + Integer.toString(id));
        dir.mkdirs();

        threadMC.start();
        System.out.println("Peer " + id + " [Connected to MC]");
        threadMDB.start();
        System.out.println("Peer " + id + " [Connected to MDB]");
        threadMDR.start();
        System.out.println("Peer " + id + " [Connected to MDR]");



    }

    private static boolean parseArgs(String args[]){
        if(args.length != 6) return false;
        if(!args[0].matches("[0-9].[0-9]")) return false;
        version = args[0];
        id = Integer.parseInt(args[1]);
        service_access_point = Integer.parseInt(args[2]);
        for (int i = 0; i < 3; i++) {
            String[] name = args[i+3].split(":");
            ips[i] = name[0];
            ports[i] = Integer.parseInt(name[1]);
        }
        restoreChannel = new MDRchannel(ips[2], ports[2], id);     
        controlChannel = new MCchannel(ips[0], ports[0], id, restoreChannel);
        backupChannel = new MDBchannel(ips[1], ports[1], id, controlChannel);
        
        return true;
    }

}