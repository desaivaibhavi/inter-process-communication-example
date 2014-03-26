
import java.io.*;
import java.net.*;

public class Client implements Runnable {

    private static boolean closed = false;
    private static Socket socket = null;
    private static PrintWriter out = null;
    private static BufferedReader sIn = null;
    private static BufferedReader in = null;
    private static String fromServer;
    private static String fromUser;

    public static void main(String[] args) throws IOException {



        try {
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sIn = new BufferedReader(new InputStreamReader(System.in));

        } catch (UnknownHostException e) {
            System.out.println("Unknown Host");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("No I/O: " + e.getMessage());
            System.exit(1);
        }

        if (socket != null && out != null & in != null) {
            try {
                new Thread(new Client()).start();
                while (!closed) {

                    fromUser = sIn.readLine();
                    if(fromUser.equals("exit"))
                        break;
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        out.println(fromUser);
                    }

                }
                out.close();
                in.close();
                sIn.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("No I/O: " + e.getMessage());
                System.exit(1);

            }

        }






    }

    @Override
    public void run() {
        String fromServer;
        try {
            while ((fromServer= in.readLine()) != null) {
                if(fromServer.equals("exit")){
                    break;
                }
                if (fromServer != null) {
                    System.out.println("Server: " + fromServer);
                }
                if (fromServer != null && fromServer.equals("You failed too many times.  Bye Bye!")) {
                    break;
                }

            }
            closed = true;
        } catch (IOException e) {
            System.out.println("");
        }
    }
}