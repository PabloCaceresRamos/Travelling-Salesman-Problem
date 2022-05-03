/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author pablo
 */
public class EnfriamientoSimulado {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;

    public EnfriamientoSimulado() {//Creamos la matriz de costes
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

                    int dist = (int)  Math.round(Math.sqrt(x * x + y * y));

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

    public ArrayList<Integer> solucionAleatoria() {
        this.coste = 0;
        ArrayList<Integer> solucion = new ArrayList<Integer>();
        ArrayList<Integer> ciudadesDisponibles = (ArrayList<Integer>) Ficheros.indice.clone();//Ciudades que siguen sin ser visitadas
        int posicionCiudadEscogida, ciudadAnterior, nuevaCiudad;

        //Esta parte trata la primera solucion escogida con el proposito de evitar un "if" en el bucle para solo la primera iteracion
        posicionCiudadEscogida = rand.nextInt(ciudadesDisponibles.size());
        solucion.add(ciudadesDisponibles.get(posicionCiudadEscogida));
        ciudadesDisponibles.remove(posicionCiudadEscogida);
        ciudadAnterior = solucion.get(0);

        while (!ciudadesDisponibles.isEmpty()) {

            posicionCiudadEscogida = rand.nextInt(ciudadesDisponibles.size());
            nuevaCiudad = ciudadesDisponibles.get(posicionCiudadEscogida);
            coste += mCostes[ciudadAnterior - 1][nuevaCiudad - 1];

            solucion.add(ciudadesDisponibles.get(posicionCiudadEscogida));
            ciudadesDisponibles.remove(posicionCiudadEscogida);
            ciudadAnterior = nuevaCiudad;//Esto para la siguiente iteracion
        }
        solucion.add(solucion.get(0));
        nuevaCiudad = solucion.get(0);
        coste += mCostes[ciudadAnterior - 1][nuevaCiudad - 1];

        return solucion;
    }

    public ArrayList<Integer> solucionGreedy() {
        Integer ciudadOrigen = 1;
        ArrayList<Integer> solucion = new ArrayList<Integer>();//solucion final

        ArrayList<Integer> ciudadesDisponibles = (ArrayList<Integer>) Ficheros.indice.clone();//Ciudades que siguen sin ser visitadas
        solucion.add(ciudadOrigen);
        ciudadesDisponibles.remove(ciudadOrigen);//Eliminamos aquella posicion con el valor 1

        while (!ciudadesDisponibles.isEmpty()) {
            int ultimaCiudad = solucion.get(solucion.size() - 1);
            Integer nuevaCiudad = ciudadMasCercana(ultimaCiudad, ciudadesDisponibles);
            solucion.add(nuevaCiudad);
            ciudadesDisponibles.remove(nuevaCiudad);
        }

        solucion.add(ciudadOrigen);//Añadimos la primera ciudad

        return solucion;
    }

    private int ciudadMasCercana(int Origen, ArrayList<Integer> ciudadesDisponibles) {

        int minDist = -1;
        int ciudadCercana = -1;
        int tamaño = ciudadesDisponibles.size();

        for (int i : ciudadesDisponibles) {
            if (minDist > mCostes[Origen - 1][i - 1] || minDist == -1) {
                minDist = mCostes[Origen - 1][i - 1];
                ciudadCercana = i;
            }
        }
        return ciudadCercana;
    }

    public int calcularCoste(ArrayList<Integer> nuevaSolucion) {
        int nuevoCoste = 0;

        for (int i = 0; i < nuevaSolucion.size() - 1; i++) {
            nuevoCoste += mCostes[nuevaSolucion.get(i) - 1][nuevaSolucion.get(i + 1) - 1];
        }

        return nuevoCoste;
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

    public ArrayList<Integer> solucionEnfriamientoSimulado(Integer semilla, int L,double mu/*Φ=µ*/) {

        this.semilla = semilla;

        rand = new Random(this.semilla);

        //Inicializamos las variables necesarias
        ArrayList<Integer> solucionActual = solucionAleatoria();
        ArrayList<Integer> solucionCandidata;
        ArrayList<Integer> mejorSolucion=solucionActual;
        int costeGreedy = calcularCoste(solucionGreedy());
        int costeSCandidata;
        int CosteSActual = this.coste;
        int mejorCoste=CosteSActual;
        double T0 = mu / (-Math.log(mu)) * costeGreedy;
        double T = T0;
        int cont = 0;
        int n = solucionActual.size() - 1;
        numEvaluaciones = 0;
        while (cont < 80 * n) {
            for (int i = 0; i < L; i++) {
                int pos2;
                int pos1;
                do {
                     pos1 = rand.nextInt(solucionAleatoria().size() - 1);//El -1 viene porque la primera ciudad se repite al final
                     pos2 = rand.nextInt(solucionAleatoria().size() - 1);
                } while (pos1 == pos2);//Para que no sean la misma solucion 
                
                solucionCandidata = getSolucionCandidata(solucionActual, pos1, pos2);
                costeSCandidata = calcularCoste(solucionCandidata);
                numEvaluaciones++;
                double difCoste =costeSCandidata-mejorCoste;
                if (difCoste < 0 || rand.nextDouble() < Math.exp((-difCoste) / T)) {
                    solucionActual = solucionCandidata;
                    CosteSActual = costeSCandidata;
                }
                if(difCoste<0){//Si es la mejor solución encontrada, la guardamos
                    mejorSolucion=solucionCandidata;
                    mejorCoste=costeSCandidata;
                }
            }

            //Mecanismo de enfriamiento
            cont++;
            T = T0 / (1 + cont);
        }
        coste = mejorCoste;
        return mejorSolucion;
    }

}
