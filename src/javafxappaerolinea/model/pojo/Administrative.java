package javafxappaerolinea.model.pojo;

import java.util.Date;

/**
 *
 * @author Dell
 */
public class Administrative extends Employee {
    private String department;
    private int workHours;
    
    // Constructor
    public Administrative() {
        super();
        setType("Administrative");
    }
    
    public Administrative(String id, String name, String address, Date birthDate, 
    String gender, double salary, String username, String password,
    String department, int workHours) {
        super(id, name, address, birthDate, gender, salary, username, password);
        this.department = department;
        this.workHours = workHours;
        setType("Administrative");
    }
    
    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public int getWorkHours() {
        return workHours;
    }
    
    public void setWorkHours(int workHours) {
        this.workHours = workHours;
    }
}