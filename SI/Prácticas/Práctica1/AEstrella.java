 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AEstrella;
import static java.lang.Math.*;
import java.util.*;

/**
 *
 * @author mirse
 */

class Nodo{
    private Nodo papa;
    private Coordenada n;
    private int f, g, h;
    
        
    public void setPapa(Nodo p){
        papa = p;
    }
    
    public void setG(int g){
        this.g = g;
    }
    
    public void setF(int f){
        this.f = f;
    }
    
    public void setH(int h){
        this.h = h;
    }
    
    public void setN(Coordenada n){
        this.n = n;
    }
    
    public Nodo getPapa(){
        return papa;
    }
    
    public Coordenada getN(){
        return n;
    }
    public int getG(){
        return g;
    }
    
    public int getF(){
        return f;
    }
    
    public int getH(){
        return h;
    }
    
    public Nodo(Coordenada n, int h, Nodo p, int coste){
       if(p == null){
           this.g = coste;
       }
       else{
           this.g = coste + p.getG();
       }
       this.f = g + h;
       this.h = h;
       this.n = n;
       papa = p;
    }
}
public class AEstrella {
    //Mundo sobre el que se debe calcular A*
    Mundo mundo;
    
    //Camino
    public char camino[][];
    
    //Casillas expandidas
    int camino_expandido[][];
    
    //Número de nodos expandidos
    int expandidos;
    
    //Coste del camino
    float coste_total;
    
    public AEstrella(){
        expandidos = 0;
        mundo = new Mundo();
        
    }
    
    public AEstrella(Mundo m){
        //Copia el mundo que le llega por parámetro
        mundo = new Mundo(m);
        camino = new char[m.tamanyo_y][m.tamanyo_x];
        camino_expandido = new int[m.tamanyo_y][m.tamanyo_x];
        expandidos = 0;
        
        //Inicializa las variables camino y camino_expandidos donde el A* debe incluir el resultado
            for(int i=0;i<m.tamanyo_x;i++)
                for(int j=0;j<m.tamanyo_y;j++){
                    camino[j][i] = '.';
                    camino_expandido[j][i] = -1;
                }
    }
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
    private int Manhattan(Coordenada x, Coordenada y){
        int sol;
                sol = abs(x.getX() - y.getX()) + abs(x.getY() - y.getY());
        return sol;
    }
    
    private int Euclidea(Coordenada x, Coordenada y){
        int sol;
                sol = (int)sqrt(pow((x.getX() - y.getX()),2) + pow((x.getY() - y.getY()),2));
        return sol;
    }

    
    public int recursivo(Coordenada a, Coordenada b, int total){
	Coordenada c = new Coordenada();
        
	if(a.getY() == b.getY() && a.getX() == b.getX()){
		return total;
	}
	if(a.getY()%2 != 0){
		total = total + 1;
		if(b.getY() == a.getY() && a.getX() < b.getX()){
			c = new Coordenada(a.getX()+1, a.getY());
                }
		else if(b.getY() == a.getY() && a.getX() > b.getX()){
			c = new Coordenada(a.getX()-1, a.getY());
		}
		else if((b.getX() == a.getX() && a.getY() < b.getY()) || (a.getX() < b.getX() && a.getY() < b.getY())){
			c = new Coordenada(a.getX(), a.getY()+1);
		}
		else if((b.getX() == a.getX() && a.getY() > b.getY()) || (a.getX() < b.getX() && a.getY() > b.getY())){
			c = new Coordenada(a.getX(), a.getY()-1);
		}
		else if(a.getX() > b.getX() && a.getY() < b.getY()){
			c = new Coordenada(a.getX()-1, a.getY()+1);
		}	
		else{
			c = new Coordenada(a.getX()-1, a.getY()-1);
		}
		total = recursivo(c, b, total);
	}
	if(a.getY()%2 == 0){
		total = total + 1;		
		if(b.getY() == a.getY() && a.getX() < b.getX()){
			c = new Coordenada(a.getX()+1, a.getY());
		}
		else if(b.getY() == a.getY() && a.getX() > b.getX()){
			c = new Coordenada(a.getX()-1, a.getY());
		}
		else if((b.getX() == a.getX() && a.getY() < b.getY()) || (a.getX() > b.getX() && a.getY() < b.getY())){
			c = new Coordenada(a.getX(), a.getY()+1);
		}
		else if((b.getX() == a.getX() && a.getY() > b.getY()) || (a.getX() > b.getX() && a.getY() > b.getY())){
			c = new Coordenada(a.getX(), a.getY()-1);
		}
		else if(a.getX() < b.getX() && a.getY() < b.getY()){
			c = new Coordenada(a.getX()+1, a.getY()+1);
		}	
		else{
			c = new Coordenada(a.getX()+1, a.getY()-1);
		}
		total = recursivo(c, b, total);
	}
        return total;
       }

    private void alrededor(Coordenada c, ArrayList<Nodo> listaI, ArrayList<Nodo> listaF, Nodo n, Coordenada d){
        //int h = 0;                                                                                                //H!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //int h = Manhattan(c, d);
        //int h = Euclidea(c, d);
        int h = recursivo(c, d , 0);
        boolean es = true;
        
        //Para cada hijo m de n(padre) que no esté en lista interior
        for(int i = 0; i < listaI.size() && es; i++){
            if(c.getX() == (listaI.get(i).getN().getX()) && c.getY() == (listaI.get(i).getN().getY())){
                es = false;
            }
        }
        //Coste de las posiciones g'(m)
        if(es){
            int coste;
            switch(mundo.getCelda(c.getX(), c.getY())){
                case('a'):
                        coste = 3;
                    break;
                case('h'):
                        coste = 2;
                    break;
                case('c'):
                        coste = 1;
                    break;
                case('d'):
                        coste = 1;
                    break;
                default:
                        coste = 99;
                    break;
            }
            int mejor = 0;
            int posicion = -1;
            
               //Si es piedra o bloque no lo pongo
            if(mundo.getCelda(c.getX(), c.getY()) != 'b' && mundo.getCelda(c.getX(), c.getY()) != 'p'){
                
                //Mirar si existe en listaF
                for(int i = 0; i < listaF.size() && es; i++){
                    if(c.getX() == listaF.get(i).getN().getX() && c.getY() == listaF.get(i).getN().getY()){
                        es = false;
                        posicion = i;
                        mejor = listaF.get(i).getG();
                    }
                }
                if(es){
                    listaF.add(new Nodo(c, h, n, coste));
                    
                    //Vamos añadiendo el camino expandido
                    expandidos++;
                    camino_expandido[c.getY()][c.getX()] = expandidos;//h;//expandidos;
                }
                else if((coste + n.getG()) < mejor){
                    listaF.remove(posicion);
                    listaF.add(new Nodo(c, h, n, coste));
                }
            }
        }
    }
    //Calcula el A*
    public int CalcularAEstrella(){
        boolean encontrado = false;
        int result = -1;
        
        //AQUÍ ES DONDE SE DEBE IMPLEMENTAR A*
        ArrayList<Nodo> listaInterior = new ArrayList<Nodo>();
        ArrayList<Nodo> listaFrontera = new ArrayList<Nodo>();       

        //Guardo las coordenadas de caballero y el dragón
        Coordenada c = mundo.getCaballero();
        Coordenada d = mundo.getDragon();
        
        //int h = 0;                                                                                               //H!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //int h = Manhattan(c, d);
        //int h = Euclidea(c, d);
        int h = recursivo(c, d , 0);

        //Añadimos la posicion del caballero en listaFrontera
        listaFrontera.add(new Nodo(c, h, null, 0)); 
        camino_expandido[c.getY()][c.getX()] = 0;
        
        //Mientras listaFrontera no este vacia y no se haya encontrado al dragón
        while(!listaFrontera.isEmpty() && !encontrado){
            
            if(c.getX() == d.getX() && c.getY() == d.getY()){
                return result;
            }
            
            //Guardo como nodo la posicion 0 de listaFrontera que se trata del caballero
            Nodo n = listaFrontera.get(0);
            
            //Busco el nodo con mejor F() para cogerlo y hacer el camino
            for(int i = 0; i < listaFrontera.size(); i++){
                Nodo co = listaFrontera.get(i);
                if(co.getF() <= n.getF()){
                    n = listaFrontera.get(i);
                }
            }
            
            //Si se ha encontrado el dragón, dibujo el camino y voy cambiando de padres
            //(Además añado el coste_total como resultado)
            if(n.getN().getX() == d.getX() && n.getN().getY() == d.getY()){
                coste_total = n.getG();
                encontrado = true;
                result = (int)coste_total;
                while(n != null){
                    camino[n.getN().getY()][n.getN().getX()] = 'X';
                    n = n.getPapa();
                }
            }
            //Si no se ha encontrado el dragón
            else{
                //Eliminamos el nodo de listaFrontera y lo añadimos en listaInterior
                listaFrontera.remove(n);
                listaInterior.add(n);


                //Posiciones para guardar en listaFrontera
                alrededor((new Coordenada(n.getN().getX(), n.getN().getY()+1)), listaInterior, listaFrontera, n, d);
                alrededor((new Coordenada(n.getN().getX(), n.getN().getY()-1)), listaInterior, listaFrontera, n, d);
                alrededor((new Coordenada(n.getN().getX()+1, n.getN().getY())), listaInterior, listaFrontera, n, d);
                alrededor((new Coordenada(n.getN().getX()-1, n.getN().getY())), listaInterior, listaFrontera, n, d);
                
                //Cuando es par la posicion
                if(n.getN().getY()% 2 == 0){

                    alrededor((new Coordenada(n.getN().getX()+1, n.getN().getY()+1)), listaInterior, listaFrontera, n, d);
                    alrededor((new Coordenada(n.getN().getX()+1, n.getN().getY()-1)), listaInterior, listaFrontera, n, d);
                }
                //Cuando es impar
                else{
                    alrededor((new Coordenada(n.getN().getX()-1, n.getN().getY()-1)), listaInterior, listaFrontera, n, d);
                    alrededor((new Coordenada(n.getN().getX()-1, n.getN().getY()+1)), listaInterior, listaFrontera, n, d);
                }
            }  
        }
        
        //Si ha encontrado la solución, es decir, el camino, muestra las matrices camino y camino_expandidos y el número de nodos expandidos
        if(encontrado){
            //Mostrar las soluciones
            System.out.println("Camino");
            
            mostrarCamino();

            System.out.println("Camino explorado");
            mostrarCaminoExpandido();
            
            System.out.println("Nodos expandidos: " + expandidos);
        }
        
        return result;
    }
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Muestra la matriz que contendrá el camino después de calcular A*
    public void mostrarCamino(){
        for (int i=0; i<mundo.tamanyo_y; i++){
            if(i%2==0)
                System.out.print(" ");
            for(int j=0;j<mundo.tamanyo_x; j++){
                System.out.print(camino[i][j]+" ");
            }
            System.out.println();   
        }
    }
    
    //Muestra la matriz que contendrá el orden de los nodos expandidos después de calcular A*
    public void mostrarCaminoExpandido(){
        for (int i=0; i<mundo.tamanyo_y; i++){
            if(i%2==0)
                    System.out.print(" ");
            for(int j=0;j<mundo.tamanyo_x; j++){
                if(camino_expandido[i][j]>-1 && camino_expandido[i][j]<10)
                    System.out.print(" ");
                System.out.print(camino_expandido[i][j]+" ");
            }
            System.out.println();   
        }
    }
    
    public void reiniciarAEstrella(Mundo m){
        //Copia el mundo que le llega por parámetro
        mundo = new Mundo(m);
        camino = new char[m.tamanyo_y][m.tamanyo_x];
        camino_expandido = new int[m.tamanyo_y][m.tamanyo_x];
        expandidos = 0;
        
        //Inicializa las variables camino y camino_expandidos donde el A* debe incluir el resultado
            for(int i=0;i<m.tamanyo_x;i++)
                for(int j=0;j<m.tamanyo_y;j++){
                    camino[j][i] = '.';
                    camino_expandido[j][i] = -1;
                }
    }
    
    public float getCosteTotal(){
        return coste_total;
    }
  
}


