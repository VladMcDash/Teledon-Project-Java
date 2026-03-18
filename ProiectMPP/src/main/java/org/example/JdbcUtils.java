package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private Properties jdbcProps;
    private static final Logger logger = LogManager.getLogger(JdbcUtils.class);
    private Connection instance = null;

    public JdbcUtils(Properties props) {
        this.jdbcProps = props;
    }

    private Connection getNewConnection() {
        logger.traceEntry();
        String url = jdbcProps.getProperty("jdbc.url");
        logger.info("Connecting to database: {}", url);
        Connection con = null;
        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.error("Error getting connection: {}", e);
        }
        return con;
    }

    public Connection getConnection() {
        logger.traceEntry();
        try {
            if (instance == null || instance.isClosed())
                instance = getNewConnection();
        } catch (SQLException e) {
            logger.error("Error DB: {}", e);
        }
        logger.traceExit(instance);
        return instance;
    }
}