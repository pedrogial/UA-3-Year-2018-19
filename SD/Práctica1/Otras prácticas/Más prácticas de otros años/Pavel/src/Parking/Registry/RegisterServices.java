package Parking.Registry;

import Parking.Sensor.SensorServices;

import java.rmi.RemoteException;

/**
 * Created by pavel on 21/10/16.
 * Interfaz para la clase registradora de sensores
 */
public interface RegisterServices extends RMIServices {
    void registerSensor(SensorServices sensor) throws RemoteException;
    void unregisterSensor(SensorServices sensor) throws RemoteException;
}
