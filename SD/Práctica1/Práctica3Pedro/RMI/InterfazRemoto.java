import java.rmi.Remote;


public interface InterfazRemoto extends Remote {
	public int getId() throws java.rmi.RemoteException;
	public void getFich(String f) throws java.rmi.RemoteException;
	public int getTem() throws java.rmi.RemoteException;
	public int getHum() throws java.rmi.RemoteException;
	public int getLum() throws java.rmi.RemoteException;
	public String getPan() throws java.rmi.RemoteException;
	public void setFich(String f) throws java.rmi.RemoteException;
	public void setTem(int t) throws java.rmi.RemoteException;
	public void setHum(int h) throws java.rmi.RemoteException;
	public void setLum(int l) throws java.rmi.RemoteException;
	public void setPan(String p) throws java.rmi.RemoteException;
	
}