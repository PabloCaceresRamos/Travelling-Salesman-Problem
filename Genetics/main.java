/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        //String fichero = "st70";
        //String fichero = "ch130";
       String fichero = "a280";
        //String fichero = "Vm1748";

        Ficheros.leerFichero(fichero);

//        GeneticoBasico g = new GeneticoBasico();
//        
//            for (int i = 0; i < 5; i++) {
//            ArrayList<Integer> solucion = g.calcularGeneticoBasico(111*i, 100, 50/ 100.0f, 5);
//            Ficheros.guardarSolucionesMultiples("gb "+fichero+" s", solucion,g.calcularCoste(solucion),g.semilla,g.numEvaluaciones,i);
//            System.out.println( g.calcularCoste(solucion));
//        }


//        CHC chc = new CHC();
//        
//         for (int i = 0; i < 5; i++) {
//        ArrayList<Integer> solucion = chc.calcularCHC(55, 70,15);
//         Ficheros.guardarSolucionesMultiples("chc "+fichero+" s", solucion,chc.calcularCoste(solucion),chc.semilla,chc.numEvaluaciones,i);
//         System.out.println( chc.calcularCoste(solucion));
//        }

            for (int i = 0; i < 5; i++) {
        GeneticoSecuencial gs=new GeneticoSecuencial();
        ArrayList<ArrayList<Integer>> a=gs.calcularGeneticoSecuencial(i*111, 100, 5);
        for (ArrayList<Integer> aa : a) {
            System.out.println(gs.calcularCoste(aa));
            Ficheros.guardarSolucionesMultiples("gs "+fichero+" s", aa,gs.calcularCoste(aa),gs.semilla,gs.numEvaluaciones,i);
        }
                System.out.println("-----------------------------------------------------------------");
        }
        
    }

}
