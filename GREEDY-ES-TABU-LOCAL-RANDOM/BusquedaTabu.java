/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author pablo
 */
public class BusquedaTabu {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;

    public BusquedaTabu() {
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

      private int ciudadMenosUtilizada(int Origen, ArrayList<Integer> ciudadesDisponibles,Integer[][] mLargoPlazo){
        
        int minUtilizacion=-1;
        int ciudadCercana=-1;
        int tamaño=ciudadesDisponibles.size();
        
        for(int i:ciudadesDisponibles){
            if(minUtilizacion > mLargoPlazo[Origen-1][i-1] || minUtilizacion==-1){
                minUtilizacion=mLargoPlazo[Origen-1][i-1];
                ciudadCercana=i;
            }
        }
        return ciudadCercana;
    }
     public ArrayList<Integer> solucionGreedy( Integer[][] mLargoPlazo){//Gredy modificado para usarse con la memoria a largo plazo
        Integer ciudadOrigen=rand.nextInt(mLargoPlazo.length)+1;//cojo una ciudad origen aleatoria
        ArrayList<Integer> solucion = new ArrayList<Integer>();//solucion final
        ArrayList<Integer> ciudadesDisponibles=  (ArrayList<Integer>) Ficheros.indice.clone();//Ciudades que siguen sin ser visitadas
        solucion.add(ciudadOrigen);
        ciudadesDisponibles.remove(ciudadOrigen);//Ocupamos la ciudad origen
        
        while(!ciudadesDisponibles.isEmpty()){
            int ultimaCiudad=solucion.get(solucion.size()-1);
            Integer nuevaCiudad= ciudadMenosUtilizada(ultimaCiudad, ciudadesDisponibles,mLargoPlazo);
            solucion.add(nuevaCiudad);
            ciudadesDisponibles.remove(nuevaCiudad);
        }
        
        solucion.add(ciudadOrigen);
        
        return solucion;
    }
    public ArrayList<Integer> solucionAleatoria() {
        ArrayList<Integer> solucion = new ArrayList<Integer>();
        ArrayList<Integer> ciudadesDisponibles = (ArrayList<Integer>) Ficheros.indice.clone();//Ciudades que siguen sin ser visitadas
        int posicionCiudadEscogida;

        //Esta parte trata la primera solucion escogida con el proposito de evitar un "if" en el bucle para solo la primera iteracion
        posicionCiudadEscogida = rand.nextInt(ciudadesDisponibles.size());
        solucion.add(ciudadesDisponibles.get(posicionCiudadEscogida));
        ciudadesDisponibles.remove(posicionCiudadEscogida);

        while (!ciudadesDisponibles.isEmpty()) {

            posicionCiudadEscogida = rand.nextInt(ciudadesDisponibles.size());

            solucion.add(ciudadesDisponibles.get(posicionCiudadEscogida));
            ciudadesDisponibles.remove(posicionCiudadEscogida);

        }
        solucion.add(solucion.get(0));

        return solucion;
    }

    private ArrayList<Integer> getSolucionCandidata(ArrayList<Integer> solucionActual, int pos1, int pos2) {
        ArrayList<Integer> solucionCandidata = (ArrayList<Integer>) solucionActual.clone();

        int ciudadPos1 = solucionActual.get(pos1);
        int ciudadPos2 = solucionActual.get(pos2);

        solucionCandidata.set(pos1, ciudadPos2);
        solucionCandidata.set(pos2, ciudadPos1);
        if (pos1 == 0) {
            solucionCandidata.set(solucionCandidata.size() - 1, ciudadPos2);
        }
        if (pos2 == 0) {
            solucionCandidata.set(solucionCandidata.size() - 1, ciudadPos1);
        }

        return solucionCandidata;
    }

    public int calcularCoste(ArrayList<Integer> nuevaSolucion) {
        int nuevoCoste = 0;

        for (int i = 0; i < nuevaSolucion.size() - 1; i++) {
            nuevoCoste += mCostes[nuevaSolucion.get(i) - 1][nuevaSolucion.get(i + 1) - 1];
        }

        return nuevoCoste;
    }

    public ArrayList<Integer> solucionTabu(int semilla) {

        this.semilla = semilla;
        rand = new Random(this.semilla);

        //inicializo las variables
        ArrayList<Integer> solucionActual = this.solucionAleatoria();//La solucion actual va a salir de una solucion aleatoria
        ArrayList<Integer> mejorSolucion = (ArrayList<Integer>) solucionActual.clone();
        ArrayList<Integer> mejorVecino = null;
        ArrayList<Integer> solucionCandidata;

        int costeSolucionActual = this.calcularCoste(solucionActual);
        int costeMejorSolucion = costeSolucionActual;
        int costeMejorVecino;
        int costeSolucionCandidata;

        this.numEvaluaciones = 0;
        int numCiudades = solucionActual.size() - 1;//el -1 es porque la primera ciudad la insertamos por duplicado en la primera y ultima posicion
        ArrayList<String> listaCortoPlazo = new ArrayList<>();
        int tamañoLista = numCiudades / 2;
        Integer[][] mLargoPlazo = new Integer[numCiudades][numCiudades];//Memoria de largo plazo
        for (int col = 0; col < mLargoPlazo.length; col++) {
            for (int fil = 0; fil < mLargoPlazo[0].length; fil++) {
                mLargoPlazo[col][fil] = 0;
            }
        }

        for (int i = 0; i < 40 * numCiudades; i++) {

            costeMejorVecino = -1;//Para no utilizar el mejor vecino de la vuelta anterior

            for (int j = 0; j < 40; j++) {//buscamos a 40 vecinos
                int pos2;
                int pos1;
                do {
                    pos1 = rand.nextInt(numCiudades);//El -1 viene porque la primera ciudad se repite al final
                    pos2 = rand.nextInt(numCiudades);
                } while (pos1 == pos2);//Para que no sean la misma solucion 

                solucionCandidata = getSolucionCandidata(solucionActual, pos1, pos2);
                costeSolucionCandidata = calcularCoste(solucionCandidata);
                numEvaluaciones++;

                int ciudad1 = solucionActual.get(pos1);
                int ciudad2 = solucionActual.get(pos2);
                String combinacion;
                //para marcar la combinacion, pongo primero la ciudad mas pequeña y luego la ciudad mas grande asi el 6,4 es lo mismo que el 4,6
                if (ciudad1 < ciudad2) {
                    combinacion = ciudad1 + ":" + ciudad2;//esto lo utilizaremos para la lista a corto plazo y ver las combinaciones facilmente
                } else {
                    combinacion = ciudad2 + ":" + ciudad1;
                }
                if (costeMejorSolucion > costeSolucionCandidata || listaCortoPlazo.indexOf(combinacion) == -1) {
                    //Entramos aquí solo si la combinacion no esta en la lista tabu, o si esta, pero es la mejor solucción encontrada

                    if (costeSolucionCandidata < costeMejorVecino || costeMejorVecino == -1) {
                        mejorVecino = solucionCandidata;
                        costeMejorVecino = costeSolucionCandidata;
                    }

                    if (costeSolucionCandidata < costeMejorSolucion) {
                        mejorSolucion = solucionCandidata;
                        costeMejorSolucion = costeSolucionCandidata;
                    }

                    //Añadimos la solucción candidata a la memoria de corto plazo, asegurandonos que si esta llena remplace al elemento mas viejo
                    if (listaCortoPlazo.size() >= tamañoLista) {
                        listaCortoPlazo.remove(0);
                        listaCortoPlazo.add(combinacion);
                    } else {
                        listaCortoPlazo.add(combinacion);
                    }

                    //Aumentamos la frecuencia de toda la solucion candidata en la lista de largo plazo
                    for (int pos = 0; pos < numCiudades; pos++) {
                        int c1 = solucionCandidata.get(pos);
                        int c2 = solucionCandidata.get(pos + 1);
                        mLargoPlazo[c1-1][c2-1]++;
                        mLargoPlazo[c2-1][c1-1]++;

                    }

                }

            }
            //Cuando he visitado todos los vecinos, me quedo con el mejor de ellos
            solucionActual = (ArrayList<Integer>) mejorVecino.clone();
            costeSolucionActual = costeMejorVecino;

            //Reinicios
            if (i == 8 * numCiudades) {
                int reinicio = rand.nextInt(4);
                //reinicio 25% solución aleatoria
                if (reinicio == 0) {
                    solucionActual = this.solucionAleatoria();
                    costeSolucionActual = this.calcularCoste(solucionActual);
                    
                }
                //reinicio 25% solución desde la mejor solución
                if (reinicio == 1) {
                    solucionActual = (ArrayList<Integer>) mejorSolucion.clone();
                    costeSolucionActual = costeMejorSolucion;
                } //reinicio 50% solucíon greedy por memoria largo plazo
                if (reinicio >= 2) {
                    solucionActual = solucionGreedy(mLargoPlazo);
                    costeSolucionActual = calcularCoste(solucionActual);
                }
                listaCortoPlazo = new ArrayList<>();//eliminamos el contenido de la lista tabu
                int cambioTamañoMemoria=rand.nextInt(2);
                //Hay un 50% de probabilidad de que la lista se reduzca un 50% o que se incremente un 50%
                if(cambioTamañoMemoria==0){
                    tamañoLista=tamañoLista*2;
                }else{
                    tamañoLista=tamañoLista/2;
                }
            }//fin reinicio
        }

        this.coste = costeMejorSolucion;
        return mejorSolucion;
    }

}
