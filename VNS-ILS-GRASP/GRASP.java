/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author pablo
 */
public class GRASP {
    
    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;
    
    public GRASP() {
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
                    
                    int dist = (int) Math.round(Math.sqrt(x * x + y * y));
                    
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
    
    private ArrayList<Integer> getCiudadesMasCercanas(int Origen, ArrayList<Integer> ciudadesDisponibles, int l) {
        
        ArrayList<Integer> ciudadesDisponiblesAux = (ArrayList<Integer>) ciudadesDisponibles.clone();
        ArrayList<Integer> ciudadesMasCercanas = new ArrayList<>();
        int cont = 0;
        int minDist;
        Integer ciudadMinDist=null;
        
        while (cont < l && !ciudadesDisponiblesAux.isEmpty()) {
            minDist=-1;
            for (int i : ciudadesDisponiblesAux) {

                if (minDist > mCostes[Origen - 1][i - 1] || minDist == -1) {
                    minDist = mCostes[Origen - 1][i - 1];
                    ciudadMinDist=i;
                }
            }
            ciudadesMasCercanas.add(ciudadMinDist);
            ciudadesDisponiblesAux.remove(ciudadMinDist);
            cont++;
        }
        
        return ciudadesMasCercanas;
    }
    

    
     private ArrayList<Double> getProbabilidadesInversas(int Origen, ArrayList<Integer> ciudadesMasCercanas) {

        ArrayList<Double> probabilidades = new ArrayList<>();
        
        double sumDif = 0;//esto se va a utilizar para la formula de la probabilidad
        
        for (int i = 0; i < ciudadesMasCercanas.size(); i++) {
            double dist=mCostes[Origen - 1][ciudadesMasCercanas.get(i) - 1];
            if(dist==0)dist=0.5;
            sumDif+=1.0/dist;
        }
        //probabilidad=(1/dist)/sumatorio(1/distancias)
        for (int i = 0; i < ciudadesMasCercanas.size(); i++) {
            double dist=mCostes[Origen - 1][ciudadesMasCercanas.get(i) - 1];
            if(dist==0)
                dist=0.5;
            double prob= (1.0/dist)/sumDif;
            probabilidades.add(prob);
        }
        
         return probabilidades;
     }
     
      public ArrayList<Double> getProbabilidadesInversasPrueba() {

          ArrayList<Double> ciudadesMasCercanas = new ArrayList<>();
          ciudadesMasCercanas.add(0.0);
          ciudadesMasCercanas.add(1.0);
          ciudadesMasCercanas.add(2.0);
          ciudadesMasCercanas.add(4.0);
          
        ArrayList<Double> probabilidades = new ArrayList<>();
        
        double sumDif = 0;//esto se va a utilizar para la formula de la probabilidad
        
        for (int i = 0; i < ciudadesMasCercanas.size(); i++) {
            double dist=ciudadesMasCercanas.get(i);
            if(dist==0)dist=0.5;
            sumDif+=1.0/dist;
        }
        //probabilidad=(1/dist)/sumatorio(1/distancias)
        for (int i = 0; i < ciudadesMasCercanas.size(); i++) {
            double dist=ciudadesMasCercanas.get(i);
            if(dist==0)
                dist=0.5;
            double prob= (1.0/dist)/sumDif;
            probabilidades.add(prob);
        }
        
         return probabilidades;
     }
    
     private int conseguirCiudadCercanaProbabilidad(ArrayList<Double> probabilidadesCiudades) {
        double rand=this.rand.nextDouble();
        double sumProbabilidadesAnteriores=0;//el valor final de esta variable debe ser 1 ya que va sumando todas las probabilidades. 
         for (int i = 0; i < probabilidadesCiudades.size(); i++) {
             if(probabilidadesCiudades.get(i)+sumProbabilidadesAnteriores>rand) return i;
             else{
                 sumProbabilidadesAnteriores+=probabilidadesCiudades.get(i);
             }
         }
         return 0;
    }
    
    private int ciudadMasProbable(int Origen, ArrayList<Integer> ciudadesDisponibles) {
        
        int ciudadCercana;

        int l = (int) (0.1 * Ficheros.cX.size());//lista de menores distancias
        ArrayList<Integer> ciudadesMasCercanas = getCiudadesMasCercanas(Origen, ciudadesDisponibles, l);
        ArrayList<Double> probabilidadesCiudades = getProbabilidadesInversas(Origen, ciudadesMasCercanas);
        ciudadCercana= conseguirCiudadCercanaProbabilidad(probabilidadesCiudades);
        
        return ciudadesDisponibles.get(ciudadCercana);
    }
    
    private ArrayList<Integer> solucionGreedyAleatorizado() {
        numEvaluaciones++;
         Integer ciudadOrigen=rand.nextInt(mCostes.length)+1;//cojo una ciudad origen aleatoria
        ArrayList<Integer> solucion = new ArrayList<>();//solucion final

        ArrayList<Integer> ciudadesDisponibles = (ArrayList<Integer>) Ficheros.indice.clone();//Ciudades que siguen sin ser visitadas
        solucion.add(ciudadOrigen);
        ciudadesDisponibles.remove(ciudadOrigen);//Eliminamos aquella posicion con el valor 1

        while (!ciudadesDisponibles.isEmpty()) {
            if (ciudadesDisponibles.size() == 1) {//Si queda una ciudad no hace falta que hagamos las probabilidades
                Integer nuevaCiudad = ciudadesDisponibles.get(0);
                solucion.add(nuevaCiudad);
                ciudadesDisponibles.remove(nuevaCiudad);
            } else {
                int ultimaCiudad = solucion.get(solucion.size() - 1);
                Integer nuevaCiudad = ciudadMasProbable(ultimaCiudad, ciudadesDisponibles);
                solucion.add(nuevaCiudad);
                ciudadesDisponibles.remove(nuevaCiudad);
            }
        }
        
        solucion.add(ciudadOrigen);
        
        return solucion;
    }
    
     public int calcularCoste(ArrayList<Integer> nuevaSolucion) {
        int nuevoCoste = 0;

        for (int i = 0; i < nuevaSolucion.size() - 1; i++) {
            nuevoCoste += mCostes[nuevaSolucion.get(i) - 1][nuevaSolucion.get(i + 1) - 1];
        }

        return nuevoCoste;
    }
    
    private ArrayList<Integer> busquedaLocalElPrimerMejor(ArrayList<Integer> solucionGreedy) {

        //nicializamos los valores necesarios
        int costeSCandidata;
        int mejorCoste;
        int contador = 0;
        int ciudadI;
        int ciudadJ;

        ArrayList<Integer> solucionCandidata;
        ArrayList<Integer> mejorSolucion;
        int repeticiones = Ficheros.cX.size() * 1600;
        int numCiudades;
       

        //Buscamos una solucion aleatoria
        solucionCandidata = solucionGreedy;
        costeSCandidata = calcularCoste(solucionCandidata);

        numCiudades = solucionCandidata.size() - 1;//Para el limite de los for
        do {
            mejorSolucion = solucionCandidata;
            mejorCoste = costeSCandidata;
            int i = 0;
            while (i < numCiudades - 1 && costeSCandidata >= mejorCoste) {//vamos a cambiar la posicion i por la posicion j buscando la mejor solucion
                //la j siempre va a ser mayor que la i, para que no se repitan vecinos
                int j = i + 1;
                while (j < numCiudades && costeSCandidata >= mejorCoste) {
                    numEvaluaciones++;
                    solucionCandidata = (ArrayList<Integer>) mejorSolucion.clone();
                    ciudadI = mejorSolucion.get(i);
                    ciudadJ = mejorSolucion.get(j);

                    solucionCandidata.set(i, ciudadJ);
                    solucionCandidata.set(j, ciudadI);
                    if (i == 0) {
                        solucionCandidata.set(solucionCandidata.size() - 1, ciudadJ);
                    }
                    //Evaluamos la solucion
                    costeSCandidata = calcularCoste(solucionCandidata);
                    j++;
                    contador++;
                }
                i++;
            }
            //contador++;
        } while (mejorCoste > costeSCandidata && contador <= repeticiones);
        return mejorSolucion;
    }
     
     
     public ArrayList<Integer> calcularSolucionGRASP(int semilla){
         this.semilla=semilla;
         this.rand=new Random(semilla);
          numEvaluaciones = 0;
          
         ArrayList<Integer> mejorSolucion=null;
          Integer mejorCoste=Integer.MAX_VALUE;
          ArrayList<Integer> SolucionCandidata;
          Integer CosteCandidata;
         for(int i=0;i<10;i++){
             //Saco la solucion greedy y se la paso a la busqueda local
             SolucionCandidata=busquedaLocalElPrimerMejor(solucionGreedyAleatorizado());
             CosteCandidata=calcularCoste(SolucionCandidata);
             
             if(CosteCandidata<mejorCoste){
                 mejorSolucion=SolucionCandidata;
                 mejorCoste=CosteCandidata;
             }
             
         }
         coste=mejorCoste;
         return mejorSolucion;
     }

   
    
}
