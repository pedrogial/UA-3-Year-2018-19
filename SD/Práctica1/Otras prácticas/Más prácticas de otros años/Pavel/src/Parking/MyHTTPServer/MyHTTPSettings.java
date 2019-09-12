package Parking.MyHTTPServer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Class for configuring and initializing server
 * Created by pavel on 29/09/16.
 */
@SuppressWarnings("CanBeFinal")
public class MyHTTPSettings {
    public int MAX_SERVER_CONNECTIONS;
    public int SERVER_PORT;
    public int CONTROLLER_PORT;
    public int REGISTRY_PORT;
    public String CONTROLLER_IP;
    public String REGISTRY_IP;
    public String DYNAMIC_CONTENT_KEYWORD;
    public String DEFAULT_INDEX_PAGE;
    public String SENSOR_KEYWORD;

    /**
     * Reads the file <b>server-config.json</b> and assigns its values to the vars wich name matches with the JSON's element key.
     * @return The object filled with the readen values.
     * @throws FileNotFoundException
     */
    public static MyHTTPSettings readConfig() throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("server-config.json"));
        return gson.fromJson(reader, MyHTTPSettings.class); //Fills all the class properties with the matched json's keys
    }

    @Override
    public String toString() {
        return "MyHTTPSettings{" +
                "MAX_SERVER_CONNECTIONS=" + MAX_SERVER_CONNECTIONS +
                ", SERVER_PORT=" + SERVER_PORT +
                ", CONTROLLER_PORT=" + CONTROLLER_PORT +
                ", CONTROLLER_IP='" + CONTROLLER_IP + '\'' +
                ", DYNAMIC_CONTENT_KEYWORD='" + DYNAMIC_CONTENT_KEYWORD + '\'' +
                ", DEFAULT_INDEX_PAGE='" + DEFAULT_INDEX_PAGE + '\'' +
                ", SENSOR_KEYWORD='" + SENSOR_KEYWORD + '\'' +
                '}';
    }
}
