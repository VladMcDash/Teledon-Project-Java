package org.example.domain;

import java.io.Serializable;

public class Donation implements Serializable {
    private Long id;
    private Donor donor;
    private CharityCase charityCase;
    private double amount;

    public Donation() {}

    public Donation(Donor donor, CharityCase charityCase, double amount) {
        this.donor = donor;
        this.charityCase = charityCase;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    public CharityCase getCharityCase() { return charityCase; }
    public void setCharityCase(CharityCase charityCase) { this.charityCase = charityCase; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}