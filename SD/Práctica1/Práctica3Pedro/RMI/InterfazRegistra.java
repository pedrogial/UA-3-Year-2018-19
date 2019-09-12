import java.rmi.Remote;


public interface InterfazRegistra extends Remote{
	public boolean registerRMI(InterfazRemoto estacion) throws java.rmi.RemoteException;
	public boolean unregisterRMI(InterfazRemoto estacion) throws java.rmi.RemoteException;
}
