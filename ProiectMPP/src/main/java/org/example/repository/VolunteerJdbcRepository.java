package org.example.repository;

import org.example.JdbcUtils;
import org.example.domain.Volunteer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repository.VolunteerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class VolunteerJdbcRepository implements VolunteerRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(VolunteerJdbcRepository.class);

    public VolunteerJdbcRepository(Properties props) {
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Volunteer findByUsernameAndPassword(String username, String password) {
        logger.traceEntry("Finding volunteer with username: {}", username);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Volunteers WHERE username=? AND password=?")) {
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Volunteer v = new Volunteer(result.getString("username"), result.getString("password"), result.getString("name"));
                    v.setId(result.getLong("id"));
                    return logger.traceExit(v);
                }
            }
        } catch (SQLException e) {
            logger.error("DB Error: {}", e);
        }
        logger.traceExit(null);
        return null;
    }

    @Override
    public void add(Volunteer entity) {
        logger.traceEntry("Saving volunteer {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO Volunteers (username, password, name) VALUES (?,?,?)")) {
            preStmt.setString(1, entity.getUsername());
            preStmt.setString(2, entity.getPassword());
            preStmt.setString(3, entity.getName());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
    }

    @Override
    public Volunteer findOne(Long id) {
        logger.traceEntry("Finding volunteer with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Volunteers WHERE id=?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Volunteer v = new Volunteer(result.getString("username"), result.getString("password"), result.getString("name"));
                    v.setId(result.getLong("id"));
                    return logger.traceExit(v);
                }
            }
        } catch (SQLException e) { logger.error(e); }
        logger.traceExit(null);
        return null;
    }

    @Override
    public Iterable<Volunteer> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Volunteer> volunteers = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Volunteers")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Volunteer v = new Volunteer(result.getString("username"), result.getString("password"), result.getString("name"));
                    v.setId(result.getLong("id"));
                    volunteers.add(v);
                }
            }
        } catch (SQLException e) { logger.error(e); }
        return logger.traceExit(volunteers);
    }

    @Override
    public void delete(Long id) {
        logger.traceEntry("Deleting volunteer with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM Volunteers WHERE id=?")) {
            preStmt.setLong(1, id);
            preStmt.executeUpdate();
        } catch (SQLException e) { logger.error(e); }
        logger.traceExit();
    }

    @Override public void update(Long id, Volunteer entity) {}
}