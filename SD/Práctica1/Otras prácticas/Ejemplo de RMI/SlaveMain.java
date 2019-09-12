package com.autentia.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SlaveMain {

	public static void main(String[] args) throws Exception {
		final String remoteHost = args[0];
		final String slaveRmiName = args[1];
		
		final Registry registry = LocateRegistry.getRegistry(remoteHost, Registry.REGISTRY_PORT);
		final Slave slave = new Slave(slaveRmiName);

		System.out.println("Try to register " + slave.getRmiName()
				+ " in remote Registry (only works if Registry was created in the same host).");
		try {
			registry.rebind(slave.getRmiName(), slave);

		} catch (RemoteException e) {
			// No puedes hacer bind, rebind, or unbind de un objeto remoto
			// en un Registry que ha sido creado en otro host.
			e.printStackTrace();
		}

		System.out.println("Register " + slave.getRmiName() + " through the Master.");
		final MasterServices master = (MasterServices)registry.lookup(Master.RMI_NAME);
		master.registerSlave(slave);
	}
}
