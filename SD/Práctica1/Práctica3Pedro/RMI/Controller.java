import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Controller {

    //Lee lo que le pasa el cliente
    public String leer(Socket cliente){
        String datos = "";
		try{
			InputStream aux = cliente.getInputStream();
			DataInputStream flujo = new DataInputStream(aux);
            datos = flujo.readUTF();
            
            //System.out.println("Lee " + datos);
		}catch (Exception e){
			System.out.println("Error en la lectura del socket: " + e.toString());
		}
      return datos;
    }
    
    //Escribe lo que le pasa el cliente
    public void escribir(Socket cliente, String datos){
		try{
			OutputStream aux = cliente.getOutputStream();
			DataOutputStream flujo= new DataOutputStream(aux);
			flujo.writeUTF(datos);
		}catch (Exception e){
			System.out.println("Error al escribir: " + e.toString());
		}
	}

    public String operacion(String host, String puerto, String dato){
        InterfazRemoto objetoRemoto = null;
        Boolean es = false;
        String resultado = "";
        int estacion = 0;
        String tipo = "";
        String titulo = "";
        String medio = "";
        String set = "";
        
        System.out.println("Operacion: " + dato);

        //Solo entra si se trata de lo siguiente
        if(dato.contains("Station=") || dato.contains("station=") || dato.contains("estaciones") || dato.contains("todo")){

            String[] cadena = dato.split("/");
            dato = cadena[1];
            if(!dato.contains("estaciones")){
                //Cojemos los datos necesarios
                int s = 0;
                for(int i = 0; i < dato.length() && !es; i++){
                    if(dato.charAt(i) == '='){
                        s += 1;
                    }
                }
                try{
                    if(s == 2){
                        for(int i = 0; i < dato.length() && !es; i++){
                            tipo += dato.charAt(i);
                            if(dato.charAt(i+1) == '='){
                                dato = dato.substring(i+1);
                                for(int j = 1; j < dato.length() && !es; j++){
                                    set += dato.charAt(j);
                                    if(dato.charAt(j+1) == '?'){
                                        dato = dato.substring(j+1);
                                        es = true;
                                    }
                                }
                            }
                        }
                    }
                    else{
                        for(int i = 0; i < dato.length() && !es; i++){
                            if(dato.charAt(i+1) == '?'){
                                es = true;
                            }
                            tipo += dato.charAt(i);
                        }
                    }
                }catch(Exception ex){
                    resultado = "error2";
                    System.err.println("Ha habido un problema al acceder a los datos del objeto: " + ex);
                }
                System.out.println("set: " + set);
                
                //La estacion
                for(int i = 0; i < dato.length(); i++){
                    if(dato.charAt(i) == '='){
                        estacion = Integer.parseInt(dato.substring(i + 1));
                    }
                }
                //Me conecto con el RMI
                System.out.println("Los datos son: " + tipo +" y la estacion " +estacion);
                String servidor = "/estacion" + estacion;
                
                try{
                    Registry registry = LocateRegistry.getRegistry(host, Integer.parseInt(puerto));
                    System.setSecurityManager(new RMISecurityManager());

                    objetoRemoto = (InterfazRemoto) registry.lookup(servidor);
                    //Para el set
                    if(set != ""){
                        set = set.replaceAll("%20", " ");
                        try{
                            switch(tipo){
                                case "temperatura":
                                        objetoRemoto.setTem(Integer.parseInt(set));
                                break;
                                case "humedad":
                                        objetoRemoto.setHum(Integer.parseInt(set));
                                break;
                                case "luminosidad":
                                        objetoRemoto.setLum(Integer.parseInt(set));
                                break;
                                case "pantalla":
                                        objetoRemoto.setPan(set);
                                break;
                                default:
                                    resultado = "error2";
                                    break;
                            }
                        }catch(Exception ex){
                            resultado = "error2";
                            System.err.println("Ha habido un problema al acceder a los datos del objeto: " + ex);
                        }
                    }
                    //Para imprimir todo
                    if(tipo.equals("todo")){
                        titulo =  "<title>Estación " + objetoRemoto.getId() + "</title> \n";     
                        medio = "<br>Temperatura: " + objetoRemoto.getTem() + "</br> \n"
                                + "<br>Humedad: " + objetoRemoto.getHum() + "</br> \n"
                                + "<br>Luminosidad: " + objetoRemoto.getLum() + "</br> \n"
                                + "<br>Pantalla: " + objetoRemoto.getPan() + "</br> \n";
                    }
                    else{
                        try{
                            //Para devolver
                            switch(tipo){
                                case "temperatura":
                                    
                                        titulo =  "<title>Estación " + objetoRemoto.getId() + "</title> \n";     
                                        medio = "<h2>Temperatura: " + objetoRemoto.getTem() + "</h2> \n";
                                    
                                    break;
                                case "humedad":
                                
                                        titulo =  "<title>Estación " + objetoRemoto.getId() + "</title> \n";     
                                        medio = "<h2>Humedad: " + objetoRemoto.getHum() + "</h2> \n";
                                
                                    break;
                                case "luminosidad":
                                    
                                        titulo =  "<title>Estación " + objetoRemoto.getId() + "</title> \n";     
                                        medio = "<h2>Luminosidad: " + objetoRemoto.getLum() + "</h2> \n";
                                
                                    break;
                                case "pantalla":
                                    
                                        titulo =  "<title>Estación " + objetoRemoto.getId() + "</title> \n";     
                                        medio = "<h2>Pantalla: " + objetoRemoto.getPan() + "</h2> \n";
                                    
                                    break;
                                default:
                                    resultado = "error2";
                                    //System.out.println("Paso por aqui");
                                    break;
                            }
                        }catch(Exception ex){
                            resultado = "error1";
                            System.err.println("Ha habido un problema al acceder a los datos del objeto: " + ex);
                        }
                    }
                }catch(Exception ex){
                    System.out.println("Error al instanciar el objeto remoto: " + ex);
                    ex.printStackTrace();
                    resultado = "error1";
                }
            }
            //Lista de estaciones:
            if(dato.equals("estaciones")){
                try{
                    String servidor = "rmi://" + host + ":" + puerto;
                    String estaciones[];
                    System.setSecurityManager(new RMISecurityManager());
                    estaciones = Naming.list(servidor);

                    titulo =  "<title>Estaciones"  + "</title> \n";  
                    for (int i = 0; i < estaciones.length; i++) {
                        StringTokenizer s = new StringTokenizer(estaciones[i], "/");
                        s.nextToken();
                        estaciones[i] = s.nextToken();
                        if(!estaciones[i].contains("Registra")){
                            StringTokenizer d = new StringTokenizer(estaciones[i], "estacion");
                            String id = d.nextToken();
                            medio = medio + "<li><a href=\"/controladorSD/todo?Station="+ id +"\">" + estaciones[i] + "</a>&nbsp;</li> \n\n";
                        }
                    }
                }catch(Exception ex){
                    System.out.println("Error al instanciar el objeto remoto: " + ex);
                    resultado = "error1";
                }
                
            }
            //Devolver las paginas
            if(resultado != "error1" && resultado != "error2"){
                resultado = "<!DOCTYPE html> \n" 
                            + "<html> \n"
                            + "<head> \n"
                            + "<meta charset=\"utf-8\"> \n"
                            + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n"
                            + titulo
                            + "<meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\"> \n"
                            + "</head> \n"
                            + "<body> \n"
                            + "<h1><span style=\"text-decoration: underline;\">Datos</span></h1> \n"
                            + medio
                            + "<h2>Opciones:</h2> \n"
                            + "<ol> \n"
                            + "<li><a href=\"/controladorSD/estaciones\">Estaciones.</a>&nbsp;</li> \n"
                            + "<li><a href=\"/index\">Inicio.</a>&nbsp;</li> \n"
                            + "</ol> \n"
                            + "</body> \n"
                            + "</html>";
            }
        }else{
                resultado = "error2";
        }
        //Si no es una variable válida ni existe la estacion devolvemos estos errores
        if(resultado == "error2"){
            resultado = "<!DOCTYPE html> \n" 
                            + "<html> \n"
                            + "<head> \n"
                            + "<meta charset=\"utf-8\"> \n"
                            + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n"
                            + "<title>Estación " + resultado + "</title> \n"
                            + "<meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\"> \n"
                            + "</head> \n"
                            + "<body> \n"
                            + "<h1><span style=\"text-decoration: underline;\">Variable no valida</span></h1> \n"
                            + "<h2>Opciones:</h2> \n"
                            + "<ol> \n"
                            + "<li><a href=\"/controladorSD/estaciones\">Estaciones.</a>&nbsp;</li> \n"
                            + "<li><a href=\"/index\">Inicio.</a>&nbsp;</li> \n"
                            + "</ol> \n"
                            + "</body> \n"
                            + "</html>";
        }
        else if(resultado == "error1"){
            resultado = "<!DOCTYPE html> \n" 
                            + "<html> \n"
                            + "<head> \n"
                            + "<meta charset=\"utf-8\"> \n"
                            + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n"
                            + "<title>Estación " + resultado + "</title> \n"
                            + "<meta name=\"description\" content=\"Un centro para el control de estaciones meteorológicas\"> \n"
                            + "</head> \n"
                            + "<body> \n"
                            + "<h1><span style=\"text-decoration: underline;\">La estación no existe</span></h1> \n"
                            + "<h2>Opciones:</h2> \n"
                            + "<ol> \n"
                            + "<li><a href=\"/controladorSD/estaciones\">Estaciones.</a>&nbsp;</li> \n"
                            + "<li><a href=\"/index\">Inicio.</a>&nbsp;</li> \n"
                            + "</ol> \n"
                            + "</body> \n"
                            + "</html>";
        }

        System.out.println("Resultado: " + resultado);
        return resultado;
    }

    public static void main(String[] args){
        String host;
        String puerto;
        String puertoHTTP;
        String dato = "";
        Controller c = new Controller();

        if(args.length == 3){

            host = args[0];
            puerto = args[1];
            puertoHTTP = args[2];
            try{
                ServerSocket s = new ServerSocket(Integer.parseInt(puertoHTTP));
                System.out.println("Escuchando en el puerto: " + puerto);
                //Bucle para recoger cosas del HiloServidor
                while(true){
                    Socket cliente = s.accept();
                    System.out.println("Sirvo al cliente...");
                    dato = c.leer(cliente);
                    //System.out.println("Leemos: " + dato);
                    dato = c.operacion(host, puerto, dato);
                    c.escribir(cliente, dato);
                    cliente.close();
                }
            }catch(Exception ex){
                System.out.println("Error: " + ex.toString());
            }
        }
        else{
            System.out.println("Error: mal argumentos");
        }
    }

}