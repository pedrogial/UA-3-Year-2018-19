/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Station;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author alejandro
 */
@SuppressWarnings("serial")
public class Station extends UnicastRemoteObject implements Serializable, Interfaz{
    private int temp;
    private int lum;
    private int hum;
	private String LCD;

	public Station() throws RemoteException {
        super();//Inicializará todas las variables necesarias de las clases que implementa
        try{
			getData("./Estacion.txt");
            if(this.temp==0 && this.lum==0 && this.hum==0){
                    throw new Exception();
            }
        }
        catch(Exception ex){
            this.temp = 30;
            this.lum = 450;
            this.hum = 90;
            String aux="Hola, esta es la practica de SD";
			this.LCD = aux;
			setData("./Estacion.txt");
        }  
	}
    
    
    @Override
    public int getTemp() {
        try {
			getData("./Estacion.txt");
        } catch (Exception ex) {
            System.err.println("No se ha podido leer desde el fichero: "+ex);
        }
        return temp;
    }

    @Override
    public int getLum() {
        try {
			getData("./Estacion.txt");
        } catch (Exception ex) {
            System.err.println("No se ha podido leer desde el fichero: "+ex);
        }
        return lum;
    }

    @Override
    public int getHum() {
        try {
			getData("./Estacion.txt");
        } catch (Exception ex) {
            System.err.println("No se ha podido leer desde el fichero: "+ex);
        }
        return hum;
    }

    @Override
	public String getLCD() {
        try {
			getData("./Estacion.txt");
        } catch (Exception ex) {
            System.err.println("No se ha podido leer desde el fichero: "+ex);
        }
        return LCD;
    }

    @Override
	public boolean setTemp(int temp) {
        if(temp>=-30 && temp<=50)
            this.temp = temp;
        else 
            this.temp=30;
		return setData("./Estacion.txt");

    }

    @Override
	public boolean setLum(int lum) {
        if(lum>=0 && lum<=800)
            this.lum = lum;
        else
            this.lum=450;
		return setData("./Estacion.txt");
    }

    @Override
	public boolean setHum(int hum) {
        if(hum>=0 && hum<=100)
            this.hum = hum;
        else
            this.hum=90;
		return setData("./Estacion.txt");
    }
    
    @Override
	public boolean setLCD(String LCD) throws Exception {
        if(LCD.length()<=150){
			this.LCD = LCD;
			return setData("./Estacion.txt");// Lo envia al fichero
        }
        else {
            throw new Exception("El array es demasiado pequeño para actualizar");
        }
    }

    //Se debe indicar a que archivo se desean escribir los datos
    @Override
	public boolean setData(String arch) {
        try {
            File file= new File(arch);
            BufferedWriter bw;
            if(file.exists()){
                bw = new BufferedWriter(new FileWriter(arch));
            }
            else{
                bw = new BufferedWriter(new FileWriter(arch));
            }
            String text;
            text="Temperatura="+temp+"\n"+"Humedad="+hum+"\n"+"Luminosidad="+lum+"\n"+"Pantalla="+String.valueOf(LCD)+"\n";
            bw.write(text);
            bw.close();
        } catch (Exception ex) {
            System.err.println("No se ha podido crear el archivo: "+ex);

        }
		return true;
        
    }
    
    //Para obtener los datos desde el fichero se debe indicar el nombre
    @Override
	public boolean getData(String arch) throws Exception {
        try {
            File archivo= new File(arch);
            try (FileReader file = new FileReader(archivo)) {
                BufferedReader br1= new BufferedReader(file);
                String lect=br1.readLine();
                while(lect!=null){
                    String aux[]=lect.split("=");
                    if(!aux[1].isEmpty()){
                        if(lect.contains("Temperatura=")){
                           setTemp(Integer.parseInt(aux[1]));
                        }
                        else if(lect.contains("Humedad=")){
                            setHum(Integer.parseInt(aux[1]));
                        }
                        else if(lect.contains("Luminosidad=")){
                            setLum(Integer.parseInt(aux[1]));
                        }
                        else if(lect.contains("Pantalla=")){
                            setLCD(aux[1]);
                        }
                    }
                    lect =br1.readLine();
                }
                br1.close();
                file.close();
            }
        } catch (Exception ex) {
            System.err.println("No se ha podido leer el fichero: "+ex);
            throw new Exception("No se ha podido leer el fichero");
        } 
		return true;
    }

	public static void main(String[] args) {
		try {
			Station s = new Station();
			System.out.println("Funciona");
			System.in.read();
			System.out.println("No ha esperado");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
