import Chunk;
import java.io.*;
import java.net.*;

public class BackupFile{

       static final int MAX_CHUNK_SIZE = 64000; //Bytes
       Chunk chunks[];
       int replication_degree;

       public BackupFile(String FilePath, int replication_degree) {
              this.replication_degree = replication_degree;
              this.chunks = new Chunk[4];
       }

       public BackupFile() {
       }



       //TODO adapt function to class
       //Function to split a file into chunks
       public static void fileToChunks(File ficheiro) throws IOException{
              
              int chunkNumber = 1; //initial number for chunks
              
              
              byte [] buffer = new byte[MAX_CHUNK_SIZE]; // maximum size of chunk
              
              String fileName = ficheiro.getName();

              try(FileInputStream fis = new FileInputStream(ficheiro);
              BufferedInputStream bis = new BufferedInputStream(fis)){
                     
                     int bytesAmount = 0;
                     while ((bytesAmount = bis.read(buffer)) > 0) {
                            //write each chunk of data into separate file with different number in name
                            String filePartName = String.format("%s.%03d", fileName, chunkNumber++);
                            File newFile = new File(ficheiro.getParent(), filePartName);
                            try (FileOutputStream out = new FileOutputStream(newFile)) {
                                   out.write(buffer, 0, bytesAmount);
                            }
                     }
              }
       }


}