package org.example.server;

import org.example.domain.*;
import org.example.repository.*;
import org.example.services.ITeledonObserver;
import org.example.services.ITeledonServices;
import org.example.services.TeledonException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TeledonServicesImpl implements ITeledonServices {
    private VolunteerRepository volunteerRepo;
    private DonorRepository donorRepo;
    private CharityCaseRepository caseRepo;
    private DonationRepository donationRepo;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private Map<Long, ITeledonObserver> loggedClients;

    public TeledonServicesImpl(VolunteerRepository vRepo, DonorRepository dRepo, CharityCaseRepository cRepo, DonationRepository dnRepo) {
        this.volunteerRepo = vRepo;
        this.donorRepo = dRepo;
        this.caseRepo = cRepo;
        this.donationRepo = dnRepo;
        this.loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Volunteer login(String user, String pass, ITeledonObserver client) throws TeledonException {
        String hashedPassword = hashPassword(pass);
        try {
            Volunteer v = volunteerRepo.findByUsernameAndPassword(user, hashedPassword);
            if (v == null) throw new TeledonException("Credentiale invalide!");
            if (loggedClients.containsKey(v.getId())) throw new TeledonException("Userul este deja logat!");

            loggedClients.put(v.getId(), client);
            return v;
        } catch (Exception e) {
            throw new TeledonException(e.getMessage());
        }
    }

    @Override
    public synchronized void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException {
        ITeledonObserver localClient = loggedClients.remove(volunteer.getId());
        if (localClient == null) throw new TeledonException("Userul nu este logat!");
    }

    @Override
    public synchronized List<CharityCase> getAllCases() {
        return StreamSupport.stream(caseRepo.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public List<Donor> searchDonors(String namePart) {
        return StreamSupport.stream(donorRepo.findByNameLike(namePart).spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public synchronized void addDonation(String name, String address, String phone, Long caseId, double amount) throws TeledonException {
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

        CharityCase updatedCase = caseRepo.findOne(caseId);
        if (updatedCase == null) {
            System.out.println("findOne a dat null, trimitem un obiect placeholder pentru refresh.");
            updatedCase = new CharityCase("Refresh Required", 0.0);
            updatedCase.setId(caseId);
        }

        System.out.println("Notificam clientii pentru update la: " + updatedCase.getName());
        notifyAllClients(updatedCase);
    }

    private void notifyAllClients(CharityCase updatedCase) {
        for (ITeledonObserver client : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    client.donationAdded(updatedCase);
                } catch (TeledonException e) {
                    System.err.println("Eroare la notificarea clientului: " + e.getMessage());
                }
            });
        }
    }

    private void notifyDonorUpdated(Donor updatedDonor) {
        for (ITeledonObserver client : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    client.donorUpdated(updatedDonor);
                } catch (TeledonException e) {
                    System.err.println("Eroare la notificare donor: " + e);
                }
            });
        }
    }

    @Override
    public synchronized void updateDonor(Long id, String nume, String adresa, String telefon) throws TeledonException {
        donorRepo.update(id, nume, adresa, telefon);

        Donor updatedDonor = new Donor(nume, adresa, telefon);
        updatedDonor.setId(id);
        notifyDonorUpdated(updatedDonor);
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