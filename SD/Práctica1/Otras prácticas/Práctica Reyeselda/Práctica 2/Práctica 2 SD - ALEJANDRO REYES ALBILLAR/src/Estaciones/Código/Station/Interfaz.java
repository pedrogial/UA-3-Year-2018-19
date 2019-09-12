/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Station;

import java.rmi.Remote;

/**
 *
 * @author alejandro
 */
public interface Interfaz extends Remote{
	public boolean setLum(int a) throws java.rmi.RemoteException;

	public boolean setTemp(int a) throws java.rmi.RemoteException;

	public boolean setHum(int a) throws java.rmi.RemoteException;

	public boolean setLCD(String a) throws java.rmi.RemoteException, Exception;
    public int getLum() throws java.rmi.RemoteException;
    public int getTemp() throws java.rmi.RemoteException;
    public int getHum() throws java.rmi.RemoteException;
	public String getLCD() throws java.rmi.RemoteException;
	public boolean setData(String a) throws java.rmi.RemoteException;
	public boolean getData(String a) throws java.rmi.RemoteException, Exception;
}
