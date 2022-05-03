/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class main {

    public static void main(String[] args) throws IOException {

        String fichero = "st70";
        //String fichero="ch130";
        //String fichero = "a280";

        Ficheros.leerFichero(fichero);

        //GRASP g = new GRASP();
        //Ficheros.guardarSolucion("GRASP2 "+fichero, g.calcularSolucionGRASP(5), g.coste, g.semilla, g.numEvaluaciones);
        
        
        // ILS i=new ILS();
        //Ficheros.guardarSolucion("ILS "+fichero, i.calcularSolucionILS(0), i.coste, i.semilla, i.numEvaluaciones);
        
        
//           VNS v=new VNS();  
//           Ficheros.guardarSolucion("VNS2 "+fichero, v.calcularSolucionVNS(0), v.coste, v.semilla, v.numEvaluaciones);       



//        GRASP g = new GRASP();
//        ArrayList<Double> p = g.getProbabilidadesInversasPrueba();

    }
}
