/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myhttpserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author miguel
 */
public class HTTPThread extends Thread {
    private static final int bufferSize = 2048; // tamaño del buffer de lectura/escritura
    private static final String dynamicContent = "controladorSD/"; // string que controla si se demanda un recurso dinamico
    private static final String dynamicContentStation = "Station=";
    private static final int httpCorrect = 200; // codigos de estado http
    private static final int httpNotFound = 404;
    private static final int httpServerError = 500;
    private static final int httpMethodNotAllowed = 405;
    
    private Socket client;
    private static String controllerIP;
    private static int controllerPort;

    HTTPThread(Socket client, String controllerIP, int controllerPort) {
        this.client = client;
        this.controllerIP = controllerIP;
        this.controllerPort = controllerPort;
    }
    
    private void sendErrorResponse (String petition) { // Metodo que llamara cuando se requiera manejar un error
        int statusCode = httpCorrect;
        String errorResponse = "";
        
        if (petition.equals("405")) {
            statusCode = httpMethodNotAllowed;
            errorResponse = "ERROR 405: method not allowed";
        } else {
            errorResponse = "Error, el recurso " + petition + " no existe";
        }
        
        try {
            OutputStream OS = client.getOutputStream();
            DataOutputStream DOS = new DataOutputStream(OS);
            DOS.writeUTF("HTTP/1.0 " + statusCode + " OK \n Server : MyHTTPServer/1.0 \nContent-Type : text/HTML \nContent-Length : " + errorResponse.length() + "\n\n" + errorResponse);
            DOS.close();
        } catch (Exception ex) {
            System.out.println("ERROR (" + ex.toString() + ") en la escritura del socket");
        }
        
    }

    private void sendTextResponse(String contentDemand) {
        String contentResponse = "";
        int statusCode = httpCorrect; // si todo sale bien, devolvera codigo 200 

        if (contentDemand.contains(dynamicContent)) {
            contentResponse = getDynamicContent(contentDemand);
        } else {
            /*
             * Recurso estatico contenido en el servidor
             */
            File file = new File(contentDemand);
            if (file.exists()) {
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String line;

                    while ((line = br.readLine()) != null) {
                        contentResponse += line;
                    }

                } catch (Exception e) {
                    statusCode = httpServerError;
                    contentResponse = "500 ERROR interno, no se ha podido leer el fichero";
                }
            } else {
                statusCode = httpNotFound;
                contentResponse = "ERROR 404: Not found";
            }
        }
        try {
            OutputStream OS = client.getOutputStream();
            DataOutputStream DOS = new DataOutputStream(OS);
            DOS.writeUTF("HTTP/1.0 " + statusCode + " OK \n Server : MyHTTPServer/1.0 \nContent-Type : text/HTML \nContent-Length : " + contentResponse.length() + "\n\n" + contentResponse);
            DOS.close();
        } catch (Exception ex) {
            System.out.println("ERROR (" + ex.toString() + ") en la escritura del socket");
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////
    // metodos de recurso dinamico
    /////////////////////////////////////////////////////////////////////////////
    
    private static String getDynamicContent(String petition) {
        int stationNo;
        String param;
        
        param = parseDynamicParameter(petition);
        try {
            stationNo = parseDynamicStation(petition);
        } catch (Exception e) {
            stationNo = -1;
        }
        
        if (stationNo == -1) {
            System.out.println("Recurso no valido");
            return "El recurso demandado no es valido";
        } else {
            System.out.println(" Recibida peticion de: " + param + " para la estacion: " + stationNo);
            String stationResponse = getControllerResponse(param, stationNo);
            String HTMLResponse = generateHTMLFromControllerResponse(stationResponse);
            return HTMLResponse;
        }
    }
    
    private static String generateHTMLFromControllerResponse(String html) {
        String HTMLResponse = "";

        File file = new File("controllerResponse.html");
        if (file.exists()) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;

                while ((line = br.readLine()) != null) {
                    HTMLResponse+= line;
                }
            } catch (Exception e) {
                
            }
        }
        HTMLResponse = HTMLResponse.replace("*controller*", html);

