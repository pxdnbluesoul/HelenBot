package com.helen.database;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Rolls {

	private static final Logger logger = Logger.getLogger(Rolls.class);

	public static void insertRoll(Roll roll) {
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertRolls"),
					"Insert Roll", roll.getDiceThrows(), roll.getDicetype(),
					roll.getDiceSize(), roll.getUsername(), roll.getBonus(),
					roll.getComputedRoll(), roll.getUsername(),
					roll.getDiceMessage(),
					new java.sql.Timestamp(System.currentTimeMillis()),
					roll.getExpanded());
			if(stmt.executeUpdate()){
				logger.info("Inserted roll successfully.");
			}
		} catch (Exception e) {
			// TODO fill in exceptions
		}
	}

	public static ArrayList<Roll> getRolls(String username) {
		ArrayList<Roll> rolls = new ArrayList<Roll>();
		try {
			CloseableStatement stmt = Connector
					.getStatement(Queries.getQuery("getRolls"), username);
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				rolls.add(new Roll(rs.getInt("throws"), rs
						.getString("rollType"), rs.getInt("size"), rs
						.getInt("bonus"), rs.getString("text"), rs
						.getInt("roll"), rs.getString("expanded_roll"), rs
						.getString("username")));
			}
			stmt.close();
		} catch (Exception e) {
			logger.error("Exception Retreiving rolls", e);
		}
		return rolls;
	}

}
