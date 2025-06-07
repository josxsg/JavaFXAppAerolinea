/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.model.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Dell
 */
public class Pilot extends Employee {
    private int yearsExperience;
    private String email;
    private Date birthDate;
    private double flightHours;
    private String name;
    private String licenseType;
    private List<Flight> flights;
    
    // Constructor
    public Pilot() {
        super();
        this.flights = new ArrayList<>();
    }
    
    public Pilot(int id, String name, String address, Date birthDate, 
                 String gender, double salary, String username, String password,
                 int yearsExperience, String email, double flightHours, String licenseType) {
        super(id, name, address, birthDate, gender, salary, username, password);
        this.yearsExperience = yearsExperience;
        this.email = email;
        this.birthDate = birthDate;
        this.flightHours = flightHours;
        this.name = name;
        this.licenseType = licenseType;
        this.flights = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getYearsExperience() {
        return yearsExperience;
    }
    
    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = yearsExperience;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public Date getBirthDate() {
        return birthDate;
    }
    
    @Override
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    
    public double getFlightHours() {
        return flightHours;
    }
    
    public void setFlightHours(double flightHours) {
        this.flightHours = flightHours;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLicenseType() {
        return licenseType;
    }
    
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }
    
    public List<Flight> getFlights() {
        return flights;
    }
    
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
    
    public void addFlight(Flight flight) {
        this.flights.add(flight);
    }
}
