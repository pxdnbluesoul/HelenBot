package com.helen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.helen.commands.CommandData;

public class Users {

	private static final Logger logger = Logger.getLogger(Users.class);

	public static void insertUser(String username, Date date, String hostmask, String message, String channel) {
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertUser"),
					username.toLowerCase(),
					new java.sql.Date(date.getTime()),
					new java.sql.Timestamp(System.currentTimeMillis()),
					message,
					message,
					channel);
			if (stmt.executeUpdate()) {
				CloseableStatement hostStatement = Connector.getStatement(Queries.getQuery("insertHostmask"),
						username.toLowerCase(),
						hostmask,
						new java.sql.Date(new Date().getTime()));
				hostStatement.executeUpdate();
			}

		} catch (SQLException e) {
			if (!e.getMessage().contains("user_unique")) {
				logger.error("Error code " + e.getErrorCode() + e.getMessage() + " Insertion exception for " + username,
						e);
			} else {
				updateUserSeen(username, message, hostmask, channel);
			}
		}
	}

	private static void updateUserSeen(String username, String message, String hostmask, String channel) {
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("updateUser"),
					new java.sql.Timestamp(System.currentTimeMillis()),
					message,
					username.toLowerCase(),
					channel);
			stmt.executeUpdate();
			
			CloseableStatement hostStatement = Connector.getStatement(Queries.getQuery("insertHostmask"),
					username.toLowerCase(),
					hostmask,
					new java.sql.Date(new Date().getTime()));
			hostStatement.executeUpdate();
		} catch (SQLException e) {
			if (!e.getMessage().contains("user_unique") && !e.getMessage().contains("host_unique")) {
				logger.error("Error code " + e.getErrorCode() + e.getMessage() + " Insertion exception for " + username,
						e);
			} else {

			}
		}
	}

	public static String seen(CommandData data) {
		try {
			if (data.getSplitMessage()[1].equals("-f")) {
				CloseableStatement stmt = Connector.getStatement(Queries.getQuery("seenFirst"),
						data.getSplitMessage()[2].toLowerCase(), data.getChannel().toLowerCase());
				ResultSet rs = stmt.executeQuery();
				if (rs != null && rs.next()) {
					return "I first met " + data.getSplitMessage()[2] + " on " + rs.getDate("first_seen").toString()
							+ " saying: " + rs.getString("first_message");
				} else {
					return "I have never seen someone by that name";
				}
			} else {
				CloseableStatement stmt = Connector.getStatement(Queries.getQuery("seen"),
						data.getSplitMessage()[1].toLowerCase(), data.getChannel().toLowerCase());
				ResultSet rs = stmt.executeQuery();
				if (rs != null && rs.next()) {
					return "I last saw " + data.getSplitMessage()[1] + " at "
							+ new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(rs.getTimestamp("last_seen"))
							+ " EST saying: " + rs.getString("last_message");
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
