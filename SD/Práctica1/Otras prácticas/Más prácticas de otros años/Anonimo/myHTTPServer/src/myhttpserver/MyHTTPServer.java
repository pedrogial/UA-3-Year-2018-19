/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myhttpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;


/**
 *
 * @author miguel
 */
public class MyHTTPServer {
    
    private static int port = 8080;
    private static int maxConnections = 5;
    private static int controllerPort = 2000;
    private static String controllerIP = "127.0.0.1";
    private static int activeConnections = 0;

    public void setup() {
        
        File file = new File("setup.txt");
        
        
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String [] configLine = line.split("=");
                String parameter = configLine[0];
                String value = configLine[1];
                
                switch(parameter) {
                    case "puerto":
                        port = Integer.parseInt(value);
                        System.out.println("Puerto configurado: " + port);
                        break;
                    case "maxconnections":
                        maxConnections = Integer.parseInt(value);
                        System.out.println("Maximo de clientes admitidos: " + maxConnections);
                        break;
                    case "controllerip":
                        controllerIP = value;
                        System.out.println("IP del controlador: " + controllerIP);
                        break;
                    case "controllerport":
                        controllerPort = Integer.parseInt(value);
                        System.out.println("Puerto del controlador: " + controllerPort);
                        break;        
                }
            }
        } catch (Exception ex) {
           System.out.println("Error al abrir el fichero o fichero no encontrado");
           System.out.println("Valores por defecto: ");
           System.out.println(" -Puerto: " + port);
           System.out.println(" -Maximo de clientes admitidos: " + maxConnections);
           System.out.println(" -IP del controlador: " + controllerIP);
           System.out.println(" -Puerto del controlador: " + controllerPort);
           
        }
        
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        try {
            MyHTTPServer server = new MyHTTPServer();
            server.setup();
            ServerSocket sk = new ServerSocket(port);
            System.out.println("Escuchando en el puerto " + port);
            
            for (;;) {
                if (activeConnections < maxConnections) {
                    Socket client = sk.accept();
                    Thread connection = new HTTPThread(client, controllerIP, controllerPort);
                    connection.run();
                    activeConnections++;
                    if (!connection.isAlive()) {
                        activeConnections--;
                    }
                } else {
                    System.out.println("Numero maximo de conexiones alcanzado.");
                }    
                
            }
        } catch (Exception e) {
            
        }
    }
    
}
