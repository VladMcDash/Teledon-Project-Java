package org.example.domain;
import java.io.Serializable;

public class CharityCase extends Entity<Long> {
    private String name;
    private double totalAmount;

    public CharityCase() {}

    public CharityCase(String name, double totalAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}