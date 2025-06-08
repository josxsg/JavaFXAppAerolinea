package javafxappaerolinea.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil<T> {
    private final String filePath;
    private final Class<T> type;
    private final Type listType;
    private final Gson gson;
    

    public JsonUtil(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.type = type;
        this.listType = TypeToken.getParameterized(List.class, type).getType();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd")
                .create();
        
        File file = new File(filePath);
        File directory = file.getParentFile();
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
    }
    

    public void saveAll(List<T> data) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            throw new IOException("Error al guardar datos en JSON: " + e.getMessage(), e);
        }
    }
    
  
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
    

    public void save(T object) throws IOException {
        List<T> data = loadAll();
        data.add(object);
        saveAll(data);
    }
    

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
    
  
    public void delete(java.util.function.Predicate<T> predicate) throws IOException {
        List<T> data = loadAll();
        data.removeIf(predicate);
        saveAll(data);
    }
}
