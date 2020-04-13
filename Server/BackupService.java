package Server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackupService extends Remote{

       public int backup(String filePath, int replicationDegree) throws RemoteException;

       public int restore(String filePath) throws RemoteException;

       public int delete(String filePath) throws RemoteException;
       
       public int manage(int maximumDiskSpace) throws RemoteException;

       public void information() throws RemoteException;
}