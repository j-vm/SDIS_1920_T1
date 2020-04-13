package Client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import Server.BackupService;

public class TestApp {

    private static int peerId;
    
    
    public static void main(String args[]) {
        if(!parseArgs(args)) {
            System.out.println(
                    "Error parsing arguments.\nUsage: peerID SubProtocol operand1 operand2\n");
            System.exit(0);
        }
        try {
            Registry registry = LocateRegistry.getRegistry(peerId);
            BackupService backupService = (BackupService) registry.lookup("BackupService");
            
            if(args[1].equals("BACKUP")){
                if(args.length != 4){
                    System.out.println("Wrong number of arguments for 'BACKUP' Service!\n");
                }else{
                    backupService.backup(args[2],Integer.parseInt(args[3]));
                }
            }else if(args[1].equals("RESTORE")){
                if(args.length != 3){
                    System.out.println("Wrong number of arguments for 'RESTORE' Service!\n");
                }else{
                    backupService.restore(args[2]);
                }
            }else if(args[1].equals("DELETE")){
                if(args.length != 3){
                    System.out.println("Wrong number of arguments for 'DELETE' Service!\n");
                }else{
                    backupService.delete(args[2]);
                }
            }else if(args[1].equals("RECLAIM")){
                if(args.length != 3){
                    System.out.println("Wrong number of arguments for 'RECLAIM' Service!\n");
                }else{
                    backupService.manage(Integer.parseInt(args[2]));
                }
            }else if(args[1].equals("STATE")){
                if(args.length != 2){
                    System.out.println("Wrong number of arguments for 'STATE' Service!\n");
                }else{
                    backupService.information();
                }
            }else{
                System.out.println(args[1] + " is not a recognized Service!\n");
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find peer " + peerId);
        }
    }

    private static boolean parseArgs(String args[]){
        if(args.length < 2) return false;
        peerId = Integer.parseInt(args[0]);
        return true;
    }
}