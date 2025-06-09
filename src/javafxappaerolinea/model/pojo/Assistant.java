package javafxappaerolinea.model.pojo;

import java.util.Date;

public class Assistant extends Employee {
    private String email;
    private int assistanceHours;
    private int numberOfLanguages;
    private Airline airline;
    
    public Assistant() {
        super();
    }
    
    public Assistant(String id, String name, String address, Date birthDate, 
                     String gender, double salary, String username, String password,
                     String email, int assistanceHours, int numberOfLanguages, Airline airline) {
        super(id, name, address, birthDate, gender, salary, username, password);
        this.email = email;
        this.assistanceHours = assistanceHours;
        this.numberOfLanguages = numberOfLanguages;
        this.airline = airline;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getAssistanceHours() {
        return assistanceHours;
    }
    
    public void setAssistanceHours(int assistanceHours) {
        this.assistanceHours = assistanceHours;
    }
    
    public int getNumberOfLanguages() {
        return numberOfLanguages;
    }
    
    public void setNumberOfLanguages(int numberOfLanguages) {
        this.numberOfLanguages = numberOfLanguages;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }
    
}
