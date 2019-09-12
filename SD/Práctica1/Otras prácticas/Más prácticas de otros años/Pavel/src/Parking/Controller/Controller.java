package Parking.Controller;

import Parking.MyHTTPServer.MyHTTPSettings;
import Parking.MyHTTPServer.SocketUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pavel on 17/10/16.
 * Intermediate between MyHTTPServer and RMI Register
 */
class Controller {
    public static void main(String args[]) {
        try {
            MyHTTPSettings settings = MyHTTPSettings.readConfig();
            System.out.println("Controller-Server setup complete. Server info: " + settings);
            init(settings);
        } catch (FileNotFoundException e) {
            System.err.println("Controller's config file not found");
        }
    }

    /**
     * Does the same that the MyHTTPServer's init, but with minor changes in order to work like a Controller
     * @param settings the readen settings.
     * @see Parking.MyHTTPServer.MyHTTPServer
     */
    private static void init(MyHTTPSettings settings) {
        try {
            SocketUtils.displayIPAdresses(); //Shows all available IP's that can connect to the server
            ServerSocket serverSocket = new ServerSocket(settings.CONTROLLER_PORT);
            System.out.println("Socket opened at port " + serverSocket.getLocalPort());

            //noinspection InfiniteLoopStatement
            while (true) { //Server is stopped with ^C
                Socket requestSocket = serverSocket.accept();
                System.out.println("Serving to " + requestSocket.getRemoteSocketAddress());
                if (Thread.activeCount() <= settings.MAX_SERVER_CONNECTIONS) {
                    Thread t = new ControllerThread(settings, requestSocket);
                    t.start();
                } else {
                    System.err.println("Connection with " + requestSocket.getRemoteSocketAddress() + " closed due to connection limit");
                    requestSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to open socket");
        }
    }
}
