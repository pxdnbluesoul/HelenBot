package com.helen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class Users {

	
	public static void insertUser(String username, Date date, String hostmask){
		Connection conn = Connector.getConnection();
		
		try {
			PreparedStatement stmt = conn.prepareStatement("Insert into users (username, first_seen) values (?, ?);");
			stmt.setString(1, username);
			stmt.setDate(2, new java.sql.Date(date.getTime()));
			
			int result = stmt.executeUpdate();
			if(result > 0){
				PreparedStatement hostStatement = conn.prepareStatement("Insert into hostmasks (username, hostmask, established) values (?,?,?);");
				hostStatement.setString(1, username);
				hostStatement.setString(2,hostmask);
				hostStatement.setDate(3, new java.sql.Date(new Date().getTime()));
				hostStatement.executeUpdate();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
