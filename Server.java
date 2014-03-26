
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


//Continuously Running Server
public class Server extends Thread {

    private static int port; //edit: take args
    String[] connecterUsers;
    static ArrayList<AcceptClient> threads = new ArrayList<AcceptClient>();
    static ArrayList<AcceptClient> allEverEntered=new ArrayList<AcceptClient>();
    static long time;
    
    public static void main(String[] args) throws Exception {
        int count = 0;
        ServerSocket serverSocket = null;
        boolean condition = true;
        String[][] allUsers=new String[0][0];
        Scanner input;
        port=Integer.parseInt(args[0]);
        
        try {
            File inFile = new File("userlist.txt");
            input = new Scanner(inFile);
            String wholeLine;
            int numberOfLines = 0;
            
            //count number of users to initialize array
            while (input.hasNext()) {
                numberOfLines++;
                input.nextLine();
            }
            //allUsers is a 2D array.  Col1=username, col2=password
            allUsers = new String[numberOfLines][2];
            
            input = new Scanner(inFile);
            int row = 0;
            String[] arr;  //arr[0] is username, arr[1] is pass
            while (input.hasNext()) {
                wholeLine = input.nextLine();
                arr = wholeLine.split("\\s+");
                allUsers[row][0] = arr[0];
                allUsers[row][1] = arr[1];
                row++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error connecting via port " + port);
            System.exit(-1);
        }
        
        //continuously listen for clients
        while (condition) {
            AcceptClient client = new AcceptClient(serverSocket.accept(), allUsers, time=System.currentTimeMillis());
            threads.add(client);
            allEverEntered.add(client);
            client.start();
            
        }
        System.out.println(count);
        serverSocket.close();
    }

    
    public static ArrayList<AcceptClient> getConnectedClients(){
        return threads;
    }
    
    //1 hr = 3600000 ms
    public static ArrayList<String>getLastSixty(){
        ArrayList<String> last60 = new ArrayList<String>();
        for(AcceptClient thread: allEverEntered){
            if(System.currentTimeMillis()-thread.getTimeEntered()<3600000){
                if(!last60.contains(thread.getUserName()))
                    last60.add(thread.getUserName());
            }
        }
        
        for(String name: last60){
            if(name==null)
                last60.remove(name);
        }
        return last60;
    }
    
    public static void removeClient(String name){
        int userIndex=0;
        int n=0;
        for(AcceptClient thread:threads){
            if(thread.getUserName().equals(name))
                userIndex=n;
            n++;
        }
        threads.remove(userIndex);
    }
    
    public static void messageAll(String m) {
        for (AcceptClient ac : threads) {
            ac.message(m);
        }

    }
}