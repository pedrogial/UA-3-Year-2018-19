package com.autentia.rmi;

import java.rmi.RemoteException;

/**
 * Define que pueden hacer los nodos Slave.
 */
public interface SlaveServices extends ServerServices {

	public String getRmiName() throws RemoteException;
}
