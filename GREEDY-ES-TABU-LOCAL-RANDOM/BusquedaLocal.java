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
public class BusquedaLocal {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;

    public BusquedaLocal() {
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

   /* public int evaluarSolucionCambioVecino(int coste, int pos1, int pos2, ArrayList<Integer> antiguaSolucion, ArrayList<Integer> nuevaSolucion) {

        int nuevoCoste = coste;
        ArrayList<Integer> SolucionAux=(ArrayList<Integer>) antiguaSolucion.clone();
        SolucionAux.remove(SolucionAux.size()-1);
        int numCiudades=SolucionAux.size();
        //guardamos la posicion siguiente y anterior para asegurarnos de que la anterior a la 0 sea la ultima ciudad y la posterior a la ultima ciudad sea la 0
        int izPos1=pos1-1;
        int dePos1=(pos1+1)%numCiudades;
        int izPos2=pos2-1;
        int dePos2=(pos2+1)%numCiudades;
        if(izPos1<0) izPos1=numCiudades-1;
        if(izPos2<0) izPos2=numCiudades-1;
        //Quitamos los caminos que conectan con la ciudad en la pos1

        nuevoCoste -= mCostes[SolucionAux.get(izPos1) - 1][SolucionAux.get(pos1) - 1];
        nuevoCoste -= mCostes[SolucionAux.get(pos1) - 1][SolucionAux.get(dePos1) - 1];

        //Quitamos los caminos que conectan con la ciudad en la pos2
        //La pos2 nunca va a estar en la primera posicion
        nuevoCoste -= mCostes[SolucionAux.get(izPos2) - 1][SolucionAux.get(pos2) - 1];
        nuevoCoste -= mCostes[SolucionAux.get(pos2) - 1][SolucionAux.get(dePos2) - 1];

         SolucionAux=(ArrayList<Integer>) nuevaSolucion.clone();
        SolucionAux.remove(SolucionAux.size()-1);
        
        //Ahora añadimos los costes las conexiones con la posicion 1 de la nueva solucion        
        nuevoCoste += mCostes[SolucionAux.get(izPos1) - 1][SolucionAux.get(pos1) - 1];
        nuevoCoste += mCostes[SolucionAux.get(pos1) - 1][SolucionAux.get(dePos1) - 1];
        //Ahora añadimos los costes las conexiones con la posicion 1 de la nueva solucion
        nuevoCoste += mCostes[SolucionAux.get(izPos2) - 1][SolucionAux.get(pos2) - 1];
        nuevoCoste += mCostes[SolucionAux.get(pos2) - 1][SolucionAux.get(dePos2) - 1];
        return nuevoCoste;
    }*/

    public int calcularCoste(ArrayList<Integer> nuevaSolucion) {
        int nuevoCoste = 0;

        for (int i = 0; i < nuevaSolucion.size() - 1; i++) {
            nuevoCoste += mCostes[nuevaSolucion.get(i) - 1][nuevaSolucion.get(i + 1) - 1];
        }

        return nuevoCoste;
    }

    public ArrayList<Integer> busquedaLocalElMejor(Integer semilla) {
        this.semilla = semilla;

        rand = new Random(this.semilla);

        //nicializamos los valores necesarios
        int costeSCandidata;
        int mejorCoste;
        int contador = 0;
        int mejorCosteVecinos;
        int ciudadI;
        int ciudadJ;

        ArrayList<Integer> solucionCandidata;
        ArrayList<Integer> mejorSolucion;
        ArrayList<Integer> mejorSolucionVecinos;
        int repeticiones = Ficheros.cX.size() * 1600;
        int numCiudades;
        numEvaluaciones = 0;

        //Buscamos una solucion aleatoria
        mejorSolucionVecinos = solucionAleatoria();
        mejorCosteVecinos = this.coste;

        numCiudades = mejorSolucionVecinos.size() - 1;//Para el limite de los for
        do {
            mejorSolucion = mejorSolucionVecinos;
            mejorCoste = mejorCosteVecinos;
            for (int i = 0; i < numCiudades - 1; i++) {//vamos a cambiar la posicion i por la posicion j buscando la mejor solucion
                //la j siempre va a ser mayor que la i, para que no se repitan vecinos
                for (int j = i + 1; j < numCiudades; j++) {
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
                    //Vemos si es el mejor vecino
                    if (costeSCandidata < mejorCosteVecinos) {
                        mejorCosteVecinos = costeSCandidata;
                        mejorSolucionVecinos = solucionCandidata;
                    }
                    contador++;
                }
            }

            // contador++;
        } while (mejorCoste > mejorCosteVecinos && contador <= repeticiones);

        this.coste = mejorCoste;

        return mejorSolucion;
    }

    public ArrayList<Integer> busquedaLocalElPrimerMejor(Integer semilla) {
        this.semilla = semilla;

        rand = new Random(this.semilla);

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
        numEvaluaciones = 0;

        //Buscamos una solucion aleatoria
        solucionCandidata = solucionAleatoria();
        costeSCandidata = this.coste;

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

        this.coste = mejorCoste;

        return mejorSolucion;
    }
}
