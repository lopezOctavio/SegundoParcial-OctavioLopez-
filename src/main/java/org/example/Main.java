package org.example;

import clases.Persona;
import clases.SSM;
import excepciones.RegistroExcepcion;
import excepciones.TempExcepecion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws RegistroExcepcion, TempExcepecion {
        //CREO LA CONTENEDORA CON 100 REACTIVOS, SI SETEO LOS REACTIVOS EN 0 ME TIENE QUE TIRAR LA EXCEPCION QUE NO HAY REGISTROS
        SSM sistema = new SSM(100);

        Persona persona1 = new Persona("Octavio", "Lopez", 29, "Centro", "37868120", "Empleado");
        Persona persona2 = new Persona("Octavio", "Lopez", 29, "Centro", "37868120", "Empleado");
        sistema.addPersona(persona1);
        //SI AGREGO A LA PERSONA 2 ME TIENE QUE TIRAR LA EXCEPCION DEL DNI EXISTENTE YA QUE USE UN HASHMAP E IMPLEMENTE EL HASHCODE
        //sistema.addPersona(persona2);

        //MUESTRO LOS DATOS DEL HASH
        System.out.println(sistema.listarPersonas());



        sistema.testear(persona1.getNumeroKit());
        sistema.aislar();
        sistema.generarJSON();




    }
}