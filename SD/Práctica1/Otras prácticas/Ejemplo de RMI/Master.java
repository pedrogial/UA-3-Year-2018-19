package com.autentia.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Master extends UnicastRemoteObject implements MasterServices {

	private static final long serialVersionUID = 1L;

	static final String RMI_NAME = Master.class.getSimpleName();

	private final Registry registry;

	protected Master(Registry registry) throws RemoteException {
		super();
		this.registry = registry;
	}

	public void registerSlave(SlaveServices slave) throws RemoteException {
		String rmiName;
		try {
			rmiName = slave.getRmiName();
			registry.rebind(rmiName, slave);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println("Registered: " + rmiName);
	}

	public void sayHelloWorld() throws RemoteException {
		System.out.println("I'm de Master, and I'm unique !!!");
	}
}
