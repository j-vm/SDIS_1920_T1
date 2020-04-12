import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private static int peerId;
    public static void main(String[] args) {
        if(!parseArgs(args)) {
            System.out.println(
                    "Error parsing arguments.\nUsage: peerID\n");
            System.exit(0);
        }
        try {
            Registry registry = LocateRegistry.getRegistry(peerId);
            BackupService backupService = (BackupService) registry.lookup("BackupService");
            int response = backupService.backup("test", 2);
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static boolean parseArgs(String args[]){
        if(args.length != 1) return false;
        peerId = Integer.parseInt(args[0]);
        return true;
    }
}