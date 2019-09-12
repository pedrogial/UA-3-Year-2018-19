import java.net.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;



public class HiloServidor extends Thread {

	private Socket cliente;
	private String host;
	private int puerto;
	private Socket controlador;
	
	public HiloServidor(Socket cliente, String host, int puerto)
	{
		this.cliente = cliente;
		this.puerto = puerto;
		this.host = host;
	}
	
	//Leer
	public String leeSocket (Socket cliente, String p_Datos){
		String datos = "";
		try{
			InputStream aux = cliente.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(aux));
			while (in.ready()) {
                datos = datos.concat(in.readLine() + "\n");
                //System.out.println(datos); //Para comprobar que es lo que lee
            }
		}
		catch (Exception e){
			System.out.println("Error en leer: " + e.toString());
		}
		return datos;
	}

	//Escribir
	public void escribeSocket (Socket p_sk, String p_Datos)
	{
		try
		{
			OutputStream aux = p_sk.getOutputStream();
			DataOutputStream flujo= new DataOutputStream( aux );
			flujo.writeUTF(p_Datos);			
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		return;
	}
	
	//Para responder al cliente
	public void responder(Socket cliente, String dato){
		String contenido = "";
		String codigo = "200";
		//Si se trata de index o de / entonces lo siguiente
		if(dato.equals("/") || dato.contains("index")){
			File file = new File("./index.html");
			if (file.exists()) {
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String line;

                    while ((line = br.readLine()) != null) {
                        contenido += line;
                    }

                } catch (Exception e) {
                    codigo = "500";
                    contenido = "500 ERROR interno, no se ha podido leer el fichero";
                }
            } else {
                codigo = "404";
                contenido = "error";
            }
		}
		//Si no es nada de esto entonces devolvemos error.
		if(!dato.contains("favicon.ico") && !dato.contains("controladorSD") && !dato.equals("/") && !dato.contains("index")){
			contenido = "error";
		}
		//si lleva controladorSD se le manda al controlador
		if(dato.contains("controladorSD")){
			System.out.println(dato);
			try {
				controlador = new Socket(host, puerto);
			} catch (Exception ex) {
				System.err.println("No se ha podido conectar con el controlador: " + ex);
			}
			System.out.println("Escribo al controlador:" + dato);
			escribeSocket(controlador, dato);
			dato = "";
			contenido = "";
			while (dato.isEmpty()) {
				dato = leeSocket(controlador, dato);
			}
			//System.out.println("Contenido " + dato);
			dato = dato.substring(2);// Bug del Socket me aparecen dos caracteres raros.
			contenido = dato;			
		}
		//Si es error entonces abrirá el fichero de error.
		if(contenido.equals("error")){
			System.out.println("Entra a error: " + contenido);
			File file = new File("./error.html");
			if (file.exists()) {
				try {
					contenido = "";
					FileReader fr = new FileReader(file);						
					BufferedReader br = new BufferedReader(fr);

					String line;

					while ((line = br.readLine()) != null) {
						contenido += line;
					}
				} catch (Exception e) {
					codigo = "500";
					contenido = "500 ERROR interno, no se ha podido leer el fichero";
				}
			} else {
				codigo = "404";
				contenido = "ERROR 404: Not found";
			}
		}
		//Lo necesario para imprimir en HTML
		try {
            OutputStream o = cliente.getOutputStream();
            DataOutputStream datos = new DataOutputStream(o);
			datos.writeUTF("HTTP/1.0 " + codigo + "OK \n Server : MyHTTPServer/1.0 \n Content-Type : text/HTML \n Content-Length : " + contenido.length() + "\n\n" + contenido);
            datos.close();
        } catch (Exception ex) {
            System.out.println("ERROR (" + ex.toString() + ") en la escritura del socket");
        }
	}


	@Override
	public void run() {
		
		String Cadena = "";
		Boolean es = false;
		//System.out.println("Paso por el run");
		try {
			//Thread.sleep(10000);
			while(!cliente.isClosed()){
				Cadena = this.leeSocket (cliente, Cadena);
				
				if(!Cadena.isEmpty()){
					StringTokenizer s = new StringTokenizer(Cadena);
					Cadena = s.nextToken();
					//Divido la cadena
					if (Cadena.equals("GET")) {
						Cadena = s.nextToken();
						String[] aux = Cadena.split("/");
						//Si es mayor que 0 
						if (aux.length > 0) {
							Cadena = aux[1];
						}
						//Si contiene controlador SD
						if(Cadena.contains("controladorSD") && aux.length >=3){
							
							Cadena = Cadena + "/" + aux[2];
						}

						//System.out.println("Divido las cosas:" + Cadena);
						responder(cliente, Cadena);
					}
					else{
						//Si no lleva Get entonces devolvemos error
						responder(cliente, "error");
					}
				}
				else{
					try {
						cliente.close();
					} catch (IOException ex) {
						//Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}	
		}catch (Exception e) {
			System.out.println("Error en el run: " + e.toString());
		}

	}
}
