
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.io.BufferedWriter;

public class AcceptClient extends Thread {

    private Socket socket = null;
    private PrintWriter dout = null;
    private BufferedReader din = null;
    private BufferedWriter bw = null;
    private static int atUserName;
    private static int atPass;
    private static int atAuthentication;
    private static int atLoggedIn;
    private int state;
    private String userName = null;
    private String userPassword = null;
    int logInAttempts;
    String[][] allUsers;
    long timeEntered;
    String inputLine;
    String outputLine;

    public AcceptClient(Socket s, String[][] userlist, long t) throws IOException {
        socket = s;
        allUsers = userlist;
        atUserName = 0;
        atPass = 1;
        atAuthentication = 2;
        atLoggedIn = 3;
        state = atUserName;
        logInAttempts = 1;
        timeEntered = t;

            
        }
    

    public void run() {
        System.out.println("Client connected to socket: " + socket.toString());

        try {
            dout = new PrintWriter(socket.getOutputStream(), true);
            din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputLine = handleRequest(null);
            dout.println(outputLine);
          
            //Read from socket and write back the response to client. 
            while((inputLine=din.readLine())!=null) { 
                outputLine = handleRequest(inputLine);
                if(inputLine.equals("exit")){
                    Server.removeClient(this.userName);
                    break;
                }
                if (outputLine != null) {
                    dout.println(outputLine);
                    if (outputLine.equals("exit")) {
                        System.out.println("Server is closing socket for client:" + socket.getLocalSocketAddress());
                        break;
                    }
                } else {
                    System.out.println("No output");
                    break;
                }
                
                
                }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dout.close();
                din.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Failed to close I/O");
            }
        }
    }

    public String handleRequest(String clientRequest) {
        String reply = null;
        String[] command_plus_param;
        try {
            if (clientRequest != null && clientRequest.equalsIgnoreCase("login")) {
                state = atPass;
            }
            if (clientRequest != null && clientRequest.equalsIgnoreCase("exit")) {
                Server.removeClient(this.userName);
                return "exit";
            }


            if (state == atUserName) {
                reply = "Please Enter your user name: ";
                state = atPass;
            } else if (state == atPass) {
                userName = clientRequest;
                reply = "Please Enter your password: ";
                state = atAuthentication;
            } else if (state == atAuthentication) {
                userPassword = clientRequest;
                int index = -1;

                //loop to find row index of User
                for (int i = 0; i < 9; i++) {
                    if (userName.equalsIgnoreCase(allUsers[i][0])) {
                        index = i;
                    }
                }
                if (index == -1) {
                    logInAttempts++;
                    if (logInAttempts > 3) {
                        return "You failed too many times.  Bye Bye!";
                    }
                    reply = "Login Failed, try again. (Attempt #" + logInAttempts + ") Username: ";
                    state = atPass;
                } else if (userName.equalsIgnoreCase(allUsers[index][0]) && userPassword.equals(allUsers[index][1])) {
                    reply = "Welcome to Simple Server!";

                    state = atLoggedIn;
                } else {
                    logInAttempts++;
                    if (logInAttempts > 3) {
                        return "You failed too many times.  Bye Bye!";
                    }
                    reply = "Login Failed, try again. (Attempt #" + logInAttempts + ") Username: ";
                    state = atPass;

                }
            } else if (state == atLoggedIn) {
                int n = 0;
                //displays name of other connected users
                if (clientRequest != null && clientRequest.equalsIgnoreCase("whoelse")) {
                    String listOfClients = "";
                    ArrayList<AcceptClient> connectedClients = new ArrayList<AcceptClient>();
                    connectedClients = Server.getConnectedClients();
                    for (AcceptClient client : connectedClients) {
                        if (!client.getUserName().equals(this.userName)) {
                            if (n == 0) {
                                listOfClients = client.getUserName();
                            } else {
                                listOfClients = listOfClients + " " + client.getUserName();
                            }
                        }
                        n++;
                    }
                    reply = listOfClients;
                } //displays name of users that connected within the last hour
                else if (clientRequest != null && clientRequest.equalsIgnoreCase("wholasthr")) {
                    String clientList = "";
                    ArrayList<String> last60 = new ArrayList<String>();
                    last60 = Server.getLastSixty();
                    for (String s : last60) {
                        clientList = clientList + " " + s;
                    }
                    reply = clientList;
                } // broadcast message following this command to all connected user   
                else if (clientRequest != null && clientRequest.startsWith("broadcast")) {
                    command_plus_param = clientRequest.split("\\s+");
                    String message = command_plus_param[1];
                    //in case there are spaces in message (want entire message)
                    for(int i=2; i<command_plus_param.length;i++)
                        message=message+ " " +command_plus_param[i];
                    Server.messageAll(message);
                    reply = "Broadcasted message to All Clients";
                } else {
                    reply = clientRequest;
                    state = atLoggedIn;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("input process falied: " + e.getMessage());
            return "exit";
        }

        return reply;
    }

    public void message(String m) {
        dout.println(m);
    }
 
    

    
    public String getUserName() {
        return userName;
    }

    public long getTimeEntered() {
        return timeEntered;
    }
}