/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmistation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author miguel
 */
public class StationTwoRemote extends UnicastRemoteObject implements RMIConnector.RMIConnector{
    StationControl stationControl;
    
    public StationTwoRemote(StationControl stationControl) throws RemoteException {
        this.stationControl = stationControl;
    }
    
    @Override
    public String getTemperatura() throws RemoteException {
        return stationControl.getTemperatura();
    }

    @Override
    public String getHumedad() throws RemoteException {
        return stationControl.getHumedad();
    }

    @Override
    public String getLuminosidad() throws RemoteException {
        return stationControl.getLuminosidad();
    }
    
    @Override
    public String get(String param) throws RemoteException {
        switch (param) {
            case "temperatura":
                return stationControl.getTemperatura();
            case "luminosidad":
                return stationControl.getLuminosidad();
            case "humedad":
                return stationControl.getHumedad();
            case "display":
                return stationControl.getDisplay();
        }
        return "parametro no valido";
    }

    @Override
    public String setDisplay(String newDisplayMessage) throws RemoteException {
        stationControl.setDisplay(newDisplayMessage);
        return stationControl.getDisplay();
    }
}
