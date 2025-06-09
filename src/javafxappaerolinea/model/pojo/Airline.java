package javafxappaerolinea.model.pojo;

public class Airline {
    private int identificationNumber;
    private String address;
    private String name;
    private String contactPerson;
    private String phoneNumber;
    
    public Airline() {
    }
    

    public Airline(int identificationNumber, String address, String name, 
                   String contactPerson, String phoneNumber) {
        this.identificationNumber = identificationNumber;
        this.address = address;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
    }
    
    public int getIdentificationNumber() {
        return identificationNumber;
    }
    
    public void setIdentificationNumber(int identificationNumber) {
        this.identificationNumber = identificationNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public String toString() {
        return identificationNumber + " - " + name;
    }
}