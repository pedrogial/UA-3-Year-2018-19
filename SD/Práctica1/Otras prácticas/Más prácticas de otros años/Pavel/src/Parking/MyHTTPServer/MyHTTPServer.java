package Parking.MyHTTPServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pavel on 23/10/16.
 * Running server
 */
class MyHTTPServer {
    public static void main(String args[]) {
        try {
            MyHTTPSettings settings = MyHTTPSettings.readConfig();
            System.out.println("MyHTTP-Server setup complete. Server info: " + settings);
            init(settings);
        } catch (FileNotFoundException e) {
            System.err.println("Server's config file not found");
        }
    }

    /**
     * Initializes the HTTP Server with the readen config and serves connections to a web browser.
     * It also controls the amount of simultaneous connections by counting the number of threads that the main process has launched
     * @param settings the readen settings
     */
    private static void init(MyHTTPSettings settings) {
        try {
            SocketUtils.displayIPAdresses(); //Shows all available IP's that can connect to the settings
            ServerSocket serverSocket = new ServerSocket(settings.SERVER_PORT);
            System.out.println("Socket opened at port " + serverSocket.getLocalPort());

            //noinspection InfiniteLoopStatement
            while (true) { //Server is stopped with ^C
                Socket requestSocket = serverSocket.accept();
                System.out.println("Serving to " + requestSocket.getRemoteSocketAddress());
                if (Thread.activeCount() <= settings.MAX_SERVER_CONNECTIONS) {
                    Thread t = new MyHTTPThread(settings, requestSocket);
                    t.start();
                }
                else {
                    System.err.println("Connection with " + requestSocket.getRemoteSocketAddress() + " closed due to connection limit");
                    requestSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to open socket");
        }
    }
}
