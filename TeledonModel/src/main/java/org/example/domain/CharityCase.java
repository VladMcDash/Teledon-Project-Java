package org.example.domain;

import jakarta.persistence.*;

@jakarta.persistence.Entity
@Table(name = "charitycases")
public class CharityCase extends Entity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "totalAmount")
    private double totalAmount;

    public CharityCase() {
    }

    public CharityCase(String name, double totalAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}