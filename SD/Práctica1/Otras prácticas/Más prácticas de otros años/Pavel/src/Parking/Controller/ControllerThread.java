package Parking.Controller;

import Parking.MyHTTPServer.MyHTTPSettings;
import Parking.MyHTTPServer.SocketUtils;
import Parking.Sensor.SensorServices;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by pavel on 23/10/16.
 * Implements Controller's logic
 */
class ControllerThread extends Thread {
    private final Socket requestSocket;
    private final MyHTTPSettings settings;
    private Registry registry;

    ControllerThread(MyHTTPSettings settings, Socket requestSocket) {
        this.requestSocket = requestSocket;
        this.settings = settings;
    }

    /**
     * Controller's main logic. Receives a query and processes a response:
     * <ol>
     *     <li>If there is no query, it will show all Available Sensors</li>
     *     <li>If not, it will process the query</li>
     * </ol>
     * It also connects to the RMI Register
     */
    @Override
    public void run() {
        try {
            String response;
            registry = LocateRegistry.getRegistry(settings.REGISTRY_IP, settings.REGISTRY_PORT);
            String query = SocketUtils.receiveMessage(requestSocket).replaceAll("\\n", "");
            System.out.println("query = " + query);
            if (query.equals("")) response = sendAvailableSensors();
            else response = processQuery(query);
            SocketUtils.sendMessage(requestSocket, response + "\n");
            requestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sepparates the sensor's name and the resource and asks for it
     * @param query the given query
     * @return the query's response info
     */
    @Nullable
    private String processQuery(String query) {
        StringBuilder response = new StringBuilder();
        String[] queryParts = query.split("\\?");
        if (queryParts.length == 2) {
            String resource = queryParts[0];
            String[] parameters = queryParts[1].split("&");
            for (String parameter : parameters) {
                String[] parameterParts = parameter.split("=");
                if (parameterParts.length == 2) {
                    String key = parameterParts[0];
                    String value = parameterParts[1];
                    if (key.equals(settings.SENSOR_KEYWORD))
                        response.append("<p>[").append(value).append(",").append(resource).append("] = ")
                                .append(getSensorProperty(value, resource)).append("</p>\n");
                }
            }
            response.append("<a href=\"../\">Inicio</a>");
            return response.toString();
        }
        return null;
    }

    /**
     * Looks for a sensor property, invokes it's method with reflexion and returns its value
     * @param sensorName registered sensor's name
     * @param resource property's name (used as the reflective method call). If it's a setter, sepparates the resource's name and its value
     * @return the property's value
     */
    @Nullable
    private String getSensorProperty(String sensorName, String resource) {
        try {
            Object returnedValue, remoteObject = registry.lookup(sensorName);
            if (remoteObject instanceof SensorServices) {
                SensorServices sensor = (SensorServices) remoteObject;
                if (resource.contains("=")) {
                    String[] resourceParts = resource.split("=");
                    int param;
                    try {
                        param = Math.max(0, Integer.parseInt(resourceParts[1]));
                    } catch (NumberFormatException e) { param = 0; }
                    sensor.getClass().getMethod(resourceParts[0], int.class).invoke(sensor, param);
                    returnedValue = param;
                } else returnedValue = sensor.getClass().getMethod(resource).invoke(sensor);
                return returnedValue.toString();
            }
        } catch (NotBoundException e) {
            //No existe el elemento en remoto
            return "No existe el sensor " + sensorName;
        } catch (NoSuchMethodException e) {
            //No existe el recurso solicitado
            return "No existe el recurso " + (resource.contains("=") ? resource.split("=")[0] : resource) + "\n";
        } catch (RemoteException | IllegalAccessException | InvocationTargetException e) {
            return "El método con parámetro " + resource + " ha tenido un error y no se ha podido procesar\n";
        }
        return null;
    }

    /**
     * Looks for the registered sensors and returns them as a list
     * @return the available sensors
     */
    @Nullable
    private String sendAvailableSensors() {
        try {
            StringBuilder names = new StringBuilder();
            names.append("<p>Lista de nombres de sensores disponibles:</p>\n");
            for (String remoteName : registry.list())
                if(registry.lookup(remoteName) instanceof SensorServices) names.append("<p>").append(remoteName).append("</p>\n");
            names.append("<p><a href=\"../\">Inicio</a></p>\n");
            return names.toString();
        } catch (RemoteException | NotBoundException e) {
            return "<h1>El controlador no se ha podido conectar con los sensores</h1>" +
                    "<p><a href=\"../\">Inicio</a></p>\n";
        }
    }
}
