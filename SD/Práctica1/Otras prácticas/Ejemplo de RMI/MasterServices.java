package com.autentia.rmi;

import java.rmi.RemoteException;

/**
 * Define que pueden hacer los nodos Maseter.
 */
public interface MasterServices extends ServerServices {

	public void registerSlave(SlaveServices slave) throws RemoteException;
}
