/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.service;

import javafxappaerolinea.model.pojo.Employee;
/**
 *
 * @author Dell
 */
public class SessionManager {
    private static SessionManager instance;
    private Employee currentUser;
    
    private SessionManager() {
        // Private constructor for singleton
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setCurrentUser(Employee user) {
        this.currentUser = user;
    }
    
    public Employee getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public boolean isAdministrative() {
        return currentUser instanceof javafxappaerolinea.model.pojo.Administrative;
    }
    
    public boolean isPilot() {
        return currentUser instanceof javafxappaerolinea.model.pojo.Pilot;
    }
    
    public boolean isAssistant() {
        return currentUser instanceof javafxappaerolinea.model.pojo.Assistant;
    }
}