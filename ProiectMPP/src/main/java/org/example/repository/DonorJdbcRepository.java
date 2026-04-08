package org.example.repository;

import org.example.domain.Donor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("INSERT INTO Donors (name, address, phoneNumber) VALUES (?,?,?)")) {
            preStmt.setString(1, entity.getName());
            preStmt.setString(2, entity.getAddress());
            preStmt.setString(3, entity.getPhoneNumber());
            preStmt.executeUpdate();
        } catch (SQLException e) { logger.error("DB Error: {}", e); }
        logger.traceExit();
    }

    @Override
    public Donor findByName(String name) {
        logger.traceEntry("Finding donor: {}", name);
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Donors WHERE name=?")) {
            preStmt.setString(1, name);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Donor d = new Donor(result.getString("name"), result.getString("address"), result.getString("phoneNumber"));
                    d.setId(result.getLong("id"));
                    return logger.traceExit(d);
                }
            }
        } catch (SQLException e) { logger.error(e);
        }
        return logger.traceExit((Donor) null);
    }

    @Override
    public Iterable<Donor> findByNameLike(String namePart) {
        logger.traceEntry("Finding donors like {}", namePart);
        List<Donor> donors = new ArrayList<>();
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Donors WHERE name LIKE ?")) {
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

    @Override public void update(Long id, Donor entity) {}
    @Override public Donor findOne(Long id) { return null; }
    @Override public Iterable<Donor> findAll() { return new ArrayList<>(); }
    @Override public void delete(Long id) {}
}