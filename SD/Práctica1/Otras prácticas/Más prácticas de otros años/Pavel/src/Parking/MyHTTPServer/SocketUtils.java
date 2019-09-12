package Parking.MyHTTPServer;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by pavel on 13/09/16.
 * Funciones comunes para que las demás clases puedan usarlas
 */
public class SocketUtils {
    /**
     * Send message through a socket byte-per-byte
     * @param socket The socket wich will be written
     * @param message The message to write
     * @throws IOException if the socket is broken
     */
    public static void sendMessage(Socket socket, String message) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeBytes(message);
    }

    /**
     * Receives a message that <b>must end with an empty line</b>
     * @param socket The socket wich will be readen
     * @return the message
     * @throws IOException if the socket is broken
     */
    @NotNull
    public static String receiveMessage(Socket socket) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null && !s.equals(""))
            sb.append(s).append("\n");
        return sb.toString();
    }

    /**
     * Display available IP's in this machine (only in IPV4 format)
     */
    public static void displayIPAdresses() {
        try {
            System.out.println("Available IP's at this machine");
            for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) //Gets Network Interfaces
                Collections
                        .list(network.getInetAddresses()).stream() //Gets IP Adresses of the Network Interface
                        .filter(adress -> adress instanceof Inet4Address) //Filters by IPV4
                        .forEach(adress -> System.out.println("\t" + adress)); //Prints every adress
        } catch (SocketException e) {
            System.err.println("Error: unable to display available IP's");
        }
    }
}
