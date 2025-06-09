package javafxappaerolinea.model.pojo;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class Employee {
    private String id;
    private String name;
    private String address;
    private Date birthDate;
    private String gender;
    private double salary;
    private String username;
    private String password;
    private String type;
    
    public Employee() {
    }
    
    public Employee(String id, String name, String address, Date birthDate, 
                   String gender, double salary, String username, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.birthDate = birthDate;
        this.gender = gender;
        this.salary = salary;
        this.username = username;
        this.password = password;
    }
    
    public int getAge() {
        if (this.birthDate == null) {
            return 0; 
        }
        LocalDate dob = this.birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now(ZoneId.of("America/Mexico_City")); 
        return Period.between(dob, now).getYears();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Date getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
