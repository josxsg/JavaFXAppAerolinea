/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.utility.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Dell
 */
public class AirplaneDAO {
    private final JsonUtil<Airplane> persistence;
    private static final String FILE_PATH = "data/aviones.json";

    public AirplaneDAO() {
        this.persistence = new JsonUtil<>(FILE_PATH, Airplane.class);
    }

    public List<Airplane> findAll() throws IOException {
        return persistence.loadAll();
    }

    public Airplane findByRegistration(String registration) throws IOException, ResourceNotFoundException {
        List<Airplane> airplanes = persistence.loadAll();
        return airplanes.stream()
                .filter(a -> a.getRegistration().equals(registration))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Avión con matrícula " + registration + " no encontrado"));
    }

    public List<Airplane> findByAirline(int airlineId) throws IOException {
        List<Airplane> airplanes = persistence.loadAll();
        return airplanes.stream()
                .filter(a -> a.getAirline() != null && a.getAirline().getIdentificationNumber()== airlineId)
                .collect(Collectors.toList());
    }

    public void save(Airplane airplane) throws IOException, DuplicateResourceException {
        List<Airplane> airplanes = persistence.loadAll();
        if (airplanes.stream().anyMatch(a -> a.getRegistration().equals(airplane.getRegistration()))) {
            throw new DuplicateResourceException("Ya existe un avión con matrícula " + airplane.getRegistration());
        }
        persistence.save(airplane);
    }

    public void update(Airplane airplane) throws IOException, ResourceNotFoundException {
        List<Airplane> airplanes = persistence.loadAll();
        if (airplanes.stream().noneMatch(a -> a.getRegistration().equals(airplane.getRegistration()))) {
            throw new ResourceNotFoundException("Avión con matrícula " + airplane.getRegistration()+ " no encontrado");
        }
        persistence.update(airplane, a -> a.getRegistration().equals(airplane.getRegistration()));
    }

    public void delete(String registration) throws IOException, ResourceNotFoundException {
        List<Airplane> airplanes = persistence.loadAll();
        if (airplanes.stream().noneMatch(a -> a.getRegistration().equals(registration))) {
            throw new ResourceNotFoundException("Avión con matrícula " + registration + " no encontrado");
        }
        persistence.delete(a -> a.getRegistration().equals(registration));
    }
}
