/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author pablo
 */
public class SCH {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;
    ArrayList<Integer> mejor;

    public SCH() {
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

    public int calcularCoste(ArrayList<Integer> nuevaSolucion) {
        int nuevoCoste = 0;

        for (int i = 0; i < nuevaSolucion.size() - 1; i++) {
            nuevoCoste += mCostes[nuevaSolucion.get(i) - 1][nuevaSolucion.get(i + 1) - 1];
        }
        nuevoCoste += mCostes[nuevaSolucion.get(0) - 1][nuevaSolucion.get(nuevaSolucion.size() - 1) - 1];//calculamos el coste de ir del ultimo al primero
        numEvaluaciones++;
        return nuevoCoste;
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

        return solucion;
    }
    
    private Integer ciudadMayorArg(Integer ciudadOrigen, ArrayList<Integer> ciudadesNoVisitadas, Double[][] matrizHeuristica, Double[][] matrizFeromonas) {
        
        ArrayList<Double> arg = new ArrayList<>();
        int alpha = 1;
        int beta = 2;
        Integer ciudadMaxArg=null;
        double resultadoCiudadMaxArg=0;
        
        for (Integer c : ciudadesNoVisitadas) {
            double resultado = (Math.pow(matrizFeromonas[ciudadOrigen - 1][c - 1], alpha) * Math.pow(matrizHeuristica[ciudadOrigen - 1][c - 1], beta));
            if(resultado>resultadoCiudadMaxArg|| ciudadMaxArg==null){
                ciudadMaxArg=c;
                resultadoCiudadMaxArg=resultado;
            }
        }
        return ciudadMaxArg;
    }

    private ArrayList<Double> calcularProbabilidad(Integer ciudadOrigen, ArrayList<Integer> ciudadesNoVisitadas, Double[][] matrizHeuristica1, Double[][] matrizFeromonas) throws InterruptedException {
        ArrayList<Double> probabilidades = new ArrayList<>();
        double sumatorio = 0;
        int alpha = 1;
        int beta = 2;
        //Saco la probabilidad de cada ciudad con la formula proporcionada en el tema
        for (Integer c : ciudadesNoVisitadas) {
            sumatorio += Math.pow(matrizFeromonas[ciudadOrigen - 1][c - 1], alpha) * Math.pow(matrizHeuristica1[ciudadOrigen - 1][c - 1], beta);
        }

        for (Integer c : ciudadesNoVisitadas) {
            double resultado = (Math.pow(matrizFeromonas[ciudadOrigen - 1][c - 1], alpha) * Math.pow(matrizHeuristica1[ciudadOrigen - 1][c - 1], beta)) / sumatorio;
            probabilidades.add(resultado);
        }

        return probabilidades;

    }

    private Integer escogerCiudad(ArrayList<Double> probabilidad, ArrayList<Integer> ciudadesNoVisitadas) {
        double r = rand.nextDouble();
        double sumProbabilidades = 0;
        for (int i = 0; i < probabilidad.size(); i++) {
            sumProbabilidades += probabilidad.get(i);
            if (r < sumProbabilidades) {
                return ciudadesNoVisitadas.get(i);
            }
        }
        return ciudadesNoVisitadas.get(ciudadesNoVisitadas.size() - 1);//La suma de las probabilidades puesde ser 0.999999999999998, si sale la probabilidad 0.999999999999999 daria fallo, por lo que devuelvo el último elemento
    }

    public ArrayList<Integer> calcularSCH(int semilla, int numHormigas) throws InterruptedException, IOException {
        this.semilla = semilla;
        this.rand = new Random(semilla);
        this.mejor=new ArrayList<>();
        this.numEvaluaciones=0;

        int numCiudades = this.mCostes.length;
        Double[][] matrizHeuristica = new Double[numCiudades][numCiudades];
        Double[][] matrizFeromonas = new Double[numCiudades][numCiudades];
        ArrayList<Integer> mejorCaminoGlobal = new ArrayList<>();
        Integer costeMejorGlobal = -1;
        int costeGreedy = calcularCoste(solucionGreedy());
        double feromonaInicial = 1.0 / (numCiudades * costeGreedy);
        double q0 = 0.98;

        //Inicializamos la matriz de Heuristica
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i != j) {
                    if (mCostes[i][j] == 0) {
                        matrizHeuristica[i][j] = 1.0 / 0.5;
                    } else {
                        matrizHeuristica[i][j] = 1.0 / mCostes[i][j];
                    }
                }
            }

        }

        //Inicializamos la matriz de feromonas
        for (int i = 0; i < numCiudades; i++) {
            for (int j = 0; j < numCiudades; j++) {
                if (i != j) {
                    matrizFeromonas[i][j] = feromonaInicial;
                }
            }

        }

       Calendar horaInicial= Calendar.getInstance();//Obtenemos la hora antes de empezar a ejecutar
        horaInicial.add(Calendar.MINUTE, 10);//Adelantamos 5 minutos
        //for (int it = 0; it < 10000 * numCiudades; it++) {
        while(tiempoNoTerminado(horaInicial)){
            ArrayList<ArrayList<Integer>> caminosHormigas = new ArrayList<>();
            ArrayList<Integer> costesCaminos = new ArrayList<>();
            ArrayList<Integer> mejorCaminoActual = new ArrayList<>();
            Integer costeMejorActual = -1;

            for (int i = 0; i < numHormigas; i++) {//Recorremos todas las hormigas
                ArrayList<Integer> caminoHormigaActual = new ArrayList<>();
                ArrayList<Integer> ciudadesNoVisitadas = (ArrayList<Integer>) Ficheros.indice.clone();

                //Cogemos una ciudad inicial aleatoria
                Integer ciudadInicial = rand.nextInt(ciudadesNoVisitadas.size()) + 1;
                caminoHormigaActual.add(ciudadInicial);
                ciudadesNoVisitadas.remove(ciudadInicial);

                while (!ciudadesNoVisitadas.isEmpty()) {//Recorremos todas las ciudades con cada hormiga
                    Integer ciudadSiguiente=null;
                    double q = rand.nextDouble();
                    //Realizamos la regla de transición del SCH
                    if (q < q0) {//----------------------------------------------------------------------------------------------------------------------------------------------------------
                        ciudadSiguiente=ciudadMayorArg(caminoHormigaActual.get(caminoHormigaActual.size() - 1), ciudadesNoVisitadas, matrizHeuristica, matrizFeromonas);
                    } else {
                        //calculo la del SH
                        ArrayList<Double> probabilidad = calcularProbabilidad(caminoHormigaActual.get(caminoHormigaActual.size() - 1), ciudadesNoVisitadas, matrizHeuristica, matrizFeromonas);
                        ciudadSiguiente = escogerCiudad(probabilidad, ciudadesNoVisitadas);
                    }
                    caminoHormigaActual.add(ciudadSiguiente);
                    ciudadesNoVisitadas.remove(ciudadSiguiente);
                }

                //Calculamos el coste del camino y lo añadimos a la lista
                Integer costeCaminoActual = calcularCoste(caminoHormigaActual);
                caminosHormigas.add(caminoHormigaActual);
                costesCaminos.add(costeCaminoActual);

                if (costeCaminoActual < costeMejorActual || costeMejorActual == -1) {//Actualizamos el mejor de la iteracíon
                    mejorCaminoActual = caminoHormigaActual;
                    costeMejorActual = costeCaminoActual;
                }

                //actualización localc de feronomas
                for (int j = 1; j < caminoHormigaActual.size(); j++) {//-------------------------------------------------------------------------------------------------------------------------
                    double actualizaciónLocal = 0.1 * feromonaInicial;
                    matrizFeromonas[j - 1][j] *= (1 - 0.1);
                    matrizFeromonas[j - 1][j] += actualizaciónLocal;

                    matrizFeromonas[j][j - 1] *= (1 - 0.1);
                    matrizFeromonas[j][j - 1] += actualizaciónLocal;
                }

            }

            if (costeMejorActual < costeMejorGlobal || costeMejorGlobal == -1) {//Actualizamos el mejor global
                mejorCaminoGlobal = mejorCaminoActual;
                costeMejorGlobal = costeMejorActual;
            }

            //actualización globlar sobre el mejor camino global
            //reduzco las feromonas del mejor camino un 10% y le sumo un 10% del aporte
            float p = 0.1f;
            for (int i = 1; i < mejorCaminoGlobal.size(); i++) {//--------------------------------------------------------------------------------------------------------------------------------
                int pos1 = mejorCaminoGlobal.get(i - 1);
                int pos2 = mejorCaminoGlobal.get(i);

                matrizFeromonas[pos1 - 1][pos2 - 1] *= 1.0f - p;
                matrizFeromonas[pos1 - 1][pos2 - 1] += p * (1 / costeMejorGlobal);
                //Como el algoritmo trabaja con media matriz y nosotros utilizamos toda la matriz, hacemos la actualización para la otra mitad
                matrizFeromonas[pos2 - 1][pos1 - 1] *= 1.0f - p;
                matrizFeromonas[pos2 - 1][pos1 - 1] += p * (1 / costeMejorGlobal);

            }
            mejor.add(costeMejorGlobal);
        }
        this.crearFicheroMejorTiempo();
        return mejorCaminoGlobal;
    }

    private boolean tiempoNoTerminado(Calendar horaInicial) {
        Calendar horaActual= Calendar.getInstance();//Obtenemos la hora actual
        int h=horaInicial.get(Calendar.MINUTE);
        if(horaActual.after(horaInicial))//Comparamos si ha pasado el tiempo establecido para parar la ejecución
            return false;
        else 
            return true;
    }
    
       public void crearFicheroMejorTiempo() throws IOException{
        Ficheros.guardarMejorTiempo(mejor,"chc");
     }

}
