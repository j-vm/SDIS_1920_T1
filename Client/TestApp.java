import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import Server.BackupService;

public class TestApp {

    private static int peerId;
    
    
    public static void main(String args[]) {
        if(!parseArgs(args)) {
            System.out.println(
                    "Error parsing arguments.\nUsage: peerID\n");
            System.exit(0);
        }
        try {
            Registry registry = LocateRegistry.getRegistry(peerId);
            BackupService backupService = (BackupService) registry.lookup("BackupService");
            
            if(args[2] == "BACKUP"){
                if(args.length != 4){
                    System.out.println("Wrong number of arguments for 'BACKUP' Service!\n");
                }else{
                    backupService.backup(args[3],Integer.parseInt(args[4]));
                }
            }else if(args[2] == "RESTORE"){
                if(args.length != 3){
                    System.out.println("Wrong number of arguments for 'RESTORE' Service!\n");
                }else{
                    backupService.restore(args[3]);
                }
            }else if(args[2] == "DELETE"){
                if(args.length != 3){
                    System.out.println("Wrong number of arguments for 'DELETE' Service!\n");
                }else{
                    backupService.delete(args[3]);
                }
            }else if(args[2] == "RECLAIM"){
                if(args.length != 3){
                    System.out.println("Wrong number of arguments for 'RECLAIM' Service!\n");
                }else{
                    backupService.manage(Integer.parseInt(args[3]));
                }
            }else if(args[2] == "STATE"){
                if(args.length != 2){
                    System.out.println("Wrong number of arguments for 'STATE' Service!\n");
                }else{
                    backupService.information();
                }
            }else{
                System.out.println( args[2] + " is not a recognized Service!\n");
            }


        } catch (Exception e) {
            System.out.println("Could not find peer " + peerId);
        }
    }

    private static boolean parseArgs(String args[]){
        if(args.length != 1) return false;
        peerId = Integer.parseInt(args[0]);
        return true;
    }
}