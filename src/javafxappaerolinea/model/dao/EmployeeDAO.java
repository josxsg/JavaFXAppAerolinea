package javafxappaerolinea.model.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Employee;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.utility.JsonUtil;


public class EmployeeDAO {
    private final JsonUtil<Employee> persistence;
    private static final String FILE_PATH = "data/empleados.json";

    public EmployeeDAO() {
        // Solo necesitamos la persistencia genérica para escribir datos.
        // La lectura la haremos de forma personalizada.
        this.persistence = new JsonUtil<>(FILE_PATH, Employee.class);
    }

    /**
     * Método central que lee el JSON y crea una lista de empleados
     * con sus tipos de objeto correctos (Administrative, Pilot, etc.).
     */
    public List<Employee> findAll() throws IOException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<Employee> employees = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return employees; // Retorna lista vacía si no hay archivo.
        }

        try (Reader reader = new FileReader(FILE_PATH)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : null;

                Employee employee = null;
                if (type != null) {
                    switch (type) {
                        case "Administrative":
                            employee = gson.fromJson(jsonObject, Administrative.class);
                            break;
                        case "Pilot":
                            employee = gson.fromJson(jsonObject, Pilot.class);
                            break;
                        case "Assistant":
                            employee = gson.fromJson(jsonObject, Assistant.class);
                            break;
                        default:
                            employee = gson.fromJson(jsonObject, Employee.class);
                            break;
                    }
                }
                if (employee != null) {
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            throw new IOException("Error al cargar y procesar el JSON de empleados: " + e.getMessage(), e);
        }

        return employees;
    }

    public Employee findById(String id) throws IOException, ResourceNotFoundException {
        List<Employee> employees = findAll(); // Usamos el nuevo findAll()
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con ID " + id + " no encontrado"));
    }

    public Employee findByUsername(String username) throws IOException, ResourceNotFoundException {
        List<Employee> employees = findAll(); // Usamos el nuevo findAll()
        return employees.stream()
                .filter(e -> e.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con usuario " + username + " no encontrado"));
    }

    /**
     * Filtra y devuelve solo los empleados administrativos de la lista completa.
     */
    public List<Administrative> findAllAdministratives() throws IOException {
        List<Employee> employees = findAll(); // Obtenemos la lista completa y correctamente tipada
        return employees.stream()
                .filter(e -> e instanceof Administrative) // Filtramos por el tipo de objeto
                .map(e -> (Administrative) e) // Hacemos la conversión, que ahora es segura
                .collect(Collectors.toList());
    }

    /**
     * Filtra y devuelve solo los pilotos de la lista completa.
     */
    public List<Pilot> findAllPilots() throws IOException {
        List<Employee> employees = findAll(); // Obtenemos la lista completa y correctamente tipada
        return employees.stream()
                .filter(e -> e instanceof Pilot) // Filtramos por el tipo de objeto
                .map(e -> (Pilot) e) // Hacemos la conversión, que ahora es segura
                .collect(Collectors.toList());
    }

    /**
     * Filtra y devuelve solo los asistentes de vuelo de la lista completa.
     */
    public List<Assistant> findAllAssistants() throws IOException {
        List<Employee> employees = findAll(); // Obtenemos la lista completa y correctamente tipada
        return employees.stream()
                .filter(e -> e instanceof Assistant) // Filtramos por el tipo de objeto
                .map(e -> (Assistant) e) // Hacemos la conversión, que ahora es segura
                .collect(Collectors.toList());
    }

    public void save(Employee employee) throws IOException, DuplicateResourceException {
        List<Employee> employees = findAll();

        if (employees.stream().anyMatch(e -> e.getId().equals(employee.getId()))) {
            throw new DuplicateResourceException("Ya existe un empleado con ID " + employee.getId());
        }
        if (employees.stream().anyMatch(e -> e.getUsername().equals(employee.getUsername()))) {
            throw new DuplicateResourceException("Ya existe un empleado con usuario " + employee.getUsername());
        }

        employees.add(employee);
        saveAllWithTypes(employees);
    }

    public void update(Employee employee) throws IOException, ResourceNotFoundException {
        // Cargar todos los empleados con sus tipos correctos
        List<Employee> employees = findAll();

        // Verificar que el empleado existe
        boolean found = false;
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equals(employee.getId())) {
                // Reemplazar solo el empleado que se está actualizando
                employees.set(i, employee);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Empleado con ID " + employee.getId() + " no encontrado");
        }

        // Guardar toda la lista preservando los tipos
        saveAllWithTypes(employees);
    }

    public void delete(String id) throws IOException, ResourceNotFoundException {
        List<Employee> employees = findAll();

        if (employees.stream().noneMatch(e -> e.getId().equals(id))) {
            throw new ResourceNotFoundException("Empleado con ID " + id + " no encontrado");
        }

        employees.removeIf(e -> e.getId().equals(id));
        saveAllWithTypes(employees);
    }
    
    private void saveAllWithTypes(List<Employee> employees) throws IOException {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd")
            .create();

        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(employees, writer);
        } catch (Exception e) {
            throw new IOException("Error al guardar datos en JSON: " + e.getMessage(), e);
        }
    }
}