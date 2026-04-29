package org.example.services;

import org.example.domain.CharityCase;
import org.example.domain.Donor;
import org.example.domain.Volunteer;
import java.util.List;

public interface ITeledonServices {
    Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException;

    void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException;

    List<CharityCase> getAllCases() throws TeledonException;

    List<Donor> searchDonors(String namePart) throws TeledonException;

    void addDonation(String name, String address, String phone, Long caseId, double amount) throws TeledonException;

    void updateDonor(Long id, String nume, String adresa, String telefon) throws TeledonException;

}