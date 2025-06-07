/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.service;

import javafxappaerolinea.model.dao.*;
import javafxappaerolinea.model.pojo.*;
import javafxappaerolinea.utility.ExportUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author Dell
 */
public class ReportService {
    private final FlightDAO flightDAO;
    private final TicketDAO ticketDAO;
    private final AirlineDAO airlineDAO;
    
    public ReportService() {
        this.flightDAO = new FlightDAO();
        this.ticketDAO = new TicketDAO();
        this.airlineDAO = new AirlineDAO();
    }
    
    public void generateFlightReport(String filePath, String format) throws IOException {
        List<Flight> flights = flightDAO.findAll();
        
        switch (format.toLowerCase()) {
            case "pdf":
                ExportUtil.exportToPDF(flights, filePath, "Reporte de Vuelos");
                break;
            case "xlsx":
                ExportUtil.exportToXLSX(flights, filePath, "Vuelos");
                break;
            case "xls":
                ExportUtil.exportToXLS(flights, filePath, "Vuelos");
                break;
            case "csv":
                ExportUtil.exportToCSV(flights, filePath);
                break;
            case "json":
                ExportUtil.exportToJSON(flights, filePath);
                break;
            default:
                throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }
    
    public void generateTicketReport(String filePath, String format) throws IOException {
        List<Ticket> tickets = ticketDAO.findAll();
        
        switch (format.toLowerCase()) {
            case "pdf":
                ExportUtil.exportToPDF(tickets, filePath, "Reporte de Boletos");
                break;
            case "xlsx":
                ExportUtil.exportToXLSX(tickets, filePath, "Boletos");
                break;
            case "xls":
                ExportUtil.exportToXLS(tickets, filePath, "Boletos");
                break;
            case "csv":
                ExportUtil.exportToCSV(tickets, filePath);
                break;
            case "json":
                ExportUtil.exportToJSON(tickets, filePath);
                break;
            default:
                throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }
    
    public void generateAirlineReport(String filePath, String format) throws IOException {
        List<Airline> airlines = airlineDAO.findAll();
        
        switch (format.toLowerCase()) {
            case "pdf":
                ExportUtil.exportToPDF(airlines, filePath, "Reporte de Aerolíneas");
                break;
            case "xlsx":
                ExportUtil.exportToXLSX(airlines, filePath, "Aerolíneas");
                break;
            case "xls":
                ExportUtil.exportToXLS(airlines, filePath, "Aerolíneas");
                break;
            case "csv":
                ExportUtil.exportToCSV(airlines, filePath);
                break;
            case "json":
                ExportUtil.exportToJSON(airlines, filePath);
                break;
            default:
                throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }
    
    public List<Flight> getFlightsByDateRange(Date startDate, Date endDate) throws IOException {
        List<Flight> flights = flightDAO.findAll();
        return flights.stream()
                .filter(f -> !f.getDepartureDate().before(startDate) && !f.getDepartureDate().after(endDate))
                .collect(Collectors.toList());
    }
    
    public List<Ticket> getTicketsByDateRange(Date startDate, Date endDate) throws IOException {
        List<Ticket> tickets = ticketDAO.findAll();
        return tickets.stream()
                .filter(t -> !t.getPurchaseDate().before(startDate) && !t.getPurchaseDate().after(endDate))
                .collect(Collectors.toList());
    }
}