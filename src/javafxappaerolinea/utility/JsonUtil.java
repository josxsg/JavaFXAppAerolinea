/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Dell
 */
public class JsonUtil<T> {
    private final String filePath;
    private final Class<T> type;
    private final Type listType;
    private final Gson gson;
    
    /**
     * Constructor
     * @param filePath Ruta del archivo JSON
     * @param type Clase del tipo de objeto a persistir
     */
    public JsonUtil(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.type = type;
        this.listType = TypeToken.getParameterized(List.class, type).getType();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd")
                .create();
        
        // Crear el directorio si no existe
        File file = new File(filePath);
        File directory = file.getParentFile();
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Guarda una lista de objetos en el archivo JSON
     * @param data Lista de objetos a guardar
     * @throws IOException Si ocurre un error de E/S
     */
    public void saveAll(List<T> data) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            throw new IOException("Error al guardar datos en JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Carga una lista de objetos desde el archivo JSON
     * @return Lista de objetos cargados
     * @throws IOException Si ocurre un error de E/S
     */
    public List<T> loadAll() throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            throw new IOException("Error al cargar datos desde JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Guarda un objeto en el archivo JSON
     * @param object Objeto a guardar
     * @throws IOException Si ocurre un error de E/S
     */
    public void save(T object) throws IOException {
        List<T> data = loadAll();
        data.add(object);
        saveAll(data);
    }
    
    /**
     * Actualiza un objeto en el archivo JSON
     * @param object Objeto a actualizar
     * @param predicate Predicado para identificar el objeto a actualizar
     * @throws IOException Si ocurre un error de E/S
     */
    public void update(T object, java.util.function.Predicate<T> predicate) throws IOException {
        List<T> data = loadAll();
        for (int i = 0; i < data.size(); i++) {
            if (predicate.test(data.get(i))) {
                data.set(i, object);
                break;
            }
        }
        saveAll(data);
    }
    
    /**
     * Elimina un objeto del archivo JSON
     * @param predicate Predicado para identificar el objeto a eliminar
     * @throws IOException Si ocurre un error de E/S
     */
    public void delete(java.util.function.Predicate<T> predicate) throws IOException {
        List<T> data = loadAll();
        data.removeIf(predicate);
        saveAll(data);
    }
}
