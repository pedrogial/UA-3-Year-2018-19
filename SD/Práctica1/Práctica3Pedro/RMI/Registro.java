import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.*;
import java.io.*;

public class Registro extends UnicastRemoteObject implements InterfazRegistra, Serializable{
    @SuppressWarnings("deprecation")

    Registry registry;

    public boolean registerRMI(InterfazRemoto estacion) throws RemoteException{
        
        try{
            String nombre;
            System.out.println(estacion.getId());
            System.setSecurityManager(new RMISecurityManager());
            nombre = "estacion" + estacion.getId();
            registry.rebind("/" + nombre, estacion);
        }catch(RemoteException e){
            System.err.println(e + "\n");
            e.printStackTrace(); 
            return false;
        }
        return true;
    }

    public boolean unregisterRMI(InterfazRemoto estacion) throws RemoteException{
        
        try{
            String nombre;
            System.out.println(estacion.getId());
            System.setSecurityManager(new RMISecurityManager());
            nombre = "estacion" + estacion.getId();
            registry.unbind("/" + nombre);
        }catch(RemoteException e){
            System.err.println(e + "\n");
            e.printStackTrace(); 
            return false;
        
        }catch(NotBoundException e){
            System.err.println(e + "\n");
            e.printStackTrace(); 
            return false;
        }
        return true;
    }

	public Registro(Registry registry) throws RemoteException{
		super();
		this.registry = registry;
	}	


	public static void main(String[] args) throws Exception{
        String URLRegistro;
        int num;
        try{
            //1.Creo el registrador y lo añado
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            System.setSecurityManager(new RMISecurityManager());
            registry.rebind("/Registra", new Registro(registry));
            
        }catch (Exception ex)            
        {                  
            System.out.println(ex);
            ex.printStackTrace();            
        }
    }
}

//java.rmi.server.hostname