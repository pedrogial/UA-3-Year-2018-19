package com.autentia.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {

	public static void main(String[] args) throws Exception {
		final String remoteHost = args[0];
		
		final Registry registry = LocateRegistry.getRegistry(remoteHost, Registry.REGISTRY_PORT);
		final String[] remoteObjNames = registry.list();
		
		for (String remoteObjName : remoteObjNames) {
			Object obj = registry.lookup(remoteObjName);
			
			if (obj instanceof ServerServices) {
				System.out.println("Calling remote object: " + remoteObjName);
				final ServerServices server = (ServerServices)obj;
				server.sayHelloWorld();
			}
		}
	}
}
