/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Ticket;
import javafxappaerolinea.utility.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Dell
 */
public class TicketDAO {
    private final JsonUtil<Ticket> persistence;
    private static final String FILE_PATH = "data/boletos.json";

    public TicketDAO() {
        this.persistence = new JsonUtil<>(FILE_PATH, Ticket.class);
    }

    public List<Ticket> findAll() throws IOException {
        return persistence.loadAll();
    }

    public Ticket findById(String id) throws IOException, ResourceNotFoundException {
        List<Ticket> tickets = persistence.loadAll();
        return tickets.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Boleto con ID " + id + " no encontrado"));
    }

    public List<Ticket> findByCustomer(String customerEmail) throws IOException {
        List<Ticket> tickets = persistence.loadAll();
        return tickets.stream()
                .filter(t -> t.getCustomer() != null && t.getCustomer().getEmail().equals(customerEmail))
                .collect(Collectors.toList());
    }

    public List<Ticket> findByFlight(String flightId) throws IOException {
        List<Ticket> tickets = persistence.loadAll();
        return tickets.stream()
                .filter(t -> t.getFlight() != null && t.getFlight().getId().equals(flightId))
                .collect(Collectors.toList());
    }

    public void save(Ticket ticket) throws IOException, DuplicateResourceException {
        List<Ticket> tickets = persistence.loadAll();
        if (tickets.stream().anyMatch(t -> t.getId().equals(ticket.getId()))) {
            throw new DuplicateResourceException("Ya existe un boleto con ID " + ticket.getId());
        }
        persistence.save(ticket);
    }

    public void update(Ticket ticket) throws IOException, ResourceNotFoundException {
        List<Ticket> tickets = persistence.loadAll();
        if (tickets.stream().noneMatch(t -> t.getId().equals(ticket.getId()))) {
            throw new ResourceNotFoundException("Boleto con ID " + ticket.getId() + " no encontrado");
        }
        persistence.update(ticket, t -> t.getId().equals(ticket.getId()));
    }

    public void delete(String id) throws IOException, ResourceNotFoundException {
        List<Ticket> tickets = persistence.loadAll();
        if (tickets.stream().noneMatch(t -> t.getId().equals(id))) {
            throw new ResourceNotFoundException("Boleto con ID " + id + " no encontrado");
        }
        persistence.delete(t -> t.getId().equals(id));
    }
}
