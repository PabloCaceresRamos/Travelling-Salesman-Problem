/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author pablo
 */
public class CHC {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;
    //datos para las gráficas
    ArrayList<Double> medias;
    ArrayList<Integer> mejor;

    public CHC() {
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
        // solucion.add(solucion.get(0));

        return solucion;
    }

    private Double calcularMedia(ArrayList<Integer> costes) {
        double suma = 0.0;
        for (Integer coste : costes) {
            suma += coste;
        }
        return suma / costes.size();
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

    public ArrayList<ArrayList<Integer>> selectR(ArrayList<ArrayList<Integer>> poblacion) {
        //devuelvo la poblacion barajada
        ArrayList<ArrayList<Integer>> solucionesDisponibles = (ArrayList<ArrayList<Integer>>) poblacion.clone();
        ArrayList<ArrayList<Integer>> elite = new ArrayList<>();

        //mientras no hayamos rellenado todo el elite, vamos seleccionando aleatoriamente soluciones de la poblacion
        int posSolucionEscogida;

        //Esta parte trata la primera solucion escogida con el proposito de evitar un "if" en el bucle para solo la primera iteracion
        posSolucionEscogida = rand.nextInt(solucionesDisponibles.size());
        elite.add(solucionesDisponibles.get(posSolucionEscogida));
        solucionesDisponibles.remove(posSolucionEscogida);

        while (!solucionesDisponibles.isEmpty()) {

            posSolucionEscogida = rand.nextInt(solucionesDisponibles.size());

            elite.add(solucionesDisponibles.get(posSolucionEscogida));
            solucionesDisponibles.remove(posSolucionEscogida);
        }

        return elite;
    }

    private int getMejor(ArrayList<Integer> costes) {
        Integer mejor = costes.get(0);
        int posMejor = 0;
        for (int i = 1; i < costes.size(); i++) {
            if (costes.get(i) < mejor) {
                posMejor = i;
                mejor = costes.get(i);
            }
        }
        return posMejor;
    }

    private int getPeor(ArrayList<Integer> costes) {
        Integer peor = costes.get(0);
        int posPeor = 0;
        for (int i = 1; i < costes.size(); i++) {
            if (costes.get(i) > peor) {
                posPeor = i;
                peor = costes.get(i);
            }
        }
        return posPeor;
    }

    public ArrayList<ArrayList<Integer>> selectS(ArrayList<ArrayList<Integer>> poblacion, ArrayList<ArrayList<Integer>> elite2, ArrayList<Integer> costes, ArrayList<Integer> costeElite2) {
        ArrayList<ArrayList<Integer>> nuevaPoblacion = (ArrayList<ArrayList<Integer>>) poblacion.clone();
        ArrayList<Integer> costesNuevos = (ArrayList<Integer>) costes.clone();

        ArrayList<ArrayList<Integer>> eliteAux = (ArrayList<ArrayList<Integer>>) elite2.clone();
        ArrayList<Integer> costesEliteAux = (ArrayList<Integer>) costeElite2.clone();

        
        if (!costesEliteAux.isEmpty()) {
            boolean cambio = true;
            do {
                int mejorPosElite = getMejor(costesEliteAux);
                int peorPosPoblacion = getPeor(costesNuevos);

                if (costesEliteAux.get(mejorPosElite) < costesNuevos.get(peorPosPoblacion)) {
                    costesNuevos.set(peorPosPoblacion, costesEliteAux.get(mejorPosElite));
                    nuevaPoblacion.set(peorPosPoblacion, eliteAux.get(mejorPosElite));
                    eliteAux.remove(mejorPosElite);
                    costesEliteAux.remove(mejorPosElite);
                } else {
                    cambio = false;
                }
                if(costesEliteAux.isEmpty())
                    cambio=false;//si la lista esta vacia, me salgo
                
            } while (cambio);
        }

    return nuevaPoblacion ;
}

private int calcularDistanciaHamming(ArrayList<Integer> s1, ArrayList<Integer> s2) {
        //miramos cuantas diferencais hay entre cada solucion
        int diferencias = 0;
        for (int i = 0; i < s1.size(); i++) {
            if (s1.get(i).intValue() != s2.get(i).intValue()) {
                diferencias++;
            }
        }
        return diferencias;
    }

    public ArrayList<Integer> OXStandar(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        //Hacemos la unión de dos soluciones.
        ArrayList<Integer> hijo = new ArrayList<>();
        int tamañoSubLista = (int) (p1.size() * 0.80f);//vamos a coger como sublista un 80% del tamaño
        int inicioSubLista = this.rand.nextInt(p1.size());
        int finSubLista = (inicioSubLista + tamañoSubLista - 1) % p1.size();
        int[] listAux = new int[p1.size()];

        ArrayList<Integer> p1SubLista = new ArrayList<>();

        //cojo la sublista el padre
        for (int i = 0; i < tamañoSubLista; i++) {
            p1SubLista.add(p1.get((inicioSubLista + i) % p1.size()));
        }

        ArrayList<Integer> p2SinSubLista = new ArrayList<>();//cojo las ciudades que no esten en la sublista del padre 2
        for (int i = 0; i < p2.size(); i++) {
            if (p1SubLista.indexOf(p2.get(i)) == -1) {
                p2SinSubLista.add(p2.get(i));
            }
        }
        //relleno el hijo auxiliar
        for (int i = inicioSubLista; i % p1.size() != (finSubLista + 1) % p1.size(); i++) { //desde que empieza la sublista hasta que estamos en la ultima posicion de la sublista
            listAux[i % p1.size()] = p1SubLista.get(i - inicioSubLista);
        }

        for (int i = finSubLista + 1; i % p1.size() != inicioSubLista; i++) {//desde el fin de la sublista hasta el principio
            listAux[i % p1.size()] = p2SinSubLista.get(0);
            p2SinSubLista.remove(0);
        }
        //paso del hijo auxiliar al hijo
        for (int i = 0; i < p1.size(); i++) {
            hijo.add(listAux[i]);
        }
        return hijo;
    }

    public ArrayList<ArrayList<Integer>> recombine(ArrayList<ArrayList<Integer>> elite, int d) {
        ArrayList<ArrayList<Integer>> elite2 = new ArrayList<>();

        for (int i = 0; i < elite.size(); i += 2) {//visitamos todos los pares

            if (calcularDistanciaHamming(elite.get(i), elite.get(i + 1)) / 2 > d) {//Si la distancia Hamming es mas grande que la que buscamos, combinamos el par en 2 hijos

                ArrayList<ArrayList<Integer>> hijos = new ArrayList<>();
                hijos.add(OXStandar(elite.get(i), elite.get(i + 1)));
                hijos.add(OXStandar(elite.get(i + 1), elite.get(i)));

                for (ArrayList<Integer> hijo : hijos) {
                    elite2.add(hijo);
                }

            }
        }

        return elite2;
    }

    private boolean cambio(ArrayList<Integer> costeP, ArrayList<Integer> costesNP) {
        //miro si son iguales, en caso contrario, ha habido un cambio
        for (int i = 0; i < costeP.size(); i++) {
            if (costeP.get(i).intValue() != costesNP.get(i).intValue()) {
                return true;
            }
        }
        return false;

    }

    private ArrayList<ArrayList<Integer>> diverge(ArrayList<Integer> mejor, int tamañoPoblacion) {
        ArrayList<ArrayList<Integer>> poblacion = new ArrayList<>();
        poblacion.add(mejor);//añadimos el mejor encontrado hasta el momento.
        //Generamos aleatoriamente el resto de la poblacion
        for (int i = 1; i < tamañoPoblacion; i++) {//generamos las n soluciones que forman la población
            ArrayList<Integer> solucionAleatoriaAux = this.solucionAleatoria();
            poblacion.add(solucionAleatoriaAux);
        }
        return poblacion;
    }

    public ArrayList<Integer> calcularCHC(int semilla, int tamañoPoblacion,int reinicios) throws IOException {
        this.semilla = semilla;
        this.rand = new Random(semilla);
        this.numEvaluaciones = 0;
        this.medias = new ArrayList<>();
        this.mejor = new ArrayList<>();

        int t = 0;
        int d = tamañoPoblacion / 4;

        ArrayList<ArrayList<Integer>> poblacion = new ArrayList<>();
        ArrayList<Integer> costes = new ArrayList<>();
        int posMejor = -1;
        ArrayList<Integer> mejor = new ArrayList<>();
        Integer costeMejor;

        for (int i = 0; i < tamañoPoblacion; i++) {//generamos las n soluciones que forman la población
            ArrayList<Integer> solucionAleatoriaAux = this.solucionAleatoria();
            poblacion.add(solucionAleatoriaAux);
            costes.add(this.calcularCoste(solucionAleatoriaAux));
            numEvaluaciones++;
            if (posMejor == -1 || costes.get(posMejor) > costes.get(i)) {
                posMejor = i;//nos quedamos con la mejor solución
            }
        }
        mejor = poblacion.get(posMejor);
        costeMejor=calcularCoste(mejor);
        int numReinicios=0;
        while (numReinicios < reinicios) {
            t++;
            //Selección R
            ArrayList<ArrayList<Integer>> elite = selectR(poblacion);
            

            //Recombinación
            ArrayList<ArrayList<Integer>> elite2 = recombine(elite, d);
            

            //evaluar
            ArrayList<Integer> costesElite2 = new ArrayList<>();
            for (int i = 0; i < elite2.size(); i++) {
                costesElite2.add(calcularCoste(elite2.get(i)));
                numEvaluaciones++;
            }
            

            //Selección S
            ArrayList<ArrayList<Integer>> nuevaPoblacion = selectS(poblacion, elite2, costes, costesElite2);
            ArrayList<Integer> costesnuevaPoblacion = new ArrayList<>();

            for (int i = 0; i < nuevaPoblacion.size(); i++) {//calculamos el coste de la nueva población y consigo el nuevo mejor.
                Integer costeAux = calcularCoste(nuevaPoblacion.get(i));
                costesnuevaPoblacion.add(costeAux);
                if (costeAux < costeMejor) {
                    costeMejor = costeAux;
                    mejor = nuevaPoblacion.get(i);
                }
            }

            if (!cambio(costes, costesnuevaPoblacion)) {
                d--;
            }
   

            if (d < 0) {
                //diverge
                poblacion = diverge(mejor, tamañoPoblacion);
                d = tamañoPoblacion / 4;
                numReinicios++;
                //calculamos los costes y buscamos al mejor
                costes=new ArrayList<>();
                for (int i = 0; i < poblacion.size(); i++) {//calculamos el coste de la nueva población y consigo el nuevo mejor.
                    Integer costeAux = calcularCoste(poblacion.get(i));
                    numEvaluaciones++;
                    costes.add(costeAux);
                    if (costeAux < costeMejor) {
                        costeMejor = costeAux;
                        mejor = poblacion.get(i);
                    }
                }
            } else {
                poblacion = nuevaPoblacion;
                costes = costesnuevaPoblacion;
            }
            
            this.mejor.add(costeMejor);
            this.medias.add(calcularMedia(costes));

        }
        this.crearFicheroMediaMejor();
        return mejor;
    }

    public void crearFicheroMediaMejor() throws IOException {
        Ficheros.guardarMediaMejor(medias, mejor, "chc");
    }

}
