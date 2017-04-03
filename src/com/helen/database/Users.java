package com.helen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.helen.commands.CommandData;

public class Users {

	private static final Logger logger = Logger.getLogger(Users.class);
	private static String insertStatement = "Insert into users (username, first_seen, last_seen, first_message) values (?, ?, ?, ?)";
	private static String updateStatement = "update users set last_seen = ?, last_message = ? where username = ?";
	private static String hostmaskStatement = "Insert into hostmasks (username, hostmask, established) values (?,?,?);";
	private static String seenQuery = "select ?, ? from users where username like ? ";

	public static void insertUser(String username, Date date, String hostmask, String message) {
		Connection conn = Connector.getConnection();

		try {
			PreparedStatement stmt = conn.prepareStatement(insertStatement);
			stmt.setString(1, username.toLowerCase());
			stmt.setDate(2, new java.sql.Date(date.getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			stmt.setString(4, message);

			int result = stmt.executeUpdate();
			if (result > 0) {
				PreparedStatement hostStatement = conn.prepareStatement(hostmaskStatement);
				hostStatement.setString(1, username.toLowerCase());
				hostStatement.setString(2, hostmask);
				hostStatement.setDate(3, new java.sql.Date(new Date().getTime()));
				hostStatement.executeUpdate();
			}

		} catch (SQLException e) {
			if (!e.getMessage().contains("user_unique")) {
				logger.error("Error code " + e.getErrorCode() + e.getMessage() + " Insertion exception for " + username,
						e);
			} else {
				updateUserSeen(username, message);
			}
		}
	}

	private static void updateUserSeen(String username, String message) {
		Connection conn = Connector.getConnection();

		try {

			PreparedStatement hostStatement = conn.prepareStatement(updateStatement);

			hostStatement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			hostStatement.setString(2, message);
			hostStatement.setString(3, username.toLowerCase());
			hostStatement.executeUpdate();

		} catch (SQLException e) {
			if (!e.getMessage().contains("user_unique")) {
				logger.error("Error code " + e.getErrorCode() + e.getMessage() + " Insertion exception for " + username,
						e);
			} else {

			}
		}
	}

	public static String seen(CommandData data) {
		Connection conn = Connector.getConnection();

		try {
			PreparedStatement stmt = conn.prepareStatement(seenQuery);
			if (data.getSplitMessage()[1].equals("-f")) {
				stmt.setString(1, "first_seen");
				stmt.setString(2, "first_message");
				stmt.setString(3, "'" + data.getSplitMessage()[2].toLowerCase() + "'");

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return "I first met " + data.getSplitMessage()[2] + " on " + rs.getDate("first_seen").toString()
							+ " saying: " + rs.getString("first_message");
				} else {
					return "I have never seen someone by that name";
				}
			} else {
				stmt.setString(1, "last_seen");
				stmt.setString(2, "last_message");
				stmt.setString(3, "'" + data.getSplitMessage()[1].toLowerCase() + "'");

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return "I last saw " + data.getSplitMessage()[2] + " at "
							+ new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(rs.getTimestamp("last_seen"))
							+ " saying: " + rs.getString("last_message");
				} else {
					return "I have never seen someone by that name";
				}
			}

		} catch (Exception e) {
			logger.error("There was an exception trying to look up seen.",e);
		}
		
		return "There was some kind of error with looking up seen targets.";

	}
}
