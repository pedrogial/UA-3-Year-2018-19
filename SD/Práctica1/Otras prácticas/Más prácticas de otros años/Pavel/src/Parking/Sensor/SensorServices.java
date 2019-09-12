package Parking.Sensor;

import Parking.Registry.RMIServices;

import java.rmi.RemoteException;

/**
 * Created by pavel on 21/10/16.
 * Interfaz para la clase que busca el registrador y pide que lo reigstre
 */
public interface SensorServices extends RMIServices {
    String getRMIName() throws RemoteException;
}
