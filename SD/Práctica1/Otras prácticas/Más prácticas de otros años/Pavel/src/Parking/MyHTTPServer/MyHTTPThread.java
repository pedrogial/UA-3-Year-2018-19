package Parking.MyHTTPServer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static Parking.MyHTTPServer.SocketUtils.receiveMessage;
import static Parking.MyHTTPServer.SocketUtils.sendMessage;

/**
 * Implements all the HTTP Server's logic
 * Each request is processed as a new thread in order to make it concurrent
 * Created by pavel on 3/10/16.
 */
class MyHTTPThread extends Thread {
    private final Socket requestSocket;
    private final MyHTTPSettings settings;

    MyHTTPThread(MyHTTPSettings settings, Socket requestSocket) {
        this.requestSocket = requestSocket;
        this.settings = settings;
    }

    /**
     * MyHTTP-Server's main logic. Receives an HTTP Petition and processes it:
     * <ol>
     *     <li>Checks if HTTP Method is GET. Otherwise, returns HTTP 405 Method Not Allowed error</li>
     *     <li>Checks wich kind of content requested the HTTP (index.html by default)</li>
     *     <li>If content is dynamic, serve dynamic content passing its query</li>
     *     <li>If content is static, serve requested file passing its filename</li>
     * </ol>
     */
    @Override
    public void run() {
        try {
            String request = receiveMessage(requestSocket);
            String[] requestParts = request.split(" ");
            if (requestParts[0].equals("GET")) {
                String URL = requestParts[1];
                if (URL.equals("/")) URL += (settings.DEFAULT_INDEX_PAGE);
                String[] routeParts = URL.split("/");
                if (routeParts[1].equals(settings.DYNAMIC_CONTENT_KEYWORD))
                    serveDynamicRequest(routeParts.length == 3 ? routeParts[2] : "");
                else serveStaticRequest(routeParts[1]);
            } else {
                System.err.println("ERROR 405 for client " + requestSocket.getRemoteSocketAddress() + ". Requested method: " + requestParts[0]);
                sendHTTPResponse(MyHTTPStatusCode.METHOD_NOT_ALLOWED.getHTMLError(), MyHTTPStatusCode.METHOD_NOT_ALLOWED);
            }
            requestSocket.close();
        } catch (IOException e) {
            System.err.println("ERROR: Unable to open inputStream from socket");
            e.printStackTrace();
        }
    }

    /**
     * Connects to the controller, sends him the query, receives its response and sends it to the HTTP Client.
     * If the controller sends a null response, an HTTP 409 Conflict error is sent.
     * @param query the query to send
     * @throws IOException if the HTTP socket is broken. If the Controller's socket is the broken one, an HTTP 409 Conflict error is sent
     */
    private void serveDynamicRequest(String query) throws IOException {
        try {
            Socket s = new Socket(settings.CONTROLLER_IP, settings.CONTROLLER_PORT);
            sendMessage(s, query + "\n\r");
            String response = receiveMessage(s);
            s.close();
            if (!response.replaceAll("\\n","").equals("null")) sendHTTPResponse(response, MyHTTPStatusCode.OK);
            else throw new IOException();
        } catch (IOException e) {
            sendHTTPResponse(MyHTTPStatusCode.CONFLICT.getHTMLError(), MyHTTPStatusCode.CONFLICT);
        }
    }

    /**
     * Opens a file, applies to him the HTML escape characters and calls the sendHTTPResponse method
     * @param path the requested file's path
     * @throws IOException if the socket is borken. If the file is not found, an HTTP 404 Not Found error is sent
     */
    private void serveStaticRequest(String path) throws IOException {
        try {
            sendHTTPResponse(HTMLEntityEncode(new String(Files.readAllBytes(Paths.get(path)))) + "\n", MyHTTPStatusCode.OK);
        } catch (IOException e) {
            try {
                sendHTTPResponse(MyHTTPStatusCode.NOT_FOUND.getHTMLError(), MyHTTPStatusCode.NOT_FOUND);
            } catch (IOException e1) { e1.printStackTrace(); }
            System.err.println("Client " + requestSocket.getRemoteSocketAddress() + " got ERROR 404 for file " + path);
        }
    }

    /**
     * Sends a generic HTTP Response
     * @param fileContent Content to send
     * @param statusCode HTTP Code wich was processed
     * @throws IOException if the socket is broken.
     */
    private void sendHTTPResponse(String fileContent, MyHTTPStatusCode statusCode) throws IOException {
        String response = "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getDescription() + "\n" +
                "Connection: close\n" +
                "Content-Length: " + fileContent.length() + "\n" +
                "Content-Type: text/html\n" +
                "Server: practicas-sd\n" +
                "\n" + //Headers end with an empty line
                fileContent;
        sendMessage(requestSocket, response);
    }

    /**
     * Encodes a String to its HTML special characters
     * @param s the given String
     * @return
     */
    @NotNull
    static String HTMLEntityEncode(String s) {
        StringBuilder builder = new StringBuilder ();
        for (char c : s.toCharArray()) builder.append((int)c < 128 ? c : "&#" + (int) c + ";");
        return builder.toString();
    }
}
