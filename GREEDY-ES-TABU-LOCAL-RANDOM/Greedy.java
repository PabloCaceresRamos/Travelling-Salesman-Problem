/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;

import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class Greedy {
    
    private Integer[][] mCostes;//Matriz de costes 
    int coste;
    
    public Greedy(){
        try{
        if(Ficheros.cX==null || Ficheros.cY==null || Ficheros.indice==null){
            throw new Exception("Se debe leer los ficheros");
        }
        int tamaño=Ficheros.cX.size();
        mCostes=new Integer[tamaño][tamaño];
        //Calculamos todas las distancias que hay entre cada punto.
        //Para simplificar los calculos de busqueda, en vez de completar la diagonar superior de la matriz, realizamos la diagonal inferior simultaneamente.
            for (int i=0; i<tamaño;i++) {
                
                for (int j=i; j<tamaño;j++) {
                    
                    double x= Ficheros.cX.get(i)-Ficheros.cX.get(j);
                    double y= Ficheros.cY.get(i)-Ficheros.cY.get(j);
                    
                    int dist=(int) Math.round(Math.sqrt(x*x + y*y));
                    
                    mCostes[i][j]=dist;
                    if(i!=j) mCostes[j][i]=dist;
                 }
                
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
        
    }
    
      private int ciudadMasCercana(int Origen, ArrayList<Integer> ciudadesDisponibles) {

        int minDist = -1;
        int ciudadCercana = -1;

        for (int i : ciudadesDisponibles) {
            if (minDist > mCostes[Origen - 1][i - 1] || minDist == -1) {
                minDist = mCostes[Origen - 1][i - 1];
                ciudadCercana = i;
            }
        }
        return ciudadCercana;
    }
    
    public ArrayList<Integer> solucionGreedy(){
        coste=0;
        Integer ciudadOrigen=1;
        ArrayList<Integer> solucion = new ArrayList<Integer>();//solucion final
        
        ArrayList<Integer> ciudadesDisponibles=  (ArrayList<Integer>) Ficheros.indice.clone();//Ciudades que siguen sin ser visitadas
        solucion.add(ciudadOrigen);
        ciudadesDisponibles.remove(ciudadOrigen);//Eliminamos aquella posicion con el valor 1
        
        while(!ciudadesDisponibles.isEmpty()){
            int ultimaCiudad=solucion.get(solucion.size()-1);
            Integer nuevaCiudad= ciudadMasCercana(ultimaCiudad, ciudadesDisponibles);
            solucion.add(nuevaCiudad);
            ciudadesDisponibles.remove(nuevaCiudad);
        }
        
        solucion.add(ciudadOrigen);
        coste+=mCostes[solucion.get(solucion.size()-1)][ciudadOrigen-1];
        
        return solucion;
    }
    
}
