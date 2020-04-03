import java.io.*;
import java.net.*;

public class TestApp {

    public static void main(String[] args) {

        //Checks if there are too many arguments
        if(args.length > 4 ){
	        System.out.println("Too many arguments it should be: $ java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>\n");
            System.exit(1);
        }

        //Checks if the second argument is a valid one
        if(args[1] != "BACKUP" && args[1] != "RESTORE" && args[1] != "DELETE" && args[1] != "RECLAIM" && args[1] != "STATE") {
            System.out.println("The second Argument should be 'BACKUP', 'RESTORE', 'DELETE', 'RECLAIM' or 'STATE'.\n");
            System.exit(1);
        }

        if (args[1]== "BACKUP"){
            System.out.println("Start BACKUP - this is a placeholder\n");
        }

        if (args[1]== "RESTORE"){
            System.out.println("Start RESTORE - this is a placeholder\n");
        }

        if (args[1]== "DELETE"){
            System.out.println("Start DELETE - this is a placeholder\n");
        }

        if (args[1]== "RECLAIM"){
            System.out.println("Start RECLAIM - this is a placeholder\n");
        }

        if (args[1]== "STATE"){
            System.out.println("Start STATE - this is a placeholder\n");
        }


    }
}