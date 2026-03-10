package org.example.repository;

import org.example.domain.Donor;

public interface DonorRepository extends Repository<Long, Donor> {

    Iterable<Donor> findByNameLike(String namePart);
    Donor findByName(String name);
}