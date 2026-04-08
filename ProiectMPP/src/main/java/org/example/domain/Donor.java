package org.example.domain;
import java.io.Serializable;


public class Donor extends Entity<Long> {
    private String name;
    private String address;
    private String phoneNumber;

    public Donor() {}

    public Donor(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    @Override
    public String toString() {
        return name + " (Tel: " + phoneNumber + ")";
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}