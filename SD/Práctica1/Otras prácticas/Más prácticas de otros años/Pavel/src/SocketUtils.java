import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by pavel on 13/09/16.
 * Funciones comunes para que las demás clases puedan usarlas
 */
class SocketUtils {
    /**
     * Entrada de consola
     */
    static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    static void sendMessage(Socket socket, String message) throws IOException {
        try {
            //System.out.println("Writing to output stream...");
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Error: unable to write stream to client");
            throw new IOException();
        }
    }

    static String receiveMessage(Socket socket) throws IOException {
        try {
            //System.out.println("Reading from input stream...");
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            return inputStream.readUTF();
        } catch (IOException e) {
            System.err.println("Error: unable to read stream from client");
            throw new IOException();
        }
    }

    /**
     * Selecciona todas las IP's que tiene el equipo y las muestra (sólo en formato IPV4)
     */
    static void displayIPAdresses() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces(); //Recibe todas las interfaces de red
            System.out.println("Available IP's at this machine");
            for (NetworkInterface network : Collections.list(networks)) {
                Enumeration<InetAddress> networkadresses = network.getInetAddresses(); //Recibe todas las direcciones de cada interfaz
                //Hacemos un filter de las IP's para quedarnos sólo con las IPV4 y las mostramos
                Collections.list(networkadresses).stream().filter(adress -> adress instanceof Inet4Address).forEach(adress -> System.out.println("\t" + adress));
            }
        } catch (SocketException e) {
            System.err.println("Error: unable to display available IP's");
        }
    }

    static String executeCommand(String command) {
        StringBuilder out = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                out.append(s).append('\n');
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}
