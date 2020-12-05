package com.helen.database.framework;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CloseableStatement implements AutoCloseable {
    private static final Logger logger = Logger
            .getLogger(CloseableStatement.class);
    private Connection conn;
    private ResultSet rs = null;
    private PreparedStatement stmt;

    public CloseableStatement(PreparedStatement st, Connection conn) {
        this.conn = conn;
        this.stmt = st;
    }

    public CloseableStatement() {
        this.conn = null;
        this.stmt = null;
    }

    public void close() {
        try {
            if (!conn.isClosed()) {
                conn.close();
            }

            if ((rs != null) && !rs.isClosed()) {
                rs.close();
            }

        } catch (Exception e) {
            logger.error(
                    "There was an exception trying to close a connection: ", e);
        }
    }

    public Boolean executeUpdate() throws SQLException {
        if (stmt != null) {
            boolean result;
            result = stmt.executeUpdate() > 0;
            close();
            return result;
        } else {
            return false;
        }
    }

    public Boolean executeDelete() throws SQLException {
        if (stmt != null) {
            stmt.executeUpdate();
            return true;
        }
        return false;
    }

    public ResultSet getResultSet() throws SQLException {
        return executeQuery();
    }

    public ResultSet execute() throws SQLException {
        if (stmt != null) {
            stmt.execute();
            return stmt.getResultSet();
        }
        return null;
    }

    public ResultSet executeQuery() throws SQLException {
        if (rs == null) {
            if (stmt != null) {
                rs = stmt.executeQuery();
            }
        }
        return rs;
    }

    public String toString() {
        return stmt.toString();
    }


}
