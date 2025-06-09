package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.utility.JsonUtil;
import java.io.IOException;
import java.util.List;

public class AirlineDAO {
    private final JsonUtil<Airline> persistence;
    private static final String FILE_PATH = "data/aerolineas.json";

    public AirlineDAO() {
        this.persistence = new JsonUtil<>(FILE_PATH, Airline.class);
    }
        public AirlineDAO(String filePath) {
        this.persistence = new JsonUtil<>(filePath, Airline.class);
    }

    public List<Airline> findAll() throws IOException {
        return persistence.loadAll();
    }

    public Airline findById(int id) throws IOException, ResourceNotFoundException {
        List<Airline> airlines = persistence.loadAll();
        return airlines.stream()
                .filter(a -> a.getIdentificationNumber()== id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aerolínea con ID " + id + " no encontrada"));
    }

    public void save(Airline airline) throws IOException, DuplicateResourceException {
        List<Airline> airlines = persistence.loadAll();
        if (airlines.stream().anyMatch(a -> a.getIdentificationNumber() == airline.getIdentificationNumber())) {
            throw new DuplicateResourceException("Ya existe una aerolínea con ID " + airline.getIdentificationNumber());
        }
        persistence.save(airline);
    }

    public void update(Airline airline) throws IOException, ResourceNotFoundException {
        List<Airline> airlines = persistence.loadAll();
        if (airlines.stream().noneMatch(a -> a.getIdentificationNumber() == airline.getIdentificationNumber())) {
            throw new ResourceNotFoundException("Aerolínea con ID " + airline.getIdentificationNumber()+ " no encontrada");
        }
        persistence.update(airline, a -> a.getIdentificationNumber() == airline.getIdentificationNumber());
    }

    public void delete(int id) throws IOException, ResourceNotFoundException {
        List<Airline> airlines = persistence.loadAll();
        if (airlines.stream().noneMatch(a -> a.getIdentificationNumber() == id)) {
            throw new ResourceNotFoundException("Aerolínea con ID " + id + " no encontrada");
        }
        persistence.delete(a -> a.getIdentificationNumber() == id);
    }
}
