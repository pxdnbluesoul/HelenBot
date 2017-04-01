package com.helen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {

	public static Connection getConnection() {
		try {
			return  DriverManager.getConnection("jdbc:postgresql://127.0.0.1/helen_db",
					"helen_bot",
					"helenrevolver");
		} catch (SQLException e) {	
			return null;
		}
	}
}
