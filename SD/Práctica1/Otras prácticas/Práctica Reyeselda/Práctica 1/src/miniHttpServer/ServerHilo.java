/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miniHttpServer;

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alejandro
 */
public class ServerHilo extends Thread {

    private Socket cliente;
    private String hostController;
    private int puertoHilo;
    private Socket controlador;

    public ServerHilo(Socket sk, String hostController, int puertoHilo) {
        cliente = sk;
        this.hostController = hostController;
        this.puertoHilo = puertoHilo;
        /////
        //HAY QUE CREAR EL CLIENTE DEL CONTROLLER CON EL PUERTOHILO
        /////
    }

    /**
     * Lee desde el socket los datos que le pide el cliente y los devuelve
     *
     * @param sk
     * @param datos
     * @return
     */
    public String readSocket(Socket sk, String datos) {
        try {
            InputStream aux = sk.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(aux));;
            datos = new String();
            while (in.ready()) {
                datos = datos.concat(in.readLine() + "\n");
                //  System.out.println(datos); //Para comprobar que es lo que lee
            }
        } catch (Exception ex) {
            // System.err.println("No se ha podido leer el socket: " + ex);
        }

        return datos;
    }

    /**
     * Escribe en el socket que conecta al navegador los datos pasados por
     * string
     *
     * @param sk
     * @param datos
     */
    public void writeSocket(Socket sk, String datos) {
        try {
            PrintWriter out = new PrintWriter(sk.getOutputStream());
            BufferedReader br1;
            String data = "";
            String abrir = "./";
            if (datos.equals("/") || datos.equals("index.html")) {
                abrir = abrir.concat("index.html");
            }

            if (datos.contains("error")) {
                abrir = "./error.html";
            }
            if (abrir.equals("./index.html") | abrir.equals("./error.html")) {
                //System.out.println(abrir);//Muestra el archivo que se abre en el fichero
                br1 = new BufferedReader(new FileReader(abrir));
                if (abrir.equals("./index.html")) {
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html; charset=utf-8");
                    out.println("Server: MyHTTPServer");
                } else {
                    out.println("HTTP/1.1 404 Not Found");
                    out.println("Content-Type: text/html; charset=utf-8");
                    out.println("Server: MyHTTPServer");
                }
                // este linea en blanco marca el final de los headers de la response
                out.println("");
                data = br1.readLine();
                while (data != null) {
                    out.println(data);
                    data = br1.readLine();
                    //System.out.println(data);
                    //Enviar a cliente
                }
            } else {
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html; charset=utf-8");
                out.println("Server: MyHTTPServer");
                out.println("");
                out.println(datos);
                //Si no se han pasado bien los datos, no hace una mierda, por lo tanto ...
            }

            out.flush();
            out.close();//debe ponerse

        } catch (Exception ex) {
            System.err.println("No se ha podido escribir en el socket: " + ex);
        }

    }

    /**
     * Escribe en el socket que conecta con el controlador los datos pasados por
     * string
     *
     * @param sk
     * @param datos
     */
    public void escribeSocket(Socket sk, String datos) {
        try {
            OutputStream aux = sk.getOutputStream();
            DataOutputStream flujo = new DataOutputStream(aux);
            flujo.writeUTF(datos);
        } catch (Exception e) {
            System.out.println("No se ha podido conectar con el controlador: " + e.toString());
        }
    }

    @Override
    public void run() {
        String Cadena;
        String Response = "";
        try {
            do {
                Cadena = "";
                Cadena = readSocket(cliente, Cadena);
                // Se escribe en pantalla la informacion recibida del cliente
                if (!Cadena.isEmpty()) {
                    StringTokenizer s = new StringTokenizer(Cadena);
                    Cadena = s.nextToken();
                    if (Cadena.equals("GET")) {
                        Cadena = s.nextToken();
                        String[] aux = Cadena.split("/");
                        if (aux.length > 0) {
                            Cadena = aux[1];
                        }
                        if (Cadena.equals("controladorSD")) {
                            //Creamos el socket para conectarnos al controlador
                            try {
                                controlador = new Socket(hostController, puertoHilo);
                            } catch (Exception ex) {
                                System.err.println("No se ha podido conectar con el controlador: " + ex);
                            }
                            //peticion RMI a Controller
                            Response = aux[2];
                            //En el caso de devolver error, algún parámetro no se habrá introducido bien
                            System.out.println(Response);
                            escribeSocket(controlador, Response);
                            Response = "";
                            while (Response.isEmpty()) {
                                Response = readSocket(controlador, Response);
                            }
                            //Para asegurarse de que response recibe algo que se pueda copiar
                            Cadena = Response;

                            //                         Response = "error";
                        } //System.out.println(Cadena); //Muestra el nombre del fichero solicitado por el explorador
                        else if (Cadena.equals("favicon.ico")) {
                            break;
                        }

                        writeSocket(cliente, Cadena);
                    }
                }
            } while (true);

            //cliente.close();
        } catch (Exception ex) {

            System.err.println("Se ha producido un error y no se ha podido abrir el servidor: " + ex);
        }
        //Siempre cierra el socket
        try {
            cliente.close();
        } catch (IOException ex1) {
            Logger.getLogger(ServerHilo.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
}
