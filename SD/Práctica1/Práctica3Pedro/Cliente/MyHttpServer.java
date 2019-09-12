import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MyHttpServer{

		public void Conectar(int puerto, String host, int controller, int max){
		try{
			//int i = 0;
			ServerSocket servidor = new ServerSocket(puerto);
			System.out.println("El puerto es: " + puerto);

			while(true){
				Socket cliente = servidor.accept();
				//Controlo los clientes.
				if(Thread.activeCount() <= max){
					//i++;
					System.out.println("Sirvo al cliente "); //+i
					Thread t = new HiloServidor(cliente, host, controller);
					t.start();
					//i--;
				}	
			}
		} catch (Exception ex) {
			System.err.println("No se ha podido abrir el servidor. " + ex);
		}
	}

	public static void main(String[] a){
		MyHttpServer myHTTP = new MyHttpServer();
		int puerto = 0;
		int controller = 0;
		String host = "";

		System.out.println("Cuantas conexiones: ");
		Scanner sc = new Scanner(System.in);
		int max = Integer.parseInt(sc.next());
		//Argumentos
		if(max > 0 && a.length == 3){
			host = a[0];
			puerto = Integer.parseInt(a[1]);
			controller = Integer.parseInt(a[2]);
			myHTTP.Conectar(puerto, host, controller, max);
		}
		else{
			System.out.println("Error de argumentos, cerrando");
		}
		sc.close();
	}
}