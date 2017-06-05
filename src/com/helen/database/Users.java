package com.helen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.helen.commands.CommandData;

public class Users {

	private static final Logger logger = Logger.getLogger(Users.class);

	private static final Long YEARS = 1000 * 60 * 60 * 24 * 365l;
	private static final Long DAYS = 1000 * 60 * 60 * 24l;
	private static final Long HOURS = 1000 * 60l * 60;
	private static final Long MINUTES = 1000 * 60l;

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
					return "I first met " + data.getSplitMessage()[2] + " " + findTime(rs.getTimestamp("first_seen").getTime()) + " saying " + rs.getString("first_message");
				} else {
					return "I have never seen someone by that name";
				}
			} else {
				CloseableStatement stmt = Connector.getStatement(Queries.getQuery("seen"),
						data.getSplitMessage()[1].toLowerCase(), data.getChannel().toLowerCase());
				ResultSet rs = stmt.executeQuery();
				if (rs != null && rs.next()) {
					return "I last saw " + data.getSplitMessage()[1] + " " + findTime(rs.getTimestamp("last_seen").getTime()) + " saying " +  rs.getString("last_message");
				} else {
					return "I have never seen someone by that name";
				}
			}

		} catch (Exception e) {
			logger.error("There was an exception trying to look up seen.",e);

		}
		
		return "There was some kind of error with looking up seen targets.";

	}

	public static String findTime(Long time){
		time  = System.currentTimeMillis()  - time;
		Long diff = 0l;
		if(time >= YEARS){
			diff = time/YEARS;
			return (time/YEARS) + " year" + (diff > 1 ? "s" : "") + " ago";

		}else if( time >= DAYS){
			diff = time/DAYS;
			return (time/DAYS) + " day" + (diff > 1 ? "s" : "") + " ago";

		}else if(time >= HOURS){
			diff = (time/HOURS);
			return (time/HOURS) + " hour" + (diff > 1 ? "s" : "") + " ago";

		}else if( time >= MINUTES){
			diff = time/MINUTES;
			return (time/MINUTES) + " minute" + (diff > 1 ? "s" : "") + " ago";

		}else{
			return "A few seconds ago ";
		}

	}
}
