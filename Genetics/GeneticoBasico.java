/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class GeneticoBasico {

    private Integer[][] mCostes;//Matriz de costes 
    public int coste;
    private Random rand;
    public int semilla;
    public int numEvaluaciones;
    //datos para las gráficas
    ArrayList<Double> medias;
    ArrayList<Integer> mejor;

    public GeneticoBasico() {
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

    public int calcularCoste(ArrayList<Integer> nuevaSolucion) {
        int nuevoCoste = 0;

        for (int i = 0; i < nuevaSolucion.size() - 1; i++) {
            nuevoCoste += mCostes[nuevaSolucion.get(i) - 1][nuevaSolucion.get(i + 1) - 1];
        }
        nuevoCoste += mCostes[nuevaSolucion.get(0) - 1][nuevaSolucion.get(nuevaSolucion.size() - 1) - 1];//calculamos el coste de ir del ultimo al primero
        numEvaluaciones++;
        return nuevoCoste;
    }
    
     public ArrayList<Integer> OXStandar(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        //Hacemos la unión de dos soluciones.
        ArrayList<Integer> hijo = new ArrayList<>();
        int tamañoSubLista = (int) (p1.size() *0.80f);//vamos a coger como sublista un 80% del tamaño
        int inicioSubLista = this.rand.nextInt(p1.size());
        int finSubLista=(inicioSubLista+tamañoSubLista-1)%p1.size();
        int []listAux=new int [p1.size()];

        ArrayList<Integer> p1SubLista = new ArrayList<>();

        //cojo la sublista el padre
        for (int i = 0; i < tamañoSubLista; i++) {
            p1SubLista.add(p1.get((inicioSubLista + i)%p1.size()));
        }

        ArrayList<Integer> p2SinSubLista = new ArrayList<>();//cojo las ciudades que no esten en la sublista del padre 2
        for (int i = 0; i < p2.size(); i++) {
            if (p1SubLista.indexOf(p2.get(i)) == -1) {
                p2SinSubLista.add(p2.get(i));
            }
        }
        //relleno el hijo auxiliar
         for (int i = inicioSubLista; i%p1.size() != (finSubLista+1)%p1.size(); i++) { //desde que empieza la sublista hasta que estamos en la ultima posicion de la sublista
             listAux[i%p1.size()]=p1SubLista.get(i-inicioSubLista);
         }
         
         for (int i = finSubLista+1; i%p1.size() != inicioSubLista; i++) {//desde el fin de la sublista hasta el principio
             listAux[i%p1.size()]=p2SinSubLista.get(0);
             p2SinSubLista.remove(0);
         }
         //paso del hijo auxiliar al hijo
         for (int i = 0; i < p1.size(); i++) {
             hijo.add(listAux[i]);
         }
        return hijo;
    }

    public ArrayList<ArrayList<Integer>> crowding(ArrayList<ArrayList<Integer>> poblacion, ArrayList<Integer> costes, ArrayList<Integer> hijo1, ArrayList<Integer> hijo2, int costeH1, int costeH2, int posP1, int posP2) {
        //nos llega los padres y los hijos y devolvemos los 2 mejores de los 4
        ArrayList<Integer> costesPadresHijos = new ArrayList<>();
        costesPadresHijos.add(costeH1);
        costesPadresHijos.add(costeH2);
        costesPadresHijos.add(costes.get(posP2));
        costesPadresHijos.add(costes.get(posP1));
        ArrayList<ArrayList<Integer>> poblacionPadresHijos = new ArrayList<>();
        poblacionPadresHijos.add(hijo1);
        poblacionPadresHijos.add(hijo2);
        poblacionPadresHijos.add(poblacion.get(posP2));
        poblacionPadresHijos.add(poblacion.get(posP1));
        int posPeor = 0;
        //vuelta para eliminar al peor
        for (int i = 1; i < costesPadresHijos.size(); i++) {
            if (costesPadresHijos.get(i) > costesPadresHijos.get(posPeor)) {
                posPeor = i;
            }
        }

        poblacionPadresHijos.remove(posPeor);
        costesPadresHijos.remove(posPeor);
        posPeor = 0;
        //vuelta para eliminar al 2º peor
        for (int i = 1; i < costesPadresHijos.size(); i++) {
            if (costesPadresHijos.get(i) > costesPadresHijos.get(posPeor)) {
                posPeor = i;
            }
        }
        poblacionPadresHijos.remove(posPeor);
        costesPadresHijos.remove(posPeor);

        return poblacionPadresHijos;
    }

    private ArrayList<Double> getProbabilidadesInversas(ArrayList<Integer> costes) {

        ArrayList<Double> probabilidades = new ArrayList<>();

        double sumDif = 0;//esto se va a utilizar para la formula de la probabilidad

        for (int i = 0; i < costes.size(); i++) {
            sumDif += 1.0 / costes.get(i);
        }
        //probabilidad=(1/dist)/sumatorio(1/distancias)
        double sum = 0;
        for (int i = 0; i < costes.size(); i++) {
            double coste = costes.get(i);
            double prob = (1.0 / coste) / sumDif;
            probabilidades.add(prob);
            sum += prob;
        }

        return probabilidades;
    }

    private int[] ruletaInversamenteProporcional(ArrayList<Integer> costePoblacion) {
        int[] resultados = new int[2];
        ArrayList<Integer> costes = (ArrayList<Integer>) costePoblacion.clone();
        double probabilidadP;
            for (int i = 0; i < 2; i++) {
                ArrayList<Double> probabilidad = getProbabilidadesInversas(costes);
                probabilidadP = rand.nextDouble();
                double sumProbabilidad = 0.0;
                for (int j = 0; j < probabilidad.size(); j++) {
                    if (probabilidadP < probabilidad.get(j) + sumProbabilidad) {
                        resultados[i] = j;
                        break;
                    }
                    sumProbabilidad+=probabilidad.get(j);
                }
                costes.set(resultados[i], Integer.MAX_VALUE);//le doy un valor muy grande al seleccionado para que en la siguiente vuelta tenga probabilidad 0.
            }

        return resultados;
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

    private ArrayList<Integer> mutar(ArrayList<Integer> solucion, int porcentajeTamaño) {
        ArrayList<Integer> subLista = new ArrayList<>();
        ArrayList<Integer> nuevaSolucion = (ArrayList<Integer>) solucion.clone();
        int tamaño = (int)((porcentajeTamaño/100.0)*solucion.size());

        int rand = this.rand.nextInt(solucion.size() - tamaño);//genero un numero aleatorio entre 0 y el tamaño de la lista menos el tamaño de la sublista
        //separo la sublista
        for (int i = 0; i < tamaño; i++) {
            subLista.add(solucion.get(rand + i));
        }
        subLista = barajarSubLista(subLista);

        for (int i = 0; i < tamaño; i++) {//cambio la vieja sublista por la nueva barajada
            nuevaSolucion.set(rand + i, subLista.get(i));
        }

        return nuevaSolucion;
    }
    
    private Double calcularMedia(ArrayList<Integer> costes) {
        double suma=0.0;
        for (Integer coste : costes) {
            suma+=coste;
        }
        return suma/costes.size();
    }

    public ArrayList<Integer> calcularGeneticoBasico(int semilla, int tamañoPoblacion,float pm,int tm) throws IOException {
        this.semilla = semilla;
        this.rand = new Random(semilla);
        this.numEvaluaciones = 0;
        this.medias=new ArrayList<>();
        this.mejor=new ArrayList<>();
        double probabilidadMutacion = /*0.10*/pm;
        int tamañoMutacion = /*10*/tm;// % a mutar
        int costeMejor;
         ArrayList<Integer> mejor;

        ArrayList<ArrayList<Integer>> poblacion = new ArrayList<>();
        ArrayList<Integer> costes = new ArrayList<>();
        int posMejor = -1;

        for (int i = 0; i < tamañoPoblacion; i++) {//generamos las n soluciones que forman la población
            ArrayList<Integer> solucionAleatoriaAux = this.solucionAleatoria();
            poblacion.add(solucionAleatoriaAux);
            costes.add(this.calcularCoste(solucionAleatoriaAux));
            numEvaluaciones++;
            if (posMejor == -1 || costes.get(posMejor) > costes.get(i)) {
                posMejor = i;//nos quedamos con la mejor solución
            }
        }
        costeMejor=costes.get(posMejor);
        mejor=poblacion.get(posMejor);
        
        int contSinMejora = 0;
        int i=1;
        boolean cambio;//controla si ha cambiado de mejor
        while (contSinMejora < 10000) {//se sale al no encontrar una mejor solución en 1000 iteraciones seguidas
          i++;
            if(i==375)
                System.out.println("");
            cambio = false;
            //Hago una ruleta proporcional al coste para sacar los padres
            int[] pos = ruletaInversamenteProporcional(costes);
            //int[] pos = posrand(costes);
            int posP1 = pos[0];
            int posP2 = pos[1];

            //Calculamos 2 hijos segun los padres
            ArrayList<Integer> hijo1 = OXStandar(poblacion.get(posP2), poblacion.get(posP1));
            ArrayList<Integer> hijo2 = OXStandar(poblacion.get(posP1), poblacion.get(posP2));
            

            // mutamos los hijos
            if (rand.nextDouble() < probabilidadMutacion) {
                hijo1 = mutar(hijo1, tamañoMutacion);
            }
            if (rand.nextDouble() < probabilidadMutacion) {
                hijo2 = mutar(hijo2, tamañoMutacion);
            }
            
            int costeH1 = calcularCoste(hijo1);
            int costeH2 = calcularCoste(hijo2);
            numEvaluaciones+=2;
            
            //implementamos el crowdin 
            ArrayList<ArrayList<Integer>> MejoresPH = crowding(poblacion, costes, hijo1, hijo2, costeH1, costeH2, posP1, posP2);
            int nuevosCostes;
            //remplazamos la 1º posicion y miramos si es el mejor coste
            poblacion.set(posP1, MejoresPH.get(0));
            nuevosCostes = calcularCoste(MejoresPH.get(0));
            if (nuevosCostes < costeMejor) {
                mejor=MejoresPH.get(0);
                costeMejor=nuevosCostes;
                cambio = true;
            }
            costes.set(posP1, nuevosCostes);
            //remplazamos la 2º posicion y miramos si es el mejor coste
            poblacion.set(posP2, MejoresPH.get(1));
            nuevosCostes = calcularCoste(MejoresPH.get(1));
            if (nuevosCostes < costeMejor) {
                mejor=MejoresPH.get(1);
                costeMejor=nuevosCostes;
                cambio = true;
            }
            costes.set(posP2, nuevosCostes);

            //compuebo si ha habido mejora
            if (!cambio) {
                contSinMejora++;
            } else {
                contSinMejora = 0;
            }
            
            this.mejor.add(costeMejor);
            this.medias.add(calcularMedia(costes));

        }
        //this.crearFicheroMediaMejor();
        return mejor;
    }
    
    public void mostrarMejorMedia(){
        for (Integer mejor : this.mejor) {
            System.out.print(mejor+";");
        }
        System.out.println("\n");
        for(Double media:this.medias){
            System.out.print(media+";");
        }
    }
    
    public void crearFicheroMediaMejor() throws IOException{
        Ficheros.guardarMediaMejor(medias, mejor,"gb");
    }

    private int[] posrand(ArrayList<Integer> costes) {
        int pos[]=new int [2];
        do{
            pos[0]=rand.nextInt(costes.size());
            pos[1]=rand.nextInt(costes.size());
        }while(pos[0]==pos[1]);
        return pos;
    }

    
}
