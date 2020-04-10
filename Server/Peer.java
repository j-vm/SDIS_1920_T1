import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;

        
public class Peer implements BackupService {
    
    private static int id; 

    public int backup(String filePath, int replicationDegree) {

        
        return 1;
    }

    public int restore(String filePath) {
        //TODO: restore service
        return 1;
    }

    public int delete(String filePath) {
        //TODO: delete service
        return 1;
    }
    
    public int manage(int maximumDiskSpace) {
        //TODO: manage service
        return 1;
    }

    public void information() {
        //TODO: information service
    }
        
    public static void main(String args[]) {
         



        try {
            Peer obj = new Peer();
            BackupService backupService = (BackupService) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("BackupService", backupService);

            System.err.println("Peer ready");
        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }
    }


    private static boolean parseArgs(String[] args){
        if(args.length != 6) return false;
        if(!args[0].matches("[0-9].[0-9]")) return false;
    }
}