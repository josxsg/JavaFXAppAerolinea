/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.service;

import java.io.IOException;
import javafxappaerolinea.exception.AuthenticationException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Employee;
import javafxappaerolinea.utility.PasswordUtil;

/**
 *
 * @author Dell
 */
public class AuthenticationService {
    private final EmployeeDAO employeeDAO;
    
    public AuthenticationService() {
        this.employeeDAO = new EmployeeDAO();
    }
    
    public Employee login(String username, String password) throws AuthenticationException {
        try {
            Employee employee = employeeDAO.findByUsername(username);
            if (PasswordUtil.verifyPassword(password, employee.getPassword())) {
                return employee;
            } else {
                throw new AuthenticationException("Contraseña incorrecta");
            }
        } catch (ResourceNotFoundException e) {
            throw new AuthenticationException("Usuario no encontrado");
        } catch (IOException e) {
            throw new AuthenticationException("Error al verificar credenciales: " + e.getMessage());
        }
    }
    
    public void changePassword(Employee employee, String currentPassword, String newPassword) 
            throws AuthenticationException {
        if (!PasswordUtil.verifyPassword(currentPassword, employee.getPassword())) {
            throw new AuthenticationException("Contraseña actual incorrecta");
        }
        
        employee.setPassword(PasswordUtil.hashPassword(newPassword));
        try {
            employeeDAO.update(employee);
        } catch (Exception e) {
            throw new AuthenticationException("Error al cambiar contraseña: " + e.getMessage());
        }
    }
}
