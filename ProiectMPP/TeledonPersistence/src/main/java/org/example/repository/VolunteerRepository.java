package org.example.repository;

import org.example.domain.Volunteer;

public interface VolunteerRepository extends Repository<Long, Volunteer> {

    Volunteer findByUsernameAndPassword(String username, String password);
}