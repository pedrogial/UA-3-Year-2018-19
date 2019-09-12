package com.autentia.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Define que puede hacer cualquier nodo del servidor.
 */
public interface ServerServices extends Remote {

	public void sayHelloWorld() throws RemoteException;
}
