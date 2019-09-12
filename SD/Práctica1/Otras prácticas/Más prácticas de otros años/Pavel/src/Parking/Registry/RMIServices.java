package Parking.Registry;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by pavel on 17/10/16.
 * General RMI Interface
 */
public interface RMIServices extends Remote {
    int volumen() throws RemoteException;
    String fecha() throws RemoteException;
    String ultimafecha() throws RemoteException;
    int luz() throws RemoteException;
    void setluz(int value) throws RemoteException;
}
