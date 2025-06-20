package javafxappaerolinea.model.pojo;

public class Airplane {
    private int capacity;
    private int age;
    private boolean status;
    private String registration;
    private String model;
    private double weight;
    private Airline airline;
    
    public Airplane() {
    }
    
    public Airplane(int capacity, int age, boolean status, String registration, 
                    String model, double weight, Airline airline) {
        this.capacity = capacity;
        this.age = age;
        this.status = status;
        this.registration = registration;
        this.model = model;
        this.weight = weight;
        this.airline = airline;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public boolean isStatus() {
        return status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public String getRegistration() {
        return registration;
    }
    
    public void setRegistration(String registration) {
        this.registration = registration;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public Airline getAirline() {
        return airline;
    }
    
    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    @Override
    public String toString() {
        return registration + "-" + model;
    }
    
    
}
