package org.example.repository;

import org.example.domain.Donation;

public interface DonationRepository extends Repository<Long, Donation> {

    Iterable<Donation> findByDonor(Long donorId);
}