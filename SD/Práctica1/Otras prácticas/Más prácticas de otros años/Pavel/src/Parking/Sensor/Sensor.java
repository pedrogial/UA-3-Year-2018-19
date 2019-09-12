package Parking.Sensor;

import Parking.Registry.Register;
import Parking.Registry.RegisterServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by pavel on 21/10/16.
 * Sensor's implementation
 */
public class Sensor extends UnicastRemoteObject implements SensorServices {
    private static final long serialVersionUID = 1L;
    private final String RMIName;
    private static RegisterServices registerServices;
    private static Registry registry;
    private static Sensor sensor;

    @SuppressWarnings("unused")
    public int Volumen, Led;
    public String UltimaFecha;

    Sensor(String fileName) throws RemoteException {
        this.RMIName = fileName.substring(0, fileName.lastIndexOf('.'));
        try {
            String s = new String(Files.readAllBytes(Paths.get(fileName)));
            readFile(fileName);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int volumen() throws RemoteException {
        try {
            readFile(RMIName + ".txt");
        } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return Volumen;
    }

    @Override
    public String fecha() throws RemoteException {
        try {
            readFile(RMIName + ".txt");
        } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return UltimaFecha;
    }

    @Override
    public String ultimafecha() throws RemoteException {
        UltimaFecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        saveFile();
        return UltimaFecha;
    }

    @Override
    public int luz() throws RemoteException {
        try {
            readFile(RMIName + ".txt");
        } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return Led;
    }

    @Override
    public void setluz(int value) throws RemoteException {
        Led = value;
        saveFile();
    }

    @Override
    public String getRMIName() throws RemoteException {
        return RMIName;
    }

    private void readFile(String fileName) throws IOException, NoSuchFieldException, IllegalAccessException {
        for (String a : Files.readAllLines(Paths.get(fileName))) {
            String[] campos = a.split("=");
            Field f = this.getClass().getDeclaredField(campos[0]);
            if (f.getType().isAssignableFrom(String.class)) f.set(this, campos[1]);
            else if (f.getType().isAssignableFrom(int.class)) f.set(this, Integer.parseInt(campos[1]));
        }
    }

    private void saveFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(RMIName + ".txt"))) {
            for (Field f : this.getClass().getDeclaredFields())
                if (!Modifier.isPublic(f.getModifiers())) bw.write(f.getName() + "=" + f.get(this) + System.lineSeparator());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "RMIName='" + RMIName + '\'' +
                ", Volumen=" + Volumen +
                ", Led=" + Led +
                ", UltimaFecha='" + UltimaFecha + '\'' +
                '}';
    }

    public static void main(String[] args) throws Exception {
        if (args.length >= 3) {
            registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            try {
                System.out.println("Before reading");
                sensor = new Sensor(args[2]);
                System.out.println("Binding " + sensor.getRMIName() + " through " + args[0] + ":" + args[1]);
                registerServices = (RegisterServices) registry.lookup(Register.RMI_NAME);
                registerServices.registerSensor(sensor);
                System.out.print("Press ENTER to disconnect ");
                new BufferedReader(new InputStreamReader(System.in)).readLine();
                System.out.println("Unregistering...");
                registerServices.unregisterSensor(sensor);
                System.out.println("Success");
                System.exit(0);
            } catch (IOException e) {
                System.err.println("Error reading file");
            }
        } else System.out.println("Wrong args: <IP> <PORT> <FILENAME>");
        System.exit(0);
    }
}