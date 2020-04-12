
//import Chunk.java;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import Server.Hashing;
import javax.naming.directory.BasicAttributes;

import java.util.Arrays;
import java.util.List;

public class BackupFile{

       static final int MAX_CHUNK_SIZE = 64000; //Bytes
       Chunk chunks[];
       int replication_degree;

       public BackupFile(String filePath, int replication_degree) {
              this.replication_degree = replication_degree;
              this.chunks = new Chunk[4];

              List<File> chunkFiles;

              //Divide the file into Chunks and get the total number of Chunks
              int numChunks = filetoChunks(filePath,chunkFiles);

              //create a loop to transfer the files

              for(int i = 0; i < numChunks; i++){
                     
              }







       }

       public BackupFile() {
       }



       //TODO adapt function to class
       /**Function to split a file into chunks
        * @param ficheiro the file that will be divided into chunks
        * @param filePath path of the file that will be divided.  
        */
       public static int fileToChunks(String filePath, List<File> chunkFiles) throws IOException{
              
              int chunkNumber = 1; //initial number for chunks
              
              Path p1 = Paths.get(filePath);

              byte [] buffer = new byte[MAX_CHUNK_SIZE]; // maximum size of chunk

              File ficheiro = new File(filePath);

              
              BasicFileAttributes attrs = Files.readAttributes(p1, BasicAttributes.class); //get metadata from file
              
              String fileName = ficheiro.getName();

              try(FileInputStream fis = new FileInputStream(ficheiro);
              BufferedInputStream bis = new BufferedInputStream(fis)){
                     
                     int bytesAmount = 0;
                     while ((bytesAmount = bis.read(buffer)) > 0) {
                            //write each chunk of data into separate file with different number in name
                            String fileId = String.format("%s_%s", fileName,attrs.lastModifiedTime());
                            String fileIdName = String.format("%s.%03d",toHexString(getSHA(fileId)),chunkNumber++);
                            File newFile = new File(ficheiro.getParent(), fileIdName);
                            try (FileOutputStream out = new FileOutputStream(newFile)) {
                                   out.write(buffer, 0, bytesAmount);
                            }
                            chunkFiles.add(newFile);
                     }
              }
              return chunkNumber;
       }


       
       
       /**Function to merge chunks into a file
        * @param chunksToMerge list of chunks to merge into a file
        * @param novo The file to which the chunks will merge into  
        */
       public static void mergeChunks(List<File> chunksToMerge, File novo) throws IOException{
              try (FileOutputStream fos = new FileOutputStream(novo);
                     BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
                     for (File f : chunksToMerge) {
                            Files.copy(f.toPath(), mergingStream);
                     }
              }      

       }

       public static List<File> listOfFilesToMerge(File oneOfFiles) {
              String tmpName = oneOfFiles.getName();//{name}.{number}
              String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));//remove .{number}
              File[] files = oneOfFiles.getParentFile().listFiles(
                      (File dir, String name) -> name.matches(destFileName + "[.]\\d+"));
              Arrays.sort(files);//ensuring order 001, 002, ..., 010, ...
              return Arrays.asList(files);
       }


       
       
       public static void mergeFiles(File oneOfFiles, File into)
          throws IOException {
              mergeFiles(listOfFilesToMerge(oneOfFiles), into);
              }


       public static List<File> listOfFilesToMerge(String oneOfFiles) {
                     return listOfFilesToMerge(new File(oneOfFiles));
       }
                 
       public static void mergeFiles(String oneOfFiles, String into) throws IOException{
                     mergeFiles(new File(oneOfFiles), new File(into));
       }


       

}