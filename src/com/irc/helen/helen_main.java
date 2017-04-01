package com.irc.helen;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

public class helen_main {

	final static Logger logger = Logger.getLogger(helen_main.class);

	public static void main(String[] args)
			throws NickAlreadyInUseException, IOException, IrcException, InterruptedException, SQLException {
		
		/*
		HelenBot helen = null;
		logger.info("Starting up HelenBot process at: " + new Date().toString());
		helen = new HelenBot();
		logger.info("Shutting down HelenBot process at: " + new Date().toString());
*/

		Connection connection = null;
		
		
		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://192.168.1.203/helen_db", "helen_bot",
					"helenrevolver");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}
}
