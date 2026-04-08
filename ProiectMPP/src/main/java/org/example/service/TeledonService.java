package org.example.service;

import org.example.domain.*;
import org.example.repository.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TeledonService {
    private VolunteerRepository volunteerRepo;
    private DonorRepository donorRepo;
    private CharityCaseRepository caseRepo;
    private DonationRepository donationRepo;
    private Volunteer loggedVolunteer;

    public TeledonService(VolunteerRepository vRepo, DonorRepository dRepo, CharityCaseRepository cRepo, DonationRepository dnRepo) {
        this.volunteerRepo = vRepo;
        this.donorRepo = dRepo;
        this.caseRepo = cRepo;
        this.donationRepo = dnRepo;
    }

    public Volunteer login(String user, String pass) throws Exception {
        String hashedPassword = hashPassword(pass);
        Volunteer v = volunteerRepo.findByUsernameAndPassword(user, hashedPassword);
        if (v == null) throw new Exception("Credentiale invalide!");
        this.loggedVolunteer = v;
        return v;
    }

    public void logout() { this.loggedVolunteer = null; }

    public List<CharityCase> getAllCases() {
        return StreamSupport.stream(caseRepo.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public List<Donor> searchDonors(String namePart) {
        return StreamSupport.stream(donorRepo.findByNameLike(namePart).spliterator(), false).collect(Collectors.toList());
    }

    public void addDonation(String name, String address, String phone, Long caseId, double amount) {
        Donor donor = donorRepo.findByName(name);
        if (donor == null) {
            donor = new Donor(name, address, phone);
            donorRepo.add(donor);
            donor = donorRepo.findByName(name);
        } else {
            if (!donor.getAddress().equals(address) || !donor.getPhoneNumber().equals(phone)) {
                donor.setAddress(address);
                donor.setPhoneNumber(phone);
                donorRepo.update(donor.getId(), donor);
            }
        }
        donationRepo.add(new Donation(donor, new CharityCase(null, 0.0) {{ setId(caseId); }}, amount));
        caseRepo.updateTotalAmount(caseId, amount);
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la hashing!", e);
        }
    }
}