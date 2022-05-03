/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Pablo
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

       String fichero="st70";
      //String fichero="ch130";
        //String fichero = "a280";
        //String fichero = "p654";
        //String fichero = "Vm1084";
       // String fichero = "Vm1748";
        
        Ficheros.leerFichero(fichero);

       //Greedy g=new Greedy();
       //Ficheros.guardarSolucion(fichero+"Greedy", g.solucionGreedy(), g.coste, null);
       
     /*  Aleatorio a=new Aleatorio();
       for(int i=1; i<= 10; i++)
       Ficheros.guardarSolucionesMultiples(fichero+"Aleatorio", a.generarSolucionAleatoria(i), a.coste, i,a.numEvaluaciones,i);*/
       
       
//        BusquedaLocal b=new BusquedaLocal();
//          for(int i=1; i<= 10; i++){
//          //Ficheros.guardarSolucionAleatorio(fichero+"BLelmejor", b.busquedaLocalElMejor(null), b.coste, b.semilla,i);
//          Ficheros.guardarSolucionesMultiples(fichero+"BL El mejor", b.busquedaLocalElMejor(i), b.coste, b.semilla,b.numEvaluaciones,i);
//          }
        
        
//        EnfriamientoSimulado e= new EnfriamientoSimulado();
//        for(int i=1; i<= 10; i++){
//          Ficheros.guardarSolucionesMultiples(fichero+"Enfriamiento", e.solucionEnfriamientoSimulado(i*10,20,0.3), e.coste, e.semilla,e.numEvaluaciones,i);
//          }

            BusquedaTabu t= new BusquedaTabu();
             for(int i=1; i<= 10; i++){
            Ficheros.guardarSolucionesMultiples(fichero + "Tabu", t.solucionTabu(i*10), t.coste, t.semilla, t.numEvaluaciones, i);
             }
        
    }

}
