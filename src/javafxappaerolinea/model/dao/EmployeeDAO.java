package javafxappaerolinea.model.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
    private String filePath;

    public EmployeeDAO() {

        this.persistence = new JsonUtil<>(FILE_PATH, Employee.class);
    }
    
        public EmployeeDAO(String testFilePath) {
        this.filePath = testFilePath;
        this.persistence = new JsonUtil<>(this.filePath, Employee.class);
    }



    public List<Employee> findAll() throws IOException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<Employee> employees = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return employees; 
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
        } catch (JsonIOException | JsonSyntaxException | IllegalStateException e) {
            throw new IOException("Error al procesar el archivo JSON de empleados: " + e.getMessage(), e);
        }


        return employees;
    }

    public Employee findById(String id) throws IOException, ResourceNotFoundException {
        List<Employee> employees = findAll(); 
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con ID " + id + " no encontrado"));
    }

    public Employee findByUsername(String username) throws IOException, ResourceNotFoundException {
        List<Employee> employees = findAll();
        return employees.stream()
                .filter(e -> e.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con usuario " + username + " no encontrado"));
    }

  
    public List<Administrative> findAllAdministratives() throws IOException {
        List<Employee> employees = findAll(); 
        return employees.stream()
                .filter(e -> e instanceof Administrative)
                .map(e -> (Administrative) e) 
                .collect(Collectors.toList());
    }

 
    public List<Pilot> findAllPilots() throws IOException {
        List<Employee> employees = findAll();
        return employees.stream()
                .filter(e -> e instanceof Pilot) 
                .map(e -> (Pilot) e) 
                .collect(Collectors.toList());
    }

  
    public List<Assistant> findAllAssistants() throws IOException {
        List<Employee> employees = findAll(); 
        return employees.stream()
                .filter(e -> e instanceof Assistant) 
                .map(e -> (Assistant) e) 
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
        List<Employee> employees = findAll();

        boolean found = false;
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equals(employee.getId())) {
                employees.set(i, employee);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Empleado con ID " + employee.getId() + " no encontrado");
        }

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
        } catch (JsonIOException e) {
            throw new IOException("Error al serializar los datos a JSON: " + e.getMessage(), e);
        }
    }
}