package org.example.repository;

import org.example.JdbcUtils;
import org.example.domain.Donor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repository.DonorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DonorJdbcRepository implements DonorRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(DonorJdbcRepository.class);

    public DonorJdbcRepository(Properties props) { dbUtils = new JdbcUtils(props); }

    @Override
    public void add(Donor entity) {
        logger.traceEntry("Saving donor {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO Donors (name, address, phoneNumber) VALUES (?,?,?)")) {
            preStmt.setString(1, entity.getName());
            preStmt.setString(2, entity.getAddress());
            preStmt.setString(3, entity.getPhoneNumber());
            preStmt.executeUpdate();
        } catch (SQLException e) { logger.error(e); }
        logger.traceExit();
    }

    @Override
    public Donor findByName(String name) {
        logger.traceEntry("Finding donor by exact name: {}", name);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Donors WHERE name=?")) {
            preStmt.setString(1, name);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Donor d = new Donor(result.getString("name"), result.getString("address"), result.getString("phoneNumber"));
                    d.setId(result.getLong("id"));
                    return logger.traceExit(d);
                }
            }
        } catch (SQLException e) { logger.error(e); }
        logger.traceExit(null);
        return null;
    }

    @Override
    public Iterable<Donor> findByNameLike(String namePart) {
        logger.traceEntry("Finding donors like {}", namePart);
        Connection con = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Donors WHERE name LIKE ?")) {
            preStmt.setString(1, "%" + namePart + "%");
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Donor d = new Donor(result.getString("name"), result.getString("address"), result.getString("phoneNumber"));
                    d.setId(result.getLong("id"));
                    donors.add(d);
                }
            }
        } catch (SQLException e) { logger.error(e); }
        return logger.traceExit(donors);
    }

    @Override
    public void update(Long id, Donor entity) {
        logger.traceEntry("Updating donor id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("UPDATE Donors SET address=?, phoneNumber=? WHERE id=?")) {
            preStmt.setString(1, entity.getAddress());
            preStmt.setString(2, entity.getPhoneNumber());
            preStmt.setLong(3, id);
            preStmt.executeUpdate();
        } catch (SQLException e) { logger.error(e); }
        logger.traceExit();
    }

    @Override
    public Donor findOne(Long id) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Donors WHERE id=?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Donor d = new Donor(result.getString("name"), result.getString("address"), result.getString("phoneNumber"));
                    d.setId(result.getLong("id"));
                    return d;
                }
            }
        } catch (SQLException e) { logger.error(e); }
        return null;
    }

    @Override public Iterable<Donor> findAll() { return new ArrayList<>(); }
    @Override public void delete(Long id) {}
}