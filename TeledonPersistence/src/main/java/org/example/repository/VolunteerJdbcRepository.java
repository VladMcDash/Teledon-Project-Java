package org.example.repository;

import org.example.domain.Volunteer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.Properties;

public class VolunteerJdbcRepository implements VolunteerRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(VolunteerJdbcRepository.class);

    public VolunteerJdbcRepository(Properties props) { dbUtils = new JdbcUtils(props); }

    @Override
    public Volunteer findByUsernameAndPassword(String username, String password) {
        logger.traceEntry("Login attempt: {}", username);
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Volunteers WHERE username=? AND password=?")) {
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Volunteer v = new Volunteer(result.getString("username"), result.getString("password"), result.getString("name"));
                    v.setId(result.getLong("id"));
                    return logger.traceExit(v);
                }
            }
        } catch (SQLException e) { logger.error(e); }
        return logger.traceExit((Volunteer) null);
    }

    @Override public void add(Volunteer entity) {}
    @Override public void delete(Long id) {}
    @Override public void update(Long id, Volunteer entity) {}
    @Override public Volunteer findOne(Long id) { return null; }
    @Override public Iterable<Volunteer> findAll() { return null; }
}