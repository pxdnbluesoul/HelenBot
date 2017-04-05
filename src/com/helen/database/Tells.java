package com.helen.database;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Tells {

	private final static Logger logger = Logger.getLogger(Tells.class);

	public static String sendTell(String target, String sender, String message, boolean privateMessage) {
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertTell"),
					target.toLowerCase(),
					sender,
					message,
					privateMessage);
			if (stmt.executeUpdate()) {
				return "Very well, initiating communication protocols...";
			}
		} catch (Exception e) {
			logger.error("Exception sending tell", e);
		}
		return "There was some sort of error.  Please contact DrMagnus";
	}
	
	public static ArrayList<Tell> getTells(String username){
		ArrayList<Tell> list = new ArrayList<Tell>();
		try{
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("searchTells"),
					username.toLowerCase());
			ResultSet rs = stmt.executeQuery();
			while(rs != null && rs.next()){
				list.add(new Tell(rs.getString("sender"),rs.getString("username")
						,rs.getTimestamp("tell_time"),rs.getString("message"), rs.getBoolean("privateMessage")));
			}
			stmt.close();
			return list;
		}catch (Exception e){
			logger.error("Exception retreiving tells",e);
		}
		return list;
	}
	
	public static void clearTells(String username){
		try{
			Connector.getStatement(Queries.getQuery("clearTells"),username.toLowerCase()).executeUpdate();
		}catch (Exception e){
			logger.error("Exception clearing tells",e);
		}
	}

}
