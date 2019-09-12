/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Station;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author alejandro
 */
public class Controller {

    /////////////////////////Para enviar y recibir datos del httpServer////////////////////////////////
    /**
     * Lee datos del socket. Supone que se le pasa un buffer con hueco
     * suficiente para los datos. Devuelve el numero de bytes leidos o 0 si se
     * cierra fichero o -1 si hay error.
     */
    public String leeSocket(Socket p_sk, String p_Datos) {
        try {
            InputStream aux = p_sk.getInputStream();
            DataInputStream flujo = new DataInputStream(aux);
            p_Datos = new String();
            p_Datos = flujo.readUTF();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return p_Datos;
    }

    /**
     * Escribe dato en el socket cliente. Devuelve numero de bytes escritos, o
     * -1 si hay error.
     */
    public void escribeSocket(Socket p_sk, String p_Datos) {
        try {
            PrintWriter out= new PrintWriter(p_sk.getOutputStream());
            out.println(p_Datos);
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////// 
    ////////////////////////////////Para recibir datos del RMI/////////////////////////////////////////
    private String pedirOperacion(String host, String port, String object) {
        Interfaz objetoRemoto = null;
        String res = "";
        String element = "";//Dato que queremos consultar temperatura
        String objeto = "ObjetoRemoto";//nombre del objeto
        String tipoOp = "";//tipo de operación, get o set
        String valorSet="";//valor a cambiar
        String aux1;
        try {
            StringTokenizer s = new StringTokenizer(object, "?");
            element = s.nextToken();
            System.out.println(element);
            aux1 = s.nextToken();
            if(!aux1.contains("station")){
                return "error";
            }
            s = new StringTokenizer(aux1, "=");
            tipoOp = s.nextToken();
            System.out.println(tipoOp);
            objeto = objeto.concat(s.nextToken());
            System.out.println(objeto);
            valorSet=s.nextToken();
            s = new StringTokenizer(objeto, "&");
            objeto = s.nextToken();
            tipoOp = s.nextToken();
            System.out.println(tipoOp);
            
            System.out.println(tipoOp + "=" + valorSet);
            System.out.println(objeto);
        } catch (Exception ex) {
            System.err.println("Se ha producido una excepcion ");
        }

        System.out.println(element + " " + objeto + " " + tipoOp);
        //object es el nombre del objeto, se debe hacer split para obtener únicamente el nombre, ya que el string 
        //tambien contiene la operacion a buscar
        String servidor = "rmi://" + host + ":" + port;
        System.out.println("Servidor:" + servidor);
        String servidorConcreto = servidor.concat("/" + objeto);
        System.out.println("Objeto:" + servidorConcreto);
        String names[];
        try {

            System.setSecurityManager(new RMISecurityManager());
            if(!tipoOp.contains("get") && !tipoOp.contains("set")){
                tipoOp="get";
            }
            
            names = Naming.list(servidor);//Devuelve el array con todos los nombres existentes en el registro;
            //Muestra los nombres de los objetos registrados
            System.out.println(element);
            if (element.equals("index")) {
                //Crear una página HTML Con los nombres de los servidores
                res = "<!DOCTYPE html>\n<html> \n"
                        + "    <head>\n"
                        + "        <meta charset=\"utf-8\">\n"
                        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                        + "        <title>CENTRO DE CONTROL</title>\n"
                        + "        <meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\">\n"
                        + "    </head>\n"
                        + "    <body>\n"
                        + "        <h1>BIENVENIDO A EL CENTRO DE CONTROL DE ESTACIONES METEOROLÓGICAS</h1>\n"
                        + "        <a href=\"/index.html\">Inicio</a>\n";

                for (int i = 0; i < names.length; i++) {
                    StringTokenizer s = new StringTokenizer(names[i], "/");
                    s.nextToken();
                    String ident = s.nextToken();
                    s = new StringTokenizer(ident, "ObjetoRemoto");
                    ident = s.nextToken();
                   // System.out.println(ident);
                    res = res.concat("<br><a href=\"/controladorSD/all?station=" + ident + "\" post >Estacion " + ident + "</a> \n");
                }

                res = res.concat("</body> \n </html>\n");
                return res;
            } else {
                objetoRemoto = (Interfaz) Naming.lookup(servidorConcreto);
            }

        } catch (Exception ex) {
            System.out.println("Error al instanciar el objeto remoto: " + ex);
            res="error";
        }
        
        switch(element){
            case "all":
                try{
                res = "<!DOCTYPE html>\n<html> \n"
                        + "    <head>\n"
                        + "        <meta charset=\"utf-8\">\n"
                        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                        + "        <title>ESTACIÓN "+objetoRemoto.getId()+"</title>\n"
                        + "        <meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\">\n"
                        + "    </head>\n"
                        + "    <body>\n"
                        + "        <h1>ESTACIÓN     "+objetoRemoto.getId()+"</h1>\n"
                        + "        <a href=\"/controladorSD/index\">Estaciones</a>\n"
                        + "        <br><a href=\"/controladorSD/temperatura?station="+objetoRemoto.getId()+"\">Temperatura: </a>"+ objetoRemoto.getTemp()
                        + "        <br><a href=\"/controladorSD/luminosidad?station="+objetoRemoto.getId()+"\">Luminosidad: </a>"+ objetoRemoto.getLum()
                        + "        <br><a href=\"/controladorSD/humedad?station="+objetoRemoto.getId()+"\">Humedad: </a>"+ objetoRemoto.getHum()
                        + "        <br><a href=\"/controladorSD/pantalla?station="+objetoRemoto.getId()+"\">Pantalla LCD: </a>"+ String.valueOf(objetoRemoto.getLCD())
                        + "</body> \n </html>\n";
                }
                catch(Exception ex){
                    res="error";
                    System.err.println("Ha habido un problema al acceder a los datos del objeto: "+ex);
                }
                break;
            case "temperatura":
                try{
                    if(tipoOp.equals("get")){
                    res = "<!DOCTYPE html>\n<html> \n"
                        + "    <head>\n"
                        + "        <meta charset=\"utf-8\">\n"
                        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                        + "        <title>ESTACIÓN "+objetoRemoto.getId()+"</title>\n"
                        + "        <meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\">\n"
                        + "    </head>\n"
                        + "    <body>\n"
                        + "        <h1>ESTACIÓN     "+objetoRemoto.getId()+"</h1>\n"
                        + "        <a href=\"/controladorSD/index\">Estaciones</a>\n"
                        + "        <a href=\"/controladorSD/all?station="+objetoRemoto.getId()+"\">Atrás</a>"
                        + "        <br>Temperatura: "+ objetoRemoto.getTemp()
                        + "</body> \n </html>\n";
                    }
                      else{res="error";}
                }
                catch(Exception ex){
                    res="error";
                    System.err.println("Ha habido un problema al acceder a los datos del objeto: "+ex);
                }
                break;
            case "humedad":
                try{
                    if(tipoOp.equals("get")){
                    res = "<!DOCTYPE html>\n<html> \n"
                        + "    <head>\n"
                        + "        <meta charset=\"utf-8\">\n"
                        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                        + "        <title>ESTACIÓN "+objetoRemoto.getId()+"</title>\n"
                        + "        <meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\">\n"
                        + "    </head>\n"
                        + "    <body>\n"
                        + "        <h1>ESTACIÓN     "+objetoRemoto.getId()+"</h1>\n"
                        + "        <a href=\"/controladorSD/index\">Estaciones</a>\n"
                        + "        <a href=\"/controladorSD/all?station="+objetoRemoto.getId()+"\">Atrás</a>"
                        + "        <br>Humedad: "+ objetoRemoto.getHum()
                        + "</body> \n </html>\n";
                    }
                    else{
                        res="error";
                    }
                }
                catch(Exception ex){
                    res="error";
                    System.err.println("Ha habido un problema al acceder a los datos del objeto: "+ex);
                }
                break;
            case "luminosidad":
                                try{
                    if(tipoOp.equals("get")){
                    res = "<!DOCTYPE html>\n<html> \n"
                        + "    <head>\n"
                        + "        <meta charset=\"utf-8\">\n"
                        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                        + "        <title>ESTACIÓN "+objetoRemoto.getId()+"</title>\n"
                        + "        <meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\">\n"
                        + "    </head>\n"
                        + "    <body>\n"
                        + "        <h1>ESTACIÓN     "+objetoRemoto.getId()+"</h1>\n"
                        + "        <a href=\"/controladorSD/index\">Estaciones</a>\n"                        
                        + "        <a href=\"/controladorSD/all?station="+objetoRemoto.getId()+"\">Atrás</a>"
                        + "        <br>Luminosidad: "+ objetoRemoto.getLum()
                        + "</body> \n </html>\n";
                    }
                    else{
                        res="error";
                    }
                }
                catch(Exception ex){
                    res="error";
                    System.err.println("Ha habido un problema al acceder a los datos del objeto: "+ex);
                }
                break;
            case "pantalla":
                                try{
                    if(tipoOp.equals("get")){
                    res = "<!DOCTYPE html>\n<html> \n"
                        + "    <head>\n"
                        + "        <meta charset=\"utf-8\">\n"
                        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                        + "        <title>ESTACIÓN "+objetoRemoto.getId()+"</title>\n"
                        + "        <meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\">\n"
                        + "    </head>\n"
                        + "    <body>\n"
                        + "        <h1>ESTACIÓN     "+objetoRemoto.getId()+"</h1>\n"
                        + "        <a href=\"/controladorSD/index\">Estaciones</a>\n"
                        + "        <a href=\"/controladorSD/all?station="+objetoRemoto.getId()+"\">Atrás</a>"
                        + "        <br>Pantalla LCD: "+String.valueOf(objetoRemoto.getLCD())
                            //Aqui es donde se introduce el form
                        + "        <FORM method=get action=\"pantalla\">"
                        + "        Introduce el nuevo valor de la pantalla:"
                        + "        <INPUT type=\"hidden\" name=\"station\" value=\""+objetoRemoto.getId()+"\">"
                        + "        <br><INPUT type=text name=\"set\">"
                        + "        <br><INPUT type=\"submit\" value=\"Enviar\"> "
                        + "        </FORM>"
                        + "    </body> \n </html>\n";
                    }
                    else if(tipoOp.equals("set")){
                        if(valorSet.isEmpty()){
                            res="errorComando";
                            break;
                        }
                        valorSet= valorSet.replace('+', ' ');
                        objetoRemoto.setLCD(valorSet);
                        
                        res = "<!DOCTYPE html>\n<html> \n"
                        + "    <head>\n"
                        + "        <meta charset=\"utf-8\">\n"
                        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                        + "        <title>ESTACIÓN "+objetoRemoto.getId()+"</title>\n"
                        + "        <meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\">\n"
                        + "    </head>\n"
                        + "    <body>\n"
                        + "        <h1>ESTACIÓN     "+objetoRemoto.getId()+"</h1>\n"
                        + "        <a href=\"/controladorSD/index\">Estaciones</a>\n"
                        + "        <a href=\"/controladorSD/all?station="+objetoRemoto.getId()+"\">Atrás</a>"
                        + "        <br>Se ha cambiado correctamente el valor de la pantalla."
                        + "    </body> \n </html>\n";
                    }
                    else{
                        res="error";
                    }
                }
                catch(Exception ex){
                    res="error";
                    System.err.println("Ha habido un problema al acceder a los datos del objeto: "+ex);
                }
                break;
            default:
                res="errorComando";
                break;
        }
        ///Hacer lo propio, pedir los datos, get o set al objeto
        return res;
    }

    public static void main(String[] args) {
        String hostRMI;
        String portRMI;
        String portHTTP;
        Controller cr = new Controller();
        int i = 0;
        if (args.length < 3) {
            try {
                System.out.println("Debe indicar la direccion del servidor y el puerto de escucha HTTP.");
                System.out.println("Se debe utilizar del siguiente modo ./Controller hostRMI portRMI portHTTP");
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            return;
        }

        hostRMI = args[0];
        portRMI = args[1];
        portHTTP = args[2];
        String op = "";
        String object = "";

        while (i == 0) {
            try {

                ServerSocket skServidor = new ServerSocket(Integer.parseInt(portHTTP));
                System.out.println("Escucho a HTTPServer desde el puerto " + portHTTP);

                /*
                 * Mantenemos la comunicacion con el cliente
                 */
                for (;;) {
                    /*
                     * Se espera un cliente que quiera conectarse
                     */
                    Socket skCliente = skServidor.accept(); // Crea objeto
                    System.out.println("Sirviendo cliente...");

                    op = cr.leeSocket(skCliente, op);

                    //Debemos obtener el nombre del objeto y la operación a realizar
                    //object=op.split("/")[1];//Obtener el nombre del objeto con la operación a realizar con lo de = o ?, mirar guia de pracica
                    System.out.println(op);//imprimir el string pedido
                    /*
                     * Se escribe en pantalla la informacion que se ha recibido del
                     * cliente
                     */
                    //Arreglar
                    op = cr.pedirOperacion(hostRMI, portRMI, op);
                    cr.escribeSocket(skCliente, op);

                    skCliente.close();//Debemos cerrar siempre el cliente.
                    //System.exit(0);				
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
}
