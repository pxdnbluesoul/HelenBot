package com.helen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

public class Connector {

	private final static Logger logger = Logger.getLogger(Connector.class);

	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1/helen_db", "helen_bot",
					"helenrevolver");
		} catch (SQLException e) {
			return null;
		}
	}

	public static CloseableStatement getStatement(String queryString) {
		try {
			Connection conn = getConnection();
			return new CloseableStatement(conn.prepareStatement(queryString),
					conn);
		} catch (Exception e) {
			// TODO exception text
		}
		return new CloseableStatement();
	}

	public static CloseableStatement getStatement(String queryString,
			Object... args) {
		try {
			Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(queryString);
			for(Object o:args){
				logger.info(o.toString());
			}
			int i = 1;
			if (args.length > 1 && args[0] != null) {
				for (int j = 0; j < args.length; j++) {
					if (args[j] instanceof String) {
						stmt.setString(i, (String) args[j]);
					} else if (args[j] instanceof Integer) {
						stmt.setInt(i, (Integer) args[j]);
					} else if (args[j] instanceof java.sql.Timestamp) {
						stmt.setTimestamp(i, (java.sql.Timestamp) args[j]);
					} else if (args[j] instanceof Boolean) {
						stmt.setBoolean(i, (Boolean) args[j]);
					} else if (args[j] instanceof Date) {
						stmt.setDate(i, new java.sql.Date(((Date) args[j]).getTime()));
					} else {
						logger.error("Unknown object type");
					}
					i++;
				}
			}

			return new CloseableStatement(stmt, conn);
		} catch (Exception e) {
			logger.error("Error constructing statement.", e);
		}
		return null;
	}
}
