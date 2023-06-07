package clases;


import excepciones.RegistroExcepcion;
import excepciones.TempExcepecion;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SSM {
        private Map<String, Persona> personasMap;
        private int cantReactivos;private boolean hayTest;
    private Map<Integer, RegistroTemperatura> tablaTemperaturas;

    public SSM(int cantReactivos) {
        this.personasMap = new HashMap<>();
        this.cantReactivos = cantReactivos;
        this.tablaTemperaturas = new HashMap<>();
    }

    public void addPersona(Persona nuevaPersona) throws RegistroExcepcion {

        if(cantReactivos<=0){
            consultarPorTestAlSSM(100);
            throw new RegistroExcepcion("No hay reactivos <!>");
        }
        if(personasMap.containsKey(nuevaPersona.getDni())){
            throw new RegistroExcepcion("Ese DNI ya se encuentra registrado en el sistema");
        }
        Random random = new Random();
        nuevaPersona.setNumeroKit(random.nextInt(0,100000));


        personasMap.put(nuevaPersona.getDni(), nuevaPersona);
        cantReactivos--;
    }

    public void consultarPorTestAlSSM(int nuevaCantidad){
        if(hayTest){
            cantReactivos+=nuevaCantidad;
        }
    }

    public String listarPersonas(){
        String lista = "No hay personas";
        if(!personasMap.isEmpty()){
            lista = "";
            for(Map.Entry<String,Persona> entry:personasMap.entrySet()){
                lista+=entry.getValue().toString();
            }
        }
        return lista;
    }

    private double generarTemperaturaAleatoria() {
        Random random = new Random();
        return 36 + random.nextDouble() * (39 - 36);
    }

    public void testear(int numeroKit){
        for(Map.Entry<String,Persona> entry:personasMap.entrySet()){
            if(entry.getValue().getNumeroKit() != numeroKit){
                return;
            }else{
                RegistroTemperatura registro = new RegistroTemperatura(entry.getValue().getDni(), generarTemperaturaAleatoria());
                tablaTemperaturas.put(entry.getValue().getNumeroKit(), registro);
                for (Map.Entry<Integer, RegistroTemperatura> entry1:tablaTemperaturas.entrySet()){
                    System.out.println(entry1.getValue().toString());
                    entry.getValue().setTemperatura(entry1.getValue().getTemperatura());
                }
            }
        }
    }

    public void aislar()throws TempExcepecion {
        for(Map.Entry<String, Persona> entry:personasMap.entrySet()){
            try{
                if(entry.getValue().getTemperatura()>38){
                    throw new TempExcepecion("Se debe aislar <!>");
                }
            }catch (TempExcepecion e){
                System.out.println("Excepcion: "+e.getMessage());
                System.out.println("Barrio: "+entry.getValue().getBarrio());
                System.out.println("Numero test: "+entry.getValue().getNumeroKit());
                String barrio = entry.getValue().getBarrio();
                String numTest = Integer.toString(entry.getValue().getNumeroKit());
                try(BufferedWriter writer = new BufferedWriter(new FileWriter("urgente.dat"))){
                    writer.write(barrio);
                    writer.newLine(); // Agregar una nueva línea
                    writer.write(numTest);
                    writer.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public void generarJSON(){
        JSONArray sanosArray = new JSONArray();
        JSONArray aislarArray = new JSONArray();
        for(Map.Entry<Integer, RegistroTemperatura> entry:tablaTemperaturas.entrySet()){
            int numeroKit = entry.getKey();
            RegistroTemperatura registro = entry.getValue();
            Persona persona = new Persona();
            JSONObject personaObject = new JSONObject();
            personaObject.put("nombre", persona.getNombre());
            personaObject.put("apellido", persona.getApellido());
            personaObject.put("edad", persona.getEdad());
            personaObject.put("barrio", persona.getBarrio());
            personaObject.put("DNI", persona.getDni());
            personaObject.put("ocupacion", persona.getOcupacion());

            JSONObject registroObject = new JSONObject();
            registroObject.put("numeroKit", numeroKit);
            registroObject.put("temperatura", registro.getTemperatura());
            registroObject.put("barrio", persona.getBarrio());

            if (registro.getTemperatura() <= 38) {
                sanosArray.put(personaObject);
            } else {
                aislarArray.put(registroObject);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sanos", sanosArray);
            jsonObject.put("aislar", aislarArray);

            try (FileWriter file = new FileWriter("registros.json")) {
                file.write(jsonObject.toString());
                System.out.println("Archivo JSON generado correctamente.");
            } catch (IOException e) {
                System.out.println("Error al generar el archivo JSON: " + e.getMessage());
            }
        }
        }

}
