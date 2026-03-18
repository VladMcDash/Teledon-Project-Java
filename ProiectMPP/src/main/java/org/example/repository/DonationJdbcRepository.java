package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.JdbcUtils;
import org.example.domain.Donation;

import java.sql.*;
import java.util.Properties;

public class DonationJdbcRepository implements DonationRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(DonationJdbcRepository.class);

    public DonationJdbcRepository(Properties props) {
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public void add(Donation entity) {
        logger.traceEntry("saving donation {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement(
                "insert into Donations (donorId, caseId, amount) values (?,?,?)")) {
            preStmt.setLong(1, entity.getDonor().getId());
            preStmt.setLong(2, entity.getCharityCase().getId());
            preStmt.setDouble(3, entity.getAmount());
            preStmt.executeUpdate();
            logger.trace("Donation saved successfully");
        } catch (SQLException ex) {
            logger.error(ex);
        }
        logger.traceExit();
    }

    @Override
    public Iterable<Donation> findByDonor(Long donorId) {
        logger.traceEntry("finding donations for donor id {}", donorId);
        // Implementare cu SELECT * FROM Donations WHERE donorId = ?
        return null;
    }

    @Override public void delete(Long id) {}
    @Override public void update(Long id, Donation entity) {}
    @Override public Donation findOne(Long id) { return null; }
    @Override public Iterable<Donation> findAll() { return null; }
}