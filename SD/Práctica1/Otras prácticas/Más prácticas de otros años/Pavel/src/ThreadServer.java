import java.io.*;
import java.net.Socket;

/**
 * Created by pavel on 15/09/16.
 * Controla la lógica de cada hilo de ejecución del servior concurrente
 */
class ThreadServer extends Thread {
    private final Socket skRequest;

    ThreadServer(Socket socket) {
        skRequest = socket;
    }

    public void run() {
        try {
            String inputCommand = SocketUtils.receiveMessage(skRequest);
            System.out.println("inputCommand = " + inputCommand);
            Thread.sleep(2000);
            System.setOut(new PrintStream(skRequest.getOutputStream()));
            System.out.println(SocketUtils.executeCommand(inputCommand));
            System.out.println("Command executed");
            SocketUtils.sendMessage(skRequest,inputCommand.toUpperCase());
            skRequest.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

}
