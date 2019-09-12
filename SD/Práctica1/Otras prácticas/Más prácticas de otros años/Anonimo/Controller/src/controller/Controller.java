/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import RMIConnector.Constants;
import RMIConnector.RMIConnector;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


/**
 *
 * @author miguel
 */
public class Controller {
   private static int port = 2000;
   private static String RMIip = "127.0.0.1";
   private static int RMIPort = 20000;
   private static String[] paramList = {"temperatura", "luminosidad", "humedad", "display"};
   private static ArrayList<String> temperaturas = new ArrayList<>();
   private static ArrayList<String> humedades = new ArrayList<>();
   private static ArrayList<String> luminosidades = new ArrayList<>();
   private static ArrayList<String> displays = new ArrayList<>();
   private static String [] parsedPetition;
   
   private static Socket client;
    
   /**
    * metodo que lee todos los parametros de configuracion
    * desde un fichero de texto
    */ 
    public void setup() {

        File file = new File("setup.txt");

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] configLine = line.split("=");
                String parameter = configLine[0];
                String value = configLine[1];

                switch (parameter) {
                    case "puerto":
                        port = Integer.parseInt(value);
                        System.out.println("Puerto configurado: " + port);
                        break;
                    case "iprmi":
                        RMIip = value;
                        System.out.println("Direccion IP de las estaciones configurada: " + RMIip);
                        break;
                    case "rmiport":
                        RMIPort = Integer.parseInt(value);
                        System.out.println("Puerto de las estaciones configurado: " + RMIPort);
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error al abrir el fichero o fichero no encontrado");
            System.out.println("Valores por defecto: ");
            System.out.println(" -Puerto: " + port);
            System.out.println(" -Direccion IP de las estaciones: " + RMIip);
            System.out.println(" -Puerto de las estaciones: " + RMIPort);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Controller controller = new Controller();
            controller.setup();
            ServerSocket sk = new ServerSocket(port);
            System.out.println("Escuchando en el puerto " + port);

            for (;;) {
                client = sk.accept();
                String petition = readSocket();
                String response = managePetition(petition);
                writeSocket(response);
                clearData();
                client.close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static String readSocket() { // lee el socket de conexion con el cliente

        String datos = "";

        try {
            InputStream IS = client.getInputStream();
            DataInputStream DIS = new DataInputStream(IS);
            datos = DIS.readUTF();

        } catch (Exception e) {
            System.out.println("Error de lectura del socket: ");
            System.out.println(e.toString());
        }
        return datos;
    }
    
    public static void writeSocket(String data) { // escribe en el socket de conexion con el cliente
        try {
            OutputStream OS = client.getOutputStream();
            DataOutputStream DOS = new DataOutputStream(OS);
            DOS.writeUTF(data);
        } catch (Exception e) {
            System.out.println("Error al devolver datos al servidor");
        }
    }
    
    private static void clearData() { // limpia los datos tras haber respondido una peticion
        temperaturas.clear();
        humedades.clear();
        luminosidades.clear();
        displays.clear();
    }
    
    public static String managePetition(String petition) throws RemoteException, NotBoundException {
        System.out.println("Recibida peticion: " + petition);
        parsedPetition = parsePetition(petition);
        boolean allStations = false;
        String HTMLResponse = "";
        if (parsedPetition[0].contains("set")) {
            String newMessage = getNewMessage(parsedPetition[0]);
            setNewMessage(newMessage, parsedPetition[1]);
            getAllRMI();
            allStations = true;
        } else {
            if (parsedPetition[0].equals("all")) {
                allStations = true;
                getAllRMI();
            } else {
                setSingleParameter(parsedPetition[0]);
            }
        }
        HTMLResponse = generateHTMLResponse(allStations);

        return HTMLResponse;
    }
    
    /*
    Metodo que recibe un mensaje y lo parsea
    */
    private static String getNewMessage(String petition) {
        String [] parsedPetition = petition.split("=");
        String cleanMessage = parsedPetition[1].replaceAll("%20", " ");
        
        return cleanMessage;
    }
    
    /**
     * metodo para hacer set al mensaje de una de las estaciones
     * @param message
     * @param stationNo
     * @return
     * @throws RemoteException
     * @throws NotBoundException 
     */
    private static String setNewMessage(String message, String stationNo) throws RemoteException, NotBoundException {
        String data;

        try {
            if (stationNo.equalsIgnoreCase("1")) {
                Registry registry = LocateRegistry.getRegistry(RMIip, RMIPort);
                RMIConnector remote = (RMIConnector) registry.lookup(Constants.RMIIdOne);
                data = remote.setDisplay(message);
            } else if (stationNo.equalsIgnoreCase("2")) {
                Registry registry = LocateRegistry.getRegistry(RMIip, RMIPort);
                RMIConnector remote = (RMIConnector) registry.lookup(Constants.RMIIdTwo);
                data = remote.setDisplay(message);
            } else {
                return "Estacion Inexistente";
            }
        } catch (Exception e) {
            return "estacion no disponible";
        }
        return data;
    }
    
    private static void setSingleParameter(String param) { // hace un get del parametro pasado 
        try {
         
            switch (param) {
                case "temperatura":
                    temperaturas.add(getParameterRMI(parsedPetition[0], parsedPetition[1]));
                    luminosidades.add("-");
                    humedades.add("-");
                    displays.add("-");
                    break;
                case "luminosidad":
                    luminosidades.add(getParameterRMI(parsedPetition[0], parsedPetition[1]));
                    temperaturas.add("-");
                    humedades.add("-");
                    displays.add("-");
                    break;
                case "humedad":
                    humedades.add(getParameterRMI(parsedPetition[0], parsedPetition[1]));
                    luminosidades.add("-");
                    temperaturas.add("-");
                    displays.add("-");
                    break;
                case "display":
                    displays.add(getParameterRMI(parsedPetition[0], parsedPetition[1]));
                    luminosidades.add("-");
                    temperaturas.add("-");
                    humedades.add("-");
                    break;
                default:
                    temperaturas.add("N/A");
                    luminosidades.add("N/A");
                    humedades.add("N/A");
                    displays.add("Variable no valida");
                    break;
            }
        } catch (IllegalArgumentException i) {
            temperaturas.add("N/A");
            luminosidades.add("N/A");
            humedades.add("N/A");
            displays.add("Estacion inexistente");
        } catch (Exception e) {
            temperaturas.add("N/A");
            luminosidades.add("N/A");
            humedades.add("N/A");
            displays.add("N/A");
        }
    }
    
    private static String[] parsePetition(String petition) {
        return petition.split("-");     
    }
    
    /**
     * Metodo que contacta con las estaciones para recibir datos
     * @param parameter
     * @param stationNo
     * @return
     * @throws RemoteException
     * @throws NotBoundException 
     */
    private static String getParameterRMI(String parameter, String stationNo) throws RemoteException, NotBoundException {
        String data;
        
        if (stationNo.equalsIgnoreCase("1")) {
            Registry registry = LocateRegistry.getRegistry(RMIip, RMIPort);
            registry.lookup(Constants.RMIIdOne);
            RMIConnector remote = (RMIConnector) registry.lookup(Constants.RMIIdOne);
            data = remote.get(parameter);
        } else if (stationNo.equalsIgnoreCase("2")) {
            Registry registry = LocateRegistry.getRegistry(RMIip, RMIPort);
            RMIConnector remote = (RMIConnector) registry.lookup(Constants.RMIIdTwo);
            data = remote.get(parameter);
        } else {
            throw new IllegalArgumentException();
        }
        
        return data;
    }
    
    private static void getAllRMI() { // metodo que recoje todos los parametros de todas las estaciones
        String param;
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 3; j++) {
                param = paramList[i];
                switch (param) {
                    case "temperatura":
                        try {
                            temperaturas.add(getParameterRMI(param, Integer.toString(j)));
                        } catch (Exception e) {
                            temperaturas.add("N/A");
                        }
                        break;
                    case "luminosidad":
                        try {
                            luminosidades.add(getParameterRMI(param, Integer.toString(j)));
                        } catch (Exception e) {
                            luminosidades.add("N/A");
                        }
                        break;
                    case "humedad":
                        try {
                            humedades.add(getParameterRMI(param, Integer.toString(j)));
                        } catch (Exception e) {
                            humedades.add("N/A");
                        }
                        break;
                    case "display":
                        try {
                            displays.add(getParameterRMI(param, Integer.toString(j)));
                        } catch (Exception e) {
                            displays.add("N/A");
                        }
                        break;
                }
            }
        }
    }
    
