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
    public void setLum(int a) throws java.rmi.RemoteException;
    public void setTemp(int a) throws java.rmi.RemoteException;
    public void setHum(int a) throws java.rmi.RemoteException;
    public void setLCD(String a) throws java.rmi.RemoteException, Exception;
    public int getLum() throws java.rmi.RemoteException;
    public int getTemp() throws java.rmi.RemoteException;
    public int getHum() throws java.rmi.RemoteException;
    public int getId() throws java.rmi.RemoteException;
    public char[] getLCD() throws java.rmi.RemoteException;
    public void setData(String a) throws java.rmi.RemoteException;
    public void getData(String a) throws java.rmi.RemoteException, Exception;
}
