package Parking.Registry;

import Parking.MyHTTPServer.MyHTTPSettings;
import Parking.Sensor.SensorServices;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by pavel on 21/10/16.
 * Register's implementation
 */
public class Register extends UnicastRemoteObject implements RegisterServices {

    private static final long serialVersionUID = 1L;
    public static final String RMI_NAME = Register.class.getSimpleName();
    private final Registry registry;

    private Register(Registry registry) throws RemoteException {
        super();
        this.registry = registry;
    }

    @Override
    public void registerSensor(SensorServices sensor) throws RemoteException {
        try {
            registry.rebind(sensor.getRMIName(), sensor);
            System.out.println("Registered: " + sensor.getRMIName());
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void unregisterSensor(SensorServices sensor) throws RemoteException {
        try {
            registry.unbind(sensor.getRMIName());
            System.out.println("Unregistered: " + sensor.getRMIName());
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int volumen() throws RemoteException {
        return 0;
    }

    @Override
    public String fecha() throws RemoteException {
        return "Hola Register";
    }

    @Override
    public String ultimafecha() throws RemoteException {
        return "Adiós Register";
    }

    @Override
    public int luz() throws RemoteException {
        return 0;
    }

    @Override
    public void setluz(int value) throws RemoteException {

    }

    public static void main(String[] args) throws Exception {
        if (args.length >= 2) {
            final Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            Register master = new Register(registry);
            registry.rebind(Register.RMI_NAME, master);
            System.out.println("Register OK -> " + args[0] + ":" + args[1]);
        } else System.out.println("Wrong args: <IP> <PORT>");
    }
}
