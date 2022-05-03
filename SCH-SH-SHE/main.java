/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica4;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class main{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        String fichero = "ch130";
        //String fichero = "a280";

        Ficheros.leerFichero(fichero);
//
//        SH sh = new SH();
//        for (int i = 0; i < 5; i++) {
//            ArrayList<Integer> solucion = sh.calcularSH(i*111, 10);
//            Ficheros.guardarSolucionesMultiples("sh " + fichero, solucion, sh.calcularCoste(solucion), sh.semilla, sh.numEvaluaciones, i);
//        }

        SHE she = new SHE();
        for (int i = 0; i < 5; i++) {
            ArrayList<Integer> solucion = she.calcularSHE(i*111, 10);
            Ficheros.guardarSolucionesMultiples("she " + fichero, solucion, she.calcularCoste(solucion), she.semilla, she.numEvaluaciones, i);
        }

      SCH sch = new SCH();
        for (int i = 0; i < 5; i++) {
            ArrayList<Integer> solucion = sch.calcularSCH(i*111, 10);
            Ficheros.guardarSolucionesMultiples("sch " + fichero, solucion, sch.calcularCoste(solucion), sch.semilla, sch.numEvaluaciones, i);
        }
    }

}
