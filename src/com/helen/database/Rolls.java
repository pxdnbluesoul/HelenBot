package com.helen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Rolls {

	private static final String insertRoll = "insert into rolls "
			+ "(throws, rolltype, size, username, bonus, roll, text, time, expanded_roll) "
			+ "values " + "(?,?,?,?,?,?,?,?,?);";

	private static final String getRolls = "select * from rolls where username like ? limit 5";

	private static final Logger logger = Logger.getLogger(Rolls.class);

	public static void insertRoll(Roll roll) {
		Connection conn = Connector.getConnection();

		try {
			PreparedStatement stmt = conn.prepareCall(insertRoll);
			stmt.setInt(1, roll.getDiceThrows());
			stmt.setString(2, roll.getDicetype());
			stmt.setInt(3, roll.getDiceSize());
			stmt.setString(4, roll.getUsername());
			stmt.setInt(5, roll.getBonus());
			stmt.setInt(6, roll.getComputedRoll());
			stmt.setString(7, roll.getDiceMessage());
			stmt.setTimestamp(8,
					new java.sql.Timestamp(System.currentTimeMillis()));
			stmt.setString(9, roll.getExpanded());

			stmt.executeUpdate();

		} catch (Exception e) {
			logger.error("Exception inserting roll", e);
		}
	}

	public static ArrayList<Roll> getRolls(String username) {
		Connection conn = Connector.getConnection();
		ArrayList<Roll> rolls = new ArrayList<Roll>();
		try {
			Statement stmt = conn.createStatement();
			String query = "select * from rolls where username like '"
					+ username + "' order by time desc limit 5";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				rolls.add(new Roll(rs.getInt("throws"), rs
						.getString("rollType"), rs.getInt("size"), rs
						.getInt("bonus"), rs.getString("text"), rs
						.getInt("roll"), rs.getString("expanded_roll"), rs
						.getString("username")));
			}

		} catch (Exception e) {
			logger.error("Exception Retreiving rolls", e);
		}
		return rolls;
	}

}
