/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo
 */
public class Ficheros {

    public static ArrayList<Integer> indice;
    public static ArrayList<Double> cX;
    public static ArrayList<Double> cY;

    public static void leerFichero(String nombre) throws IOException {
        BufferedReader br = null;

        indice = new ArrayList<Integer>();
        cX = new ArrayList<>();
        cY = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader("./zciudades/" + nombre + ".tsp"));
            String line = br.readLine();//Leemos una linea

            while (null != line) {//Mientras leamos una linea continuamos
                String[] partes = line.split(";");//Dividimos los datos separandolos por el " "
                if (partes.length != 3) {
                    throw new Exception();
                }

                indice.add(Integer.parseInt(partes[0]));
                cX.add(Double.parseDouble(partes[1]));
                cY.add(Double.parseDouble(partes[2]));
                line = br.readLine();//Leemos una linea
            }

        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        } finally {
            if (null != br) {
                br.close();
            }
        }

    }

    public static void guardarSolucion(String nombre,ArrayList<Integer> s,Integer coste,Integer semilla,Integer numEvaluaciones) throws IOException {
        BufferedWriter bw=null;
        try {
            File fichero = new File("./zsoluciones/" + nombre + ".txt");
            
            bw = new BufferedWriter(new FileWriter(fichero));
            
            if(semilla!=null) bw.write("Semilla: "+semilla+"\n");
            bw.write("Numero de evaluaciones: "+numEvaluaciones+"\n");
            bw.write("Coste: "+coste+"\n");
            for(Integer i: s){
                bw.write(i+" ");
            }
            
            
        } catch (Exception ex) {
            System.out.println("error en la generacion del fichero "+ nombre);
        } finally {
            if (null != bw) {
                bw.close();
            }
        }
    }
    
    public static void guardarSolucionesMultiples(String nombre,ArrayList<Integer> s,Integer coste,Integer semilla,Integer numEvaluaciones,Integer iteracion) throws IOException {
        //La diferencia con el el otro metodo de guardar, es que este no sobrescribe le fichero y agrega la nueva informacion despues de la anterior
        BufferedWriter bw=null;
        try {
            File fichero = new File("./zsoluciones/" + nombre + ".txt");
            if(!fichero.exists()) 
                fichero.createNewFile();
            
            bw = new BufferedWriter(new FileWriter(fichero.getAbsoluteFile(),true));
            bw.write("Iteracion: "+iteracion+"\n");
            if(semilla!=null) bw.write("Semilla: "+semilla+"\n");
            bw.write("Numero de evaluaciones: "+numEvaluaciones+"\n");
            bw.write("Coste: "+coste+"\n");
            for(Integer i: s){
                bw.write(i+" ");
            }
            bw.write("\n\n\n");
            
            
        } catch (Exception ex) {
            System.out.println("error en la generacion del fichero "+ nombre);
        } finally {
            if (null != bw) {
                bw.close();
            }
        }
    }
    

}
