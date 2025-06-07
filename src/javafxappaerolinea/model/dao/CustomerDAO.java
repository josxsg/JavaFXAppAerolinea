/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Customer;
import javafxappaerolinea.utility.JsonUtil;

import java.io.IOException;
import java.util.List;
/**
 *
 * @author Dell
 */
public class CustomerDAO {
    private final JsonUtil<Customer> persistence;
    private static final String FILE_PATH = "data/clientes.json";

    public CustomerDAO() {
        this.persistence = new JsonUtil<>(FILE_PATH, Customer.class);
    }

    public List<Customer> findAll() throws IOException {
        return persistence.loadAll();
    }

    public Customer findByEmail(String email) throws IOException, ResourceNotFoundException {
        List<Customer> customers = persistence.loadAll();
        return customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con correo " + email + " no encontrado"));
    }

    public void save(Customer customer) throws IOException, DuplicateResourceException {
        List<Customer> customers = persistence.loadAll();
        if (customers.stream().anyMatch(c -> c.getEmail().equals(customer.getEmail()))) {
            throw new DuplicateResourceException("Ya existe un cliente con correo " + customer.getEmail());
        }
        persistence.save(customer);
    }

    public void update(Customer customer) throws IOException, ResourceNotFoundException {
        List<Customer> customers = persistence.loadAll();
        if (customers.stream().noneMatch(c -> c.getEmail().equals(customer.getEmail()))) {
            throw new ResourceNotFoundException("Cliente con correo " + customer.getEmail() + " no encontrado");
        }
        persistence.update(customer, c -> c.getEmail().equals(customer.getEmail()));
    }

    public void delete(String email) throws IOException, ResourceNotFoundException {
        List<Customer> customers = persistence.loadAll();
        if (customers.stream().noneMatch(c -> c.getEmail().equals(email))) {
            throw new ResourceNotFoundException("Cliente con correo " + email + " no encontrado");
        }
        persistence.delete(c -> c.getEmail().equals(email));
    }
}
