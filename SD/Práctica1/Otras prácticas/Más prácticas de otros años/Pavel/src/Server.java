import java.io.IOException;
import java.net.*;

/**
 * Created by pavel on 12/09/16.
 * Interfaz del servidor para sockets
 */
class Server {
    /**
     * Configuramos el estado del servidor con estas dos constantes y la variable que lo controla
     * Seguiremos escuchando al cliente mientras ServerStatus tenga el estado SERVER_ON
     */
    /*PALUEGO
    private static final int SERVER_ON = 1;
    private static final int SERVER_OFF = 0;
    private static int ServerStatus = SERVER_OFF; //Por defecto no aceptamos
    */

    public static void main(String[] args) {
        SocketUtils.displayIPAdresses();
        //Obtenemos el puerto en el que queremos comenzar a escuchar
        int serverPort = getServerPort(args);

        //Abrimos el socket y comenzamos a escuchar
        try {
            ServerSocket skServer = new ServerSocket(serverPort);
            System.out.println("Socket opened at port " + skServer.getLocalPort());

            while (true) { //Bucle. Para con ^C o ^Z
                Socket skRequest = skServer.accept();
                System.out.println("Serving to " + skRequest.getRemoteSocketAddress());
                String inputMessage = SocketUtils.receiveMessage(skRequest);
                System.out.println("inputMessage = " + inputMessage);
                SocketUtils.sendMessage(skRequest, inputMessage.toUpperCase());
                skRequest.close();
            }
        } catch (IOException e) {
            System.err.println("Error: connection closed with client at port" + serverPort);
        }
    }

    public static int getServerPort(String[] args) {
        int serverPort = -1;
        if (args.length != 1) {
            try {
                System.out.printf("Server port: ");
                serverPort = Integer.parseInt(SocketUtils.input.readLine());
            } catch (NumberFormatException e) {
                System.err.println("Error: port must be a number");
            } catch (IOException e) {
                System.err.println("Error: unable to open console input buffer");
            }
        } else {
            serverPort = Integer.parseInt(args[0]);
        }
        return serverPort;
    }

}
