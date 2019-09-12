/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiregistry;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Miguel
 */
public class RMIRegistry {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(20000);
        } catch (Exception e) {
            System.out.println("Error al crear el registro de estaciones.");
        }
        for (;;) {
                
                
        }
    }
    
}
