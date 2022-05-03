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
public class ILS {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;

    public ILS() {
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

        numEvaluaciones++;
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
    
     public ArrayList<Integer> barajarSubLista(ArrayList<Integer> subLista) {
        ArrayList<Integer> nuevaSubLista = new ArrayList<Integer>();
        ArrayList<Integer> ciudadesDisponibles = subLista;
        int posicionCiudadEscogida;

        //Esta parte trata la primera solucion escogida con el proposito de evitar un "if" en el bucle para solo la primera iteracion
        posicionCiudadEscogida = rand.nextInt(ciudadesDisponibles.size());
        nuevaSubLista.add(ciudadesDisponibles.get(posicionCiudadEscogida));
        ciudadesDisponibles.remove(posicionCiudadEscogida);

        while (!ciudadesDisponibles.isEmpty()) {

            posicionCiudadEscogida = rand.nextInt(ciudadesDisponibles.size());

            nuevaSubLista.add(ciudadesDisponibles.get(posicionCiudadEscogida));
            ciudadesDisponibles.remove(posicionCiudadEscogida);
        }
        nuevaSubLista.add(nuevaSubLista.get(0));

        return nuevaSubLista;
    }
     
    private ArrayList<Integer> mutar(ArrayList<Integer> mejorSolucion) {
        ArrayList<Integer> subLista=new ArrayList<>();
        ArrayList<Integer> nuevaSolucion=(ArrayList<Integer>) mejorSolucion.clone();
        int tamaño=(mejorSolucion.size()-1)/4;
        
        int rand=this.rand.nextInt(mejorSolucion.size()-1-tamaño);//genero un numero aleatorio entre 0 y el tamaño de la lista menos el tamaño de la sublista
        //separo la sublista
        for (int i = 0; i < tamaño; i++) {
            subLista.add(mejorSolucion.get(rand+i));
        }
        subLista=barajarSubLista(subLista);
        
        for (int i = 0; i < tamaño; i++) {//cambio la vieja sublista por la nueva barajada
            nuevaSolucion.set(rand+i, subLista.get(i));
        }
        if(rand==0) nuevaSolucion.set(nuevaSolucion.size()-1, nuevaSolucion.get(0));
        
        return nuevaSolucion;
    }

    public ArrayList<Integer> calcularSolucionILS(int semilla) {
        this.semilla = semilla;
        this.rand = new Random(semilla);
        numEvaluaciones = 0;

        ArrayList<Integer> mejorSolucion = null;
        Integer mejorCoste = Integer.MAX_VALUE;
        ArrayList<Integer> SolucionCandidata;
        ArrayList<Integer> solucionMutada;
        Integer CosteCandidata;
        
        SolucionCandidata=busquedaLocalElPrimerMejor(solucionAleatoria());
        mejorSolucion=SolucionCandidata;
        mejorCoste=calcularCoste(mejorSolucion);
        for(int i=0;i<49;i++){
            solucionMutada=mutar(mejorSolucion);
            SolucionCandidata=busquedaLocalElPrimerMejor(solucionMutada);
            CosteCandidata=calcularCoste(SolucionCandidata);
            if(mejorCoste>CosteCandidata){
                mejorSolucion=SolucionCandidata;
                mejorCoste=CosteCandidata;
            }
        }
        this.coste=mejorCoste;
        return mejorSolucion;
    }

    

}
