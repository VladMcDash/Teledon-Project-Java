package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private Properties jdbcProps;
    private static final Logger logger = LogManager.getLogger(JdbcUtils.class);

    public JdbcUtils(Properties props) {
        this.jdbcProps = props;
    }

    public Connection getConnection() {
        logger.traceEntry();
        String url = jdbcProps.getProperty("jdbc.url");
        Connection con = null;
        try {
            con = DriverManager.getConnection(url);
            logger.info("Connection opened to: {}", url);
        } catch (SQLException e) {
            logger.error("Error getting connection: {}", e);
        }
        return logger.traceExit(con);
    }
}