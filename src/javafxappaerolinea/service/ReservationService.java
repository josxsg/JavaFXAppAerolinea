package javafxappaerolinea.service;

import javafxappaerolinea.exception.CapacityExceededException;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.CustomerDAO;
import javafxappaerolinea.model.dao.FlightDAO;
import javafxappaerolinea.model.dao.TicketDAO;
import javafxappaerolinea.model.pojo.Customer;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Ticket;
import javafxappaerolinea.utility.IdGeneratorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationService {
    private final FlightDAO flightDAO;
    private final CustomerDAO customerDAO;
    private final TicketDAO ticketDAO;
    
    public ReservationService() {
        this.flightDAO = new FlightDAO();
        this.customerDAO = new CustomerDAO();
        this.ticketDAO = new TicketDAO();
    }
    
    public Ticket createReservation(String flightId, String customerEmail, String seatNumber) 
            throws ResourceNotFoundException, CapacityExceededException, DuplicateResourceException, IOException {
        
        Flight flight = flightDAO.findById(flightId);
        Customer customer = customerDAO.findByEmail(customerEmail);
        
        if (flight.getPassengerCount() >= flight.getAirplane().getCapacity()) {
            throw new CapacityExceededException("El vuelo ha alcanzado su capacidad máxima");
        }
        
        List<Ticket> flightTickets = ticketDAO.findByFlight(flightId);
        if (flightTickets.stream().anyMatch(t -> t.getSeatNumber().equals(seatNumber))) {
            throw new DuplicateResourceException("El asiento " + seatNumber + " ya está ocupado");
        }
        
        String ticketId = IdGeneratorUtil.generateBoletoId(flightId, seatNumber);
        Ticket ticket = new Ticket(ticketId, new Date(), seatNumber, flight, customer);
        
        ticketDAO.save(ticket);
        
        flight.setPassengerCount(flight.getPassengerCount() + 1);
        flight.addTicket(ticket);
        flightDAO.update(flight);
        
        customer.addTicket(ticket);
        customerDAO.update(customer);
        
        return ticket;
    }
    
    public void cancelReservation(String ticketId) 
            throws ResourceNotFoundException, IOException {
        
        Ticket ticket = ticketDAO.findById(ticketId);
        Flight flight = ticket.getFlight();
        Customer customer = ticket.getCustomer();
        
        flight.setPassengerCount(flight.getPassengerCount() - 1);
        flight.getTickets().removeIf(t -> t.getId().equals(ticketId));
        flightDAO.update(flight);
        
        customer.getTickets().removeIf(t -> t.getId().equals(ticketId));
        customerDAO.update(customer);
        
        ticketDAO.delete(ticketId);
    }
    
    public List<String> getAvailableSeats(String flightId) throws ResourceNotFoundException, IOException {
        Flight flight = flightDAO.findById(flightId);
        List<Ticket> tickets = ticketDAO.findByFlight(flightId);
        
        int capacity = flight.getAirplane().getCapacity();
        List<String> occupiedSeats = tickets.stream()
                .map(Ticket::getSeatNumber)
                .collect(Collectors.toList());
        
        List<String> availableSeats = new ArrayList<>();
        for (int i = 1; i <= capacity; i++) {
            String seatNumber = String.format("%02d", i);
            if (!occupiedSeats.contains(seatNumber)) {
                availableSeats.add(seatNumber);
            }
        }
        
        return availableSeats;
    }
}
