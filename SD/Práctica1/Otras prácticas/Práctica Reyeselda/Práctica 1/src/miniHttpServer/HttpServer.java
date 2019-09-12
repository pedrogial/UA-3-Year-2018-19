/**
 *
 */
package miniHttpServer;

import java.io.*;
import java.net.*;

/**
 * @author ALEJANDRO REYES ALBILLAR 45931406-S correo ara65@alu.ua.es
 *
 */
public class HttpServer {

    // Abrir Socket
    // url ww.ara65.es
    // Puerto 8080
    // Cerrar socket
    public void OpenServer(int port, String hostController,int controller, int max) {

        if (port == 0) {
            port = 8080;
        }
        if (controller == 0) {
            controller = 8081;
        }
        if (max == 0) {
            max = 5;
        }
        if(hostController.equals("") || hostController==null){
            hostController="localhost";
        }
        try {

            ServerSocket servidor = new ServerSocket(port);
            System.out.println("Escucho el puerto " + port + ".");
            int sockAb=0;//Número de sockets abiertos    
            
            for (;;) {
                if(max>sockAb){
                    sockAb++;
                    Socket cliente= servidor.accept();
                    System.out.println("Sirviendo a cliente..."+sockAb);
                    Thread t = new ServerHilo(cliente,hostController, controller);
                    t.start();
                    sockAb--;
                    //Socket skcontroller = controlador.accept();
                    //cliente.close();
                }
                
                
                
            }

        } catch (Exception ex) {
            System.err.println("Se ha producido un error y no se ha podido abrir el servidor: " + ex);
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        HttpServer miniHTTP = new HttpServer();
        int puerto = 0;
        int controller = 0;
        int max=0;
        String hostController="";
        try {
            if(args.length<1){
            
                System.out.println("Escriba a continuación el puerto que desea abrir (En caso de no escribir nada se abrirá el puerto 8080 por defecto):");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String readPort = br.readLine();
                if (!readPort.equals("")) {
                    puerto = Integer.parseInt(readPort);
                }
            }
            else{
                puerto=Integer.parseInt(args[0]);
            }
            if(args.length<2){
            
                System.out.println("Escriba a continuación el host del controlador (En caso de no escribir nada se seleccionará localhost por defecto):");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String readHost = br.readLine();
                if (!readHost.equals("")) {
                    hostController=readHost;
                }
            }
            else{
                hostController=args[1];
            }
            
            if(args.length<3){
                System.out.println("Escriba a continuación el número de puerto del controlador (En caso de no escribir nada se pondrá el máximo en 8081):");

                BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
                String readPort2 = br2.readLine();
                if (!readPort2.equals("")) {
                     controller= Integer.parseInt(readPort2);
                }
            }
            else{
                controller=Integer.parseInt(args[2]);
            }
            
            if(args.length<4){
                System.out.println("Escriba a continuación el número de conexiones que aceptará el servidor (En caso de no escribir nada se pondrá el máximo en 5.");

                BufferedReader br3 = new BufferedReader(new InputStreamReader(System.in));
                String readMax = br3.readLine();
                if (!readMax.equals("")) {
                     max= Integer.parseInt(readMax);
                }
            }
            else{
                max=Integer.parseInt(args[3]);
            }
        } catch (IOException | NumberFormatException ex) {
            System.err.println("Se ha producido un error al leer desde consola: " + ex);
        }

        miniHTTP.OpenServer(puerto,hostController, controller, max);
    }

}
