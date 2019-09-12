package com.autentia.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Slave extends UnicastRemoteObject implements SlaveServices {

	private static final long serialVersionUID = 1L;

	private final String rmiName;

	protected Slave(String rmiName) throws RemoteException {
		super();
		this.rmiName = rmiName;
	}

	public void sayHelloWorld() throws RemoteException {
		System.out.println("I'm the slave " + getRmiName() + ", and I say: Hellow world !!!");
	}

	public String getRmiName() throws RemoteException {
		return rmiName;
	}
}