        return HTMLResponse;
    }
    
    private static String parseDynamicParameter(String petition) {
        int position = petition.indexOf(dynamicContent) + dynamicContent.length();
        return petition.substring(position, petition.indexOf("?"));
    }
    
    private static int parseDynamicStation(String petition) {
        int position = petition.indexOf(dynamicContentStation) + dynamicContentStation.length();
        
        return Integer.parseInt(petition.substring(position, petition.length()));
    }

    private static String getControllerResponse(String parameter, int stationNo) {
        String response = "";

        try {
            Socket clientSk = new Socket(controllerIP, controllerPort);
            OutputStream OS = clientSk.getOutputStream();
            DataOutputStream DOS = new DataOutputStream(OS);
            InputStream IS = clientSk.getInputStream();
            DataInputStream DIS = new DataInputStream(IS);
            
            DOS.writeUTF(parameter + "-" + stationNo);
            response = DIS.readUTF();
        } catch (Exception e) {
            System.out.println("Error de comunicacion con el controlador de estaciones: ");
            System.out.println(e.toString());
        }

        return response;
    }
    
    
    
    private static String getBinarycontentType(String fileName) { // recibe un contenido y devuelve su tipo MIME
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        }
        return "not supported";
    }
    
    private void sendBinaryResponse (String contentDemand) { // se llamara siempre que se requiera un recurso que no sea texto
        int statusCode = httpCorrect;
        String contentType = getBinarycontentType(contentDemand);
        File file = new File(contentDemand);

        try {
            OutputStream OS = client.getOutputStream();
            DataOutputStream DOS = new DataOutputStream(OS);
            DOS.writeUTF("HTTP/1.0 " + statusCode + " OK \n Server : MyHTTPServer/1.0 \nContent-Type : " + contentType + " \nContent-Length : " + file.length() + "\n\n"); // monto la cabecera 
            InputStream IS = new FileInputStream(file); 
            int car;
            
            while ((car = IS.read()) != -1) { // vamos leyendo byte a byte y lo enviamos por el outputStream
                DOS.write(car);
            }
            DOS.close();
        } catch (Exception e) {
            
        }
        
        
    }
    
    public String readSocket() { // lee el socket de conexion con el cliente

        String datos = "";

        try {

            InputStreamReader ISR = new InputStreamReader(client.getInputStream());
            BufferedReader br = new BufferedReader(ISR);
            datos = br.readLine();

        } catch (Exception e) {
            System.out.println("Error de lectura del socket: ");
            System.out.println(e.toString());
        }
        return datos;
    }
    
    public void managePetition (String petition) {
        String contentDemand = "";
        boolean textResponse = false;
        boolean binaryResponse = false;
        

        String parsePetition = petition.substring(5, petition.length()); // elimino el tag de la peticion
        if (parsePetition.contains(" ")) {
            String[] splitPetition = parsePetition.split(" ");
            contentDemand = splitPetition[0];
        }

        if (contentDemand.equals("/") || contentDemand.equals("") || contentDemand.equals(" "))
            contentDemand = "index.html";
        
        System.out.println(" Recibida peticion de: " + contentDemand + " desde: " + client.getInetAddress());
        
        if (contentDemand.endsWith(".htm") || contentDemand.endsWith(".html") || contentDemand.endsWith(".txt") || contentDemand.contains("controladorSD"))
            textResponse = true;
        if (contentDemand.endsWith(".png") || contentDemand.endsWith(".gif") || contentDemand.endsWith(".jpg") || contentDemand.endsWith(".jpeg"))
            binaryResponse = true;
        
        if (textResponse) {
            sendTextResponse(contentDemand);
        } else {
            if (binaryResponse)
                sendBinaryResponse(contentDemand);
            else
                sendErrorResponse(contentDemand); // si no es de ninguno de los tipos aceptados por el servidor
        }   
    }
    
    @Override
    public void run() { // override del metodo run de un hilo
        try {
            String petition;
            
            while (!client.isClosed()) {
                petition = readSocket();
                
                if (petition.length() > 3) {
                    if (petition.substring(0, 3).equalsIgnoreCase("GET")) {
                        managePetition(petition);
                    } else {
                        sendErrorResponse("405"); // method not allowed se controla aqui, y se devuelve el codigo de error 405
                        System.out.println("Peticion no soportada. Solo GET esta disponible");
                    }
                } else {
                    try {
                        client.close();
                    } catch (IOException ex) {
                        //Logger.getLogger(HTTPThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            this.finalize();
        } catch (Throwable ex) {
            //Logger.getLogger(HTTPThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
