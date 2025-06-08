package javafxappaerolinea.model.pojo;

public class Seat {
    private String seatNumber;
    private boolean isOccupied;
    private String seatClass; // ECONOMY, BUSINESS, FIRST
    private int row;
    private String column;
    
    public Seat(String seatNumber, boolean isOccupied, String seatClass) {
        this.seatNumber = seatNumber;
        this.isOccupied = isOccupied;
        this.seatClass = seatClass;
        
        // Extraer fila y columna del número de asiento (ej: "12A")
        if (seatNumber != null && seatNumber.length() >= 2) {
            this.row = Integer.parseInt(seatNumber.substring(0, seatNumber.length() - 1));
            this.column = seatNumber.substring(seatNumber.length() - 1);
        }
    }
    
    // Getters y Setters
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
    
    public String getSeatClass() {
        return seatClass;
    }
    
    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }
    
    public int getRow() {
        return row;
    }
    
    public String getColumn() {
        return column;
    }
}