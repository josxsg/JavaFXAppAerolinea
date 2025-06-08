package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.utility.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class FlightDAO {
    private final JsonUtil<Flight> persistence;
    private static final String FILE_PATH = "data/vuelos.json";

    public FlightDAO() {
        this.persistence = new JsonUtil<>(FILE_PATH, Flight.class);
    }

    public List<Flight> findAll() throws IOException {
        return persistence.loadAll();
    }

    public Flight findById(String id) throws IOException, ResourceNotFoundException {
        List<Flight> flights = persistence.loadAll();
        return flights.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo con ID " + id + " no encontrado"));
    }

    public List<Flight> findByAirline(int airlineId) throws IOException {
        List<Flight> flights = persistence.loadAll();
        return flights.stream()
                .filter(f -> f.getAirline() != null && f.getAirline().getIdentificationNumber()== airlineId)
                .collect(Collectors.toList());
    }

    public List<Flight> findByAirplane(String registration) throws IOException {
        List<Flight> flights = persistence.loadAll();
        return flights.stream()
                .filter(f -> f.getAirplane()!= null && f.getAirplane().getRegistration().equals(registration))
                .collect(Collectors.toList());
    }

    public void save(Flight flight) throws IOException, DuplicateResourceException {
        List<Flight> flights = persistence.loadAll();
        if (flights.stream().anyMatch(f -> f.getId().equals(flight.getId()))) {
            throw new DuplicateResourceException("Ya existe un vuelo con ID " + flight.getId());
        }
        persistence.save(flight);
    }

    public void update(Flight flight) throws IOException, ResourceNotFoundException {
        List<Flight> flights = persistence.loadAll();
        if (flights.stream().noneMatch(f -> f.getId().equals(flight.getId()))) {
            throw new ResourceNotFoundException("Vuelo con ID " + flight.getId() + " no encontrado");
        }
        persistence.update(flight, f -> f.getId().equals(flight.getId()));
    }

    public void delete(String id) throws IOException, ResourceNotFoundException {
        List<Flight> flights = persistence.loadAll();
        if (flights.stream().noneMatch(f -> f.getId().equals(id))) {
            throw new ResourceNotFoundException("Vuelo con ID " + id + " no encontrado");
        }
        persistence.delete(f -> f.getId().equals(id));
    }
}
