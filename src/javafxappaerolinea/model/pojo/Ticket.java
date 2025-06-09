package javafxappaerolinea.model.pojo;

import java.util.Date;

public class Ticket {
    private String id;
    private Date purchaseDate;
    private String seatNumber;
    private Flight flight;
    private Customer customer;
    
    public Ticket() {
    }
    
    public Ticket(String id, Date purchaseDate, String seatNumber, Flight flight, Customer customer) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.seatNumber = seatNumber;
        this.flight = flight;
        this.customer = customer;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {    
        this.id = id;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public Flight getFlight() {
        return flight;
    }
    
    public void setFlight(Flight flight) {
        this.flight = flight;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
