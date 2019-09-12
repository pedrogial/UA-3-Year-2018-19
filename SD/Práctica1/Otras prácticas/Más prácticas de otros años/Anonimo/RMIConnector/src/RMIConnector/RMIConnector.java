/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMIConnector;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author miguel
 */
public interface RMIConnector extends Remote {
    public String getTemperatura() throws RemoteException;
    public String getHumedad() throws RemoteException;
    public String getLuminosidad() throws RemoteException;
    public String get(String param) throws RemoteException;
    public String setDisplay(String newDisplayMessage) throws RemoteException;
}
