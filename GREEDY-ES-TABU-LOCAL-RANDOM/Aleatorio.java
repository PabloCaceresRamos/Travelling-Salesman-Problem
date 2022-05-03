/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import javafx.util.converter.LocalDateTimeStringConverter;

/**
 *
 * @author pablo
 */
public class Aleatorio {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    int numEvaluaciones;

    public Aleatorio() {//Creamos la matriz de costes
        try {
            if (Ficheros.cX == null || Ficheros.cY == null || Ficheros.indice == null) {
                throw new Exception("Se debe leer los ficheros");
            }
            int coste = 0;
            int tamaño = Ficheros.cX.size();
            mCostes = new Integer[tamaño][tamaño];
            //Calculamos todas las distancias que hay entre cada punto.
            //Para simplificar los calculos de busqueda, en vez de completar la diagonar superior de la matriz, realizamos la diagonal inferior simultaneamente.
            for (int i = 0; i < tamaño; i++) {

                for (int j = i; j < tamaño; j++) {

                    double x = Ficheros.cX.get(i) - Ficheros.cX.get(j);
                    double y = Ficheros.cY.get(i) - Ficheros.cY.get(j);

                    int dist = (int) Math.sqrt(x * x + y * y);

                    mCostes[i][j] = dist;
                    if (i != j) {
                        mCostes[j][i] = dist;
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    public ArrayList<Integer> solucionAleatoria(){
        ArrayList<Integer> solucion = new ArrayList<Integer>();
        ArrayList<Integer> ciudadesDisponibles =  (ArrayList<Integer>) Ficheros.indice.clone();//Ciudades que siguen sin ser visitadas
        int posicionCiudadEscogida,ciudadAnterior,nuevaCiudad;
        
        //Esta parte trata la primera solucion escogida con el proposito de evitar un "if" en el bucle para solo la primera iteracion
        posicionCiudadEscogida=rand.nextInt(ciudadesDisponibles.size()); 
        solucion.add(ciudadesDisponibles.get(posicionCiudadEscogida));
        ciudadesDisponibles.remove(posicionCiudadEscogida);
        ciudadAnterior=solucion.get(0);
        
        while(!ciudadesDisponibles.isEmpty()){
      
            posicionCiudadEscogida=rand.nextInt(ciudadesDisponibles.size()); 
            nuevaCiudad=ciudadesDisponibles.get(posicionCiudadEscogida);
            coste+=mCostes[ciudadAnterior-1][nuevaCiudad-1];
            
            solucion.add(ciudadesDisponibles.get(posicionCiudadEscogida));
            ciudadesDisponibles.remove(posicionCiudadEscogida);   
            ciudadAnterior=nuevaCiudad;//Esto para la siguiente iteracion
        }
        solucion.add(solucion.get(0));
        nuevaCiudad=solucion.get(0);
        coste+=mCostes[ciudadAnterior-1][nuevaCiudad-1];
        numEvaluaciones++;
        return solucion;
    }
    
     public ArrayList<Integer> generarSolucionAleatoria(int semilla){
        //Generamos una semilla segun la hora para el random
        LocalTime time=LocalTime.now();
        this.semilla=semilla;
        rand=new Random(semilla);
        numEvaluaciones=0;
        
        
        coste=0;
         ArrayList<Integer> solucion;
         ArrayList<Integer> mejorSolucion;
         int mejorCoste;
         int repeticiones=Ficheros.cX.size()*1600;
         
         mejorSolucion=solucionAleatoria();
         mejorCoste=coste;
         
         for(int i=0; i < repeticiones;i++){
             coste=0;
             solucion=solucionAleatoria();
             if(coste < mejorCoste){
                 mejorSolucion= (ArrayList<Integer>) solucion.clone();
                 mejorCoste=coste;
             }
         }
            coste=mejorCoste;
            return mejorSolucion;
    }
}
