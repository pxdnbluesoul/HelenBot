package com.irc.helen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.xmlrpc.XmlRpcException;

public class TestMain {



	public static void main(String args[]) throws XmlRpcException, ParseException {
		Connection con = getConnection();
		System.out.println("Win");
	}

	
	
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(
					"jdbc:postgresql://helen_bot:5432/helen_db", "helen_bot",
					"helenrevolver");
		} catch (SQLException e) {
			return null;
		}
	}

}