    /**
     * Genera la respuesta html para pasarsela a MyHTTPServer
     * @param allStations
     * @return 
     */
    private static String generateHTMLResponse(boolean allStations) { // metodo que genera la respuesta html 
        String HTMLFile;
        String HTMLCode = "";
        String HTMLResponse;

        if (allStations) {
            HTMLFile = "estaciones.html";
        } else {
            HTMLFile = "estacion.html";
        }

        File file = new File(HTMLFile);
        if (file.exists()) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;

                while ((line = br.readLine()) != null) {
                    HTMLCode += line;
                }

            } catch (Exception e) {
                return "Error al generar la respuesta de las estaciones";
            }
        }
        HTMLResponse = addStationResponse(HTMLCode, allStations);
        
        return HTMLResponse;
    }
    
    private static String addStationResponse(String HTTPResponse, boolean allStations) {
        String HTMLStationResponse = HTTPResponse;
        
        if (allStations) {
           HTMLStationResponse = HTMLStationResponse.replace("*temperatura1*", temperaturas.get(0));
           HTMLStationResponse = HTMLStationResponse.replace("*luminosidad1*", luminosidades.get(0));
           HTMLStationResponse = HTMLStationResponse.replace("*humedad1*", humedades.get(0));
           HTMLStationResponse = HTMLStationResponse.replace("*display1*", displays.get(0));
           HTMLStationResponse = HTMLStationResponse.replace("*temperatura2*", temperaturas.get(1));
           HTMLStationResponse = HTMLStationResponse.replace("*luminosidad2*", luminosidades.get(1));
           HTMLStationResponse = HTMLStationResponse.replace("*humedad2*", humedades.get(1));
           HTMLStationResponse = HTMLStationResponse.replace("*display2*", displays.get(1));
        } else {
           HTMLStationResponse = HTMLStationResponse.replace("*nombre*", parsedPetition[1]);
           HTMLStationResponse = HTMLStationResponse.replace("*temperatura*", temperaturas.get(0));
           HTMLStationResponse = HTMLStationResponse.replace("*luminosidad*", luminosidades.get(0));
           HTMLStationResponse = HTMLStationResponse.replace("*humedad*", humedades.get(0));
           HTMLStationResponse = HTMLStationResponse.replace("*display*", displays.get(0));       
        }
        
        return HTMLStationResponse;
    }
}
