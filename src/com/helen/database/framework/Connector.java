package com.helen.database.framework;

import org.apache.log4j.Logger;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

public class Connector {

	private final static Logger logger = Logger.getLogger(Connector.class);
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(
					"jdbc:postgresql://192.168.1.25/helen_db", "helen_bot",
					"helenrevolver");
		} catch (Exception e) {
			logger.error("Exception getting connection.",e);
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
			int i = 1;
			if (!(args.length == 1 && args[0] == null)) {
				for (Object o: args) {

					if (o instanceof String) {
						stmt.setString(i, (String) o);
					} else if (o instanceof Integer) {
						stmt.setInt(i, (Integer) o);
					} else if (o instanceof java.sql.Timestamp) {
						stmt.setTimestamp(i, (java.sql.Timestamp) o);
					} else if (o instanceof Boolean) {
						stmt.setBoolean(i, (Boolean) o);
					} else if (o instanceof Date) {
						stmt.setDate(i, new java.sql.Date(((Date) o).getTime()));
					}else if(o instanceof Array){
						stmt.setArray(i, (Array) o);
					}else {
						logger.error("Unknown object type: " + o.toString());
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

	public static CloseableStatement getArrayStatement(String queryString,
			String[] args) {
		try {
			StringBuilder s = new StringBuilder("'%");
			for(int i = 0; i < args.length; i++){
				s.append(args[i]).append('%');
				if((i + 1) < args.length ){
					s.append(",");
				}
			}

			Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(queryString);
			stmt.setString(1, s.toString());
			stmt.setString(2, s.toString());
			return new CloseableStatement(stmt, conn);
		} catch (Exception e) {
			logger.error("Error constructing statement.", e);
		}
		return null;
	}
}
