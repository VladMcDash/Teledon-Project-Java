package org.example.repository;

import org.example.domain.CharityCase;

public interface CharityCaseRepository extends Repository<Long, CharityCase> {

    void updateTotalAmount(Long caseId, double newAmount);

}