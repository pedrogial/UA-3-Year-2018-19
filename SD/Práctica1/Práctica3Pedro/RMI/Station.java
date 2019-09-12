
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Arrays;
import java.io.Serializable; 
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SuppressWarnings("serial")
public class Station extends UnicastRemoteObject implements InterfazRemoto, Serializable {
    private final int id;
    private int tem;
    private int lum;
    private int hum;
    private String pan;

    public Station (int id) throws RemoteException{
        super();
        this.id = id;
        try{
            getFich("./Station" + id + ".txt");

        }catch(Exception ex){
            System.err.println("Se ha rellenado mal el fichero: " + ex);
        }
    }

    public void leer(String f){
        try{
            File estacion = new File(f);
            System.out.println("Cambio un fichero");
            //Leer el fichero
            BufferedReader br1 = new BufferedReader(new FileReader(estacion));
            //Coger la primera linea.
            String linea = br1.readLine();
            //Dividir.
            while(linea != null){
                String aux[] = linea.split("=");
                String dato = aux[0];
                switch(dato){
                    case ("Temperatura"):
                            tem = Integer.parseInt(aux[1]);
                            System.out.println(dato + ": " + tem);
                        break;
                    case ("Humedad"):
                            hum = Integer.parseInt(aux[1]);
                            System.out.println(dato + ": " + hum);
                        break;
                    case ("Luminosidad"):
                            lum = Integer.parseInt(aux[1]);
                            System.out.println(dato + ": " + lum);
                        break;
                    case ("Pantalla"):
                            pan = aux[1];
                            System.out.println(dato + ": " + pan);
                        break;
                    default:
                        break;
                }
                linea =br1.readLine();
            }
            System.out.println("Cierro el fichero");
            br1.close();
        }catch (Exception ex) {
            System.err.println("No se ha podido leer el fichero: "+ ex);
        }
    }

    @Override
    public void getFich(String f){
        try{
            //Abrir un fichero.
            File estacion = new File(f);
            if(estacion.exists()){
                //System.out.println("Se encontró un fichero:");
                //Leer el fichero
                BufferedReader br1 = new BufferedReader(new FileReader(estacion));
                //Coger la primera linea.
                String linea = br1.readLine();
                //Dividir.
                while(linea != null){
                    String aux[] = linea.split("=");
                    String dato = aux[0];
                    switch(dato){
                        case ("Temperatura"):
                                tem = Integer.parseInt(aux[1]);
                                System.out.println(dato + ": " + tem);
                            break;
                        case ("Humedad"):
                                hum = Integer.parseInt(aux[1]);
                                System.out.println(dato + ": " + hum);
                            break;
                        case ("Luminosidad"):
                                lum = Integer.parseInt(aux[1]);
                                System.out.println(dato + ": " + lum);
                            break;
                        case ("Pantalla"):
                                pan = aux[1];
                                System.out.println(dato + ": " + pan);
                            break;
                        default:
                            break;
                    }
                    linea =br1.readLine();
                }
                System.out.println("Cierro el fichero");
                br1.close();
            }
            else{
                setFich(f);
            }
        }catch (Exception ex) {
            System.err.println("No se ha podido leer el fichero: "+ex);

        }
    }

    @Override
    public void setFich(String f){
        try{
            File estacion= new File(f);
            String datos;
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            datos = "Temperatura=" + getTem() + "\n" +
                    "Humedad=" + getHum() + "\n" +
                    "Luminosidad=" + getLum() + "\n" +
                    "Pantalla=" + getPan() + "\n";
            bw.write(datos);
            bw.close();  
        }catch(Exception ex){
            System.err.println("No se ha podido crear el archivo: " + ex);
        }
    }

    @Override
    public int getId(){
        return id;
    }

    @Override
    public int getTem(){
        if(this.tem == 0){
            this.tem = 30;
        }
        leer("Station" + id + ".txt");
        return tem;
    }

    @Override
    public int getLum() {
        if(this.lum == 0){
            this.lum = 20;
        }
        leer("Station" +  id + ".txt");
        return lum;
    }

    @Override
    public int getHum() {
        if(this.hum == 0){
            this.hum = 50;
        }
        leer("Station" +  id + ".txt");
        return hum;
    }

    @Override
    public String getPan() {
        if(this.pan == null){
            this.pan = "Por defecto";
        }
        leer("Station" + id + ".txt");
        return pan;
    }

    @Override
    public void setTem(int t) {
        if(t >= -30 && t <= 50){
            this.tem = t;
        }
        else{
            this.tem = 30;
        }
        setFich("Station" + id + ".txt");
    }

    @Override
    public void setLum(int l) {
        if(l >= 0 && l <= 800){
            this.lum = l;
        }else{
            this.lum = 450;
        }
        setFich("Station" + id + ".txt");
    }

    @Override
    public void setHum(int h) {
        if(h >= 0 && h <= 100){
            this.hum = h;
        }else{
            this.hum=90;
        }
        setFich("Station" + id + ".txt");    
    }

    @Override
    public void setPan(String p){
        if(p.length() <= 150){
            this.pan = p;
        }
        else {
           this.pan = "Era demasiado pequenyo";
        }
        setFich("Station" + id + ".txt");
    }

    public static void main(String[] args){
        try{
            String remoteHost = args[0]; //IP rmi
            int puerto = Integer.parseInt(args[1]);
            int id = Integer.parseInt(args[2]); //id de la estacion
            Registry registry = LocateRegistry.getRegistry(remoteHost, puerto);
            System.setSecurityManager(new RMISecurityManager());
            InterfazRemoto remoto = new Station(id); //creas la estacion

            InterfazRegistra registra = (InterfazRegistra) registry.lookup("/Registra"); //busco el registrador
            registra.registerRMI(remoto); //Envias la estacion

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    try{
                    Registry registry = LocateRegistry.getRegistry(remoteHost, puerto);
                    System.setSecurityManager(new RMISecurityManager());
                    InterfazRegistra registra = (InterfazRegistra) registry.lookup("/Registra"); //busco el registrador
                    registra.unregisterRMI(remoto);
                    }catch(RemoteException e){
                        System.err.println(e + "\n");
            
                    }catch(NotBoundException e){
                        System.err.println(e + "\n");

                    }
                }
            }, "Shutdown-thread"));
        }catch(Exception ex){
            System.out.println(ex);
        }

    }
}
