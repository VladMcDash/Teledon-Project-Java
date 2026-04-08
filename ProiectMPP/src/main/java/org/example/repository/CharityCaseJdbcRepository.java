package org.example.repository;

import org.example.domain.CharityCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CharityCaseJdbcRepository implements CharityCaseRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(CharityCaseJdbcRepository.class);

    public CharityCaseJdbcRepository(Properties props) {
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Iterable<CharityCase> findAll() {
        logger.traceEntry();
        List<CharityCase> cases = new ArrayList<>();
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("SELECT * FROM CharityCases")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    CharityCase c = new CharityCase(result.getString("name"), result.getDouble("totalAmount"));
                    c.setId(result.getLong("id"));
                    cases.add(c);
                }
            }
        } catch (SQLException e) {
            logger.error("DB Error: {}", e);
        }
        return logger.traceExit(cases);
    }

    @Override
    public void updateTotalAmount(Long id, double amount) {
        logger.traceEntry("Updating amount for case {}", id);
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("UPDATE CharityCases SET totalAmount = totalAmount + ? WHERE id = ?")) {
            preStmt.setDouble(1, amount);
            preStmt.setLong(2, id);
            preStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB Error: {}", e);
        }
        logger.traceExit();
    }

    @Override public void add(CharityCase entity) {}
    @Override public void delete(Long id) {}
    @Override public void update(Long id, CharityCase entity) {}
    @Override public CharityCase findOne(Long id) { return null; }
}