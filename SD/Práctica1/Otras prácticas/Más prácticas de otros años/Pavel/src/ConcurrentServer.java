import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pavel on 15/09/16.
 * Versión del servidor concurrente. Cada petición será un hilo de ejecución
 */
class ConcurrentServer {
    public static void main(String[] args) {
        SocketUtils.displayIPAdresses();
        //Obtenemos el puerto en el que queremos comenzar a escuchar
        int serverPort = Server.getServerPort(args);

        //Abrimos el socket y comenzamos a escuchar
        try {
            ServerSocket skServer = new ServerSocket(serverPort);
            System.out.println("Socket opened at port " + skServer.getLocalPort());

            while (true) { //Bucle. Para con ^C o ^Z
                Socket skRequest = skServer.accept();
                System.out.println("Serving to " + skRequest.getRemoteSocketAddress());
                Thread t = new ThreadServer(skRequest);
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Error: connection closed with client at port" + serverPort);
        }
    }
}
