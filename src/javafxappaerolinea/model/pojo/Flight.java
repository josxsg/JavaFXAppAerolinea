package javafxappaerolinea.model.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Flight {
    private String id;
    private String destinationCity;
    private String originCity;
    private double ticketCost;
    private Date arrivalDate;
    private Date departureDate;
    private int arrivalHour;
    private String departureHour;
    private int passengerCount;
    private String gate;
    private double travelTime;
    private Airplane airplane;
    private List<Pilot> pilots;
    private List<Assistant> assistants;
    private List<Ticket> tickets;
    private Airline airline;

    public Flight() {
        this.pilots = new ArrayList<>();
        this.assistants = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }

    public Flight(String id, String destinationCity, String originCity, double ticketCost,
                  Date arrivalDate, Date departureDate, int arrivalHour, String departureHour,
                  int passengerCount, String gate, double travelTime,
                  Airplane airplane, Airline airline) {
        this.id = id;
        this.destinationCity = destinationCity;
        this.originCity = originCity;
        this.ticketCost = ticketCost;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.arrivalHour = arrivalHour;
        this.departureHour = departureHour;
        this.passengerCount = passengerCount;
        this.gate = gate;
        this.travelTime = travelTime;
        this.airplane = airplane;
        this.airline = airline;
        this.pilots = new ArrayList<>();
        this.assistants = new ArrayList<>();
    }
    
    public int getCapacity() {
        if (airplane != null) {
            return airplane.getCapacity();
        }
        return 44; 
    }
    
    public boolean hasAvailableSeats() {
        return passengerCount < getCapacity();
    }
    
    public int getAvailableSeats() {
        return getCapacity() - passengerCount;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {    
        this.id = id;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public double getTicketCost() {
        return ticketCost;
    }

    public void setTicketCost(double ticketCost) {
        this.ticketCost = ticketCost;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public int getArrivalHour() {
        return arrivalHour;
    }

    public void setArrivalHour(int arrivalHour) {
        this.arrivalHour = arrivalHour;
    }

    public String getDepartureHour() {
        return departureHour;
    }

    public void setDepartureHour(String departureHour) {
        this.departureHour = departureHour;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(double travelTime) {
        this.travelTime = travelTime;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public List<Pilot> getPilots() {
        return pilots;
    }

    public void setPilots(List<Pilot> pilots) {
        this.pilots = pilots;
    }

    public void addPilot(Pilot pilot) {
        if (this.pilots.size() < 2) {
            this.pilots.add(pilot);
        }
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public void setAssistants(List<Assistant> assistants) {
        this.assistants = assistants;
    }

    public void addAssistant(Assistant assistant) {
        if (this.assistants.size() < 4) {
            this.assistants.add(assistant);
        }
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }
    
    @Override
    public String toString() {
        return String.format("Vuelo %s: %s -> %s", id, originCity, destinationCity);
    }
}
