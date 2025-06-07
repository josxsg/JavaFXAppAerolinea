package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Employee;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.utility.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Dell
 */
public class EmployeeDAO {
    private final JsonUtil<Employee> persistence;
    private static final String FILE_PATH = "data/empleados.json";

    public EmployeeDAO() {
        this.persistence = new JsonUtil<>(FILE_PATH, Employee.class);
    }

    public List<Employee> findAll() throws IOException {
        return persistence.loadAll();
    }

    public Employee findById(String id) throws IOException, ResourceNotFoundException {
        List<Employee> employees = persistence.loadAll();
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con ID " + id + " no encontrado"));
    }

    public Employee findByUsername(String username) throws IOException, ResourceNotFoundException {
        List<Employee> employees = persistence.loadAll();
        return employees.stream()
                .filter(e -> e.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con usuario " + username + " no encontrado"));
    }

    public List<Administrative> findAllAdministratives() throws IOException {
        List<Employee> employees = persistence.loadAll();
        return employees.stream()
                .filter(e -> "Administrative".equals(e.getType()))
                .map(e -> (Administrative) e)
                .collect(Collectors.toList());
    }

    public List<Assistant> findAllAssistants() throws IOException {
        List<Employee> employees = persistence.loadAll();
        return employees.stream()
                .filter(e -> "Assistant".equals(e.getType()))
                .map(e -> (Assistant) e)
                .collect(Collectors.toList());
    }

    public List<Pilot> findAllPilots() throws IOException {
        List<Employee> employees = persistence.loadAll();
        return employees.stream()
                .filter(e -> "Pilot".equals(e.getType()))
                .map(e -> (Pilot) e)
                .collect(Collectors.toList());
    }

    public void save(Employee employee) throws IOException, DuplicateResourceException {
        List<Employee> employees = persistence.loadAll();
        if (employees.stream().anyMatch(e -> e.getId().equals(employee.getId()))) {
            throw new DuplicateResourceException("Ya existe un empleado con ID " + employee.getId());
        }
        if (employees.stream().anyMatch(e -> e.getUsername().equals(employee.getUsername()))) {
            throw new DuplicateResourceException("Ya existe un empleado con usuario " + employee.getUsername());
        }
        persistence.save(employee);
    }

    public void update(Employee employee) throws IOException, ResourceNotFoundException {
        List<Employee> employees = persistence.loadAll();
        if (employees.stream().noneMatch(e -> e.getId().equals(employee.getId()))) {
            throw new ResourceNotFoundException("Empleado con ID " + employee.getId() + " no encontrado");
        }
        persistence.update(employee, e -> e.getId().equals(employee.getId()));
    }

    public void delete(String id) throws IOException, ResourceNotFoundException {
        List<Employee> employees = persistence.loadAll();
        if (employees.stream().noneMatch(e -> e.getId().equals(id))) {
            throw new ResourceNotFoundException("Empleado con ID " + id + " no encontrado");
        }
        persistence.delete(e -> e.getId().equals(id));
    }
}