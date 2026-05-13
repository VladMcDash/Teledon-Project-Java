package org.example.services;

import org.example.domain.CharityCase;
import org.example.domain.Donor;

public interface ITeledonObserver {
    void donationAdded(CharityCase updatedCase) throws TeledonException;
    void donorUpdated(Donor updatedDonor) throws TeledonException;
}