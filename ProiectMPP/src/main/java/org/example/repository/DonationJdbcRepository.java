package org.example.repository;

import org.example.domain.Donation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.Properties;

public class DonationJdbcRepository implements DonationRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(DonationJdbcRepository.class);

    public DonationJdbcRepository(Properties props) { dbUtils = new JdbcUtils(props); }

    @Override
    public void add(Donation entity) {
        logger.traceEntry("Saving donation {}", entity);
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("INSERT INTO Donations (donorId, caseId, amount) VALUES (?,?,?)")) {
            preStmt.setLong(1, entity.getDonor().getId());
            preStmt.setLong(2, entity.getCharityCase().getId());
            preStmt.setDouble(3, entity.getAmount());
            preStmt.executeUpdate();
        } catch (SQLException e) { logger.error(e); }
        logger.traceExit();
    }

    @Override public void delete(Long id) {}
    @Override public void update(Long id, Donation entity) {}
    @Override public Donation findOne(Long id) { return null; }
    @Override public Iterable<Donation> findAll() { return null; }
    @Override public Iterable<Donation> findByDonor(Long donorId) { return null; }
}