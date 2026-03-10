package org.example.domain;
import java.io.Serializable;

public class CharityCase implements Serializable {
    private Long id;
    private String name;
    private double totalAmount;

    public CharityCase() {}

    public CharityCase(String name, double totalAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
    }

    // Getters și Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}