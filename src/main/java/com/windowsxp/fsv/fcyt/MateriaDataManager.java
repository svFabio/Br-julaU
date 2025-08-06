package com.windowsxp.fsv.fcyt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MateriaDataManager {

    // El nombre del archivo donde guardaremos todo. Se creará en la carpeta raíz del proyecto.
    private static final String RUTA_ARCHIVO = "plan_de_estudios.json";

    // Creamos una instancia de Gson que formateará el JSON de forma legible.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Carga la lista de materias desde el archivo JSON.
     * @return Una ObservableList de Materias. Si el archivo no existe, devuelve null.
     */
    public ObservableList<Materia> cargarMaterias() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("No se encontró el archivo de guardado. Se cargarán los datos por defecto.");
            return null; // Devolvemos null para indicar que se debe usar la lista por defecto.
        }

        try (Reader reader = new FileReader(archivo)) {
            // Esto es necesario para decirle a Gson que queremos convertir el JSON a una Lista de Materias.
            Type tipoListaMaterias = new TypeToken<ArrayList<Materia>>(){}.getType();

            List<Materia> materias = gson.fromJson(reader, tipoListaMaterias);
            System.out.println("Datos cargados correctamente desde " + RUTA_ARCHIVO);
            return FXCollections.observableArrayList(materias);

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de datos: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList(); // Retorna lista vacía en caso de error.
        }
    }

    /**
     * Guarda la lista de materias proporcionada en el archivo JSON.
     * @param materias La lista de materias a guardar.
     */
    public void guardarMaterias(List<Materia> materias) {
        try (Writer writer = new FileWriter(RUTA_ARCHIVO)) {
            // Gson se encarga de convertir la lista de objetos Java a texto JSON.
            gson.toJson(materias, writer);
            System.out.println("Datos guardados correctamente en " + RUTA_ARCHIVO);
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
