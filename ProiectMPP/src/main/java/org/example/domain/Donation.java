package org.example.domain;

import java.io.Serializable;

public class Donation extends Entity<Long> {
    private Donor donor;
    private CharityCase charityCase;
    private double amount;

    public Donation() {}

    public Donation(Donor donor, CharityCase charityCase, double amount) {
        this.donor = donor;
        this.charityCase = charityCase;
        this.amount = amount;
    }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    public CharityCase getCharityCase() { return charityCase; }
    public void setCharityCase(CharityCase charityCase) { this.charityCase = charityCase; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}