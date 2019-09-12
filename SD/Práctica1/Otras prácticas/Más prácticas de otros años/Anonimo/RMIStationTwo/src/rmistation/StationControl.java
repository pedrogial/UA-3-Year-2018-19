/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmistation;

import RMIConnector.Constants;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 *
 * @author miguel
 */
public class StationControl {

    final static String filePath = "estacion2.txt";
    private static ArrayList<String> texto;
    public static String temperatura, humedad, luminosidad, display;
    StationInterface iface;

    
    public StationControl() throws RemoteException, AlreadyBoundException {
       iface = new StationInterface();
       iface.setVisible(true);
       StationTwoRemote objetoRemoto = new StationTwoRemote(this);
       Registry registry = LocateRegistry.getRegistry("127.0.0.1",20000); 
       registry.bind(Constants.RMIIdTwo, objetoRemoto); // registro la estacion
       System.out.println("Controlador de estacion operativo");
    }
    
    public static void lecturaDelFichero() {
        try {
            leerFichero();
        } catch (FileNotFoundException e) {
            System.out.println("excepcion por no haber encontrado el fichero");
        } catch (IOException e) {
            System.out.println("excepcion por no haber podido leer mas lineas");
        }
    }

    private static void leerFichero() throws IOException {

        texto = new ArrayList<>();
        String textAux = null;

        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);

        while ((textAux = br.readLine()) != null) {
            String[] aux = textAux.split("=");
            texto.add(aux[1]);
        }
        fr.close();
        
        for (int i = 0; i < texto.size(); i++) {
            switch (i) {
                case 0:
                    temperatura = texto.get(i);
                    break;
                case 1:
                    humedad = texto.get(i);
                    break;
                case 2:
                    luminosidad = texto.get(i);
                    break;
                case 3:
                    display = texto.get(i);
                    break;
            }
        }
    }

    public String getTemperatura() {
        return temperatura;
    }

    public String getHumedad() {
        return humedad;
    }

    public String getLuminosidad() {
        return luminosidad;
    }

    public String getDisplay() {
        return display;
    }
    
    public void setDisplay(String newDisplayMessage) {
        display = newDisplayMessage;
        iface.setDisplay();
    }
    
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        StationControl station = new StationControl();
    }
}
