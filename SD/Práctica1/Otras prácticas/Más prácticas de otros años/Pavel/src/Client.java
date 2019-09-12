import java.io.IOException;
import java.net.Socket;

/**
 * Created by pavel on 12/09/16.
 * Interfaz del cliente para sockets
 */
class Client {
    public static void main(String[] args) {
        //Obtenemos la dirección IP y el puerto al que queremos conectarnos
        String serverIP = "";
        int serverPort = -1;
        if (args.length != 2) {
            try {
                System.out.printf("Connection adress (IP:Port): ");
                serverIP = SocketUtils.input.readLine();
                serverPort = Integer.parseInt(serverIP.substring(serverIP.indexOf(":")+1)); //Recojo la subcadena desde : (no incluido) hasta el final
                serverIP = serverIP.substring(0, serverIP.indexOf(":"));
            } catch (NumberFormatException e) {
                System.err.println("Error: port must be a number");
            } catch (IOException e) {
                System.err.println("Error: unable to open console input buffer");
            }
        } else {
            serverIP = args[0];
            serverPort = Integer.parseInt(args[1]);
        }

        try {
            Socket skClient = new Socket(serverIP, serverPort);
            System.out.println("Socket conected to " + skClient.getRemoteSocketAddress() + ":" + skClient.getLocalPort());
            System.out.printf("Message to send: ");
            SocketUtils.sendMessage(skClient, SocketUtils.input.readLine());
            System.out.println("Response: " + SocketUtils.receiveMessage(skClient));
            skClient.close();
        } catch (IOException e) {
            System.err.println("Error: unable to connect");
        }
    }
}
