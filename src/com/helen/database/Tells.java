package com.helen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Tells {

	private static final String insertTell = "insert into tells (username, sender, tell_time, message, privatemessage) values (?,?,(select localtimestamp),?,?);";
	private static final String searchTells = "select * from tells where username like ? order by tell_time asc;";

	private final static Logger logger = Logger.getLogger(Tells.class);

	public static String sendTell(String target, String sender, String message, boolean privateMessage) {
		Connection conn = Connector.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(insertTell);
			stmt.setString(1, target);
			stmt.setString(2, sender);
			stmt.setString(3, message);
			stmt.setBoolean(4, privateMessage);

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				return "Very well, initiating communication protocols...";
			}
		} catch (Exception e) {
			logger.error("Exception sending tell", e);
		}
		return "There was some sort of error.  Please contact DrMagnus";
	}
	
	public static ArrayList<Tell> getTells(String username){
		ArrayList<Tell> list = new ArrayList<Tell>();
		Connection conn = Connector.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(searchTells);
			stmt.setString(1, "%" + username + "%");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				list.add(new Tell(rs.getString("sender"),rs.getString("username")
						,rs.getTimestamp("tell_time"),rs.getString("message"), rs.getBoolean("privateMessage")));
			}
			
			return list;
		}catch (Exception e){
			logger.error("Exception retreiving tells",e);
		}
		return list;
	}
	
	public static void clearTells(String username){
		Connection conn = Connector.getConnection();
		try{
			Statement stmt = conn.createStatement();
			stmt.execute("delete from tells where username like '" + username + "'");
			
			
		}catch (Exception e){
			logger.error("Exception clearing tells",e);
		}
	}

}
