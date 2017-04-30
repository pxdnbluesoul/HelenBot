package com.helen.database;

import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.helen.commands.CommandData;

public class Hugs {
	
	private final static Logger logger = Logger.getLogger(Hugs.class);
	
	public static String getHugMessage(String username){
		String hug = "I'm sorry, I don't think you've told me what you would like me to say yet.";
		try{
		CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getHug"),username);
		ResultSet rs = stmt.getResultSet();
		if(rs!= null && rs.next()){
			hug = rs.getString("hug");
		}
		rs.close();
		stmt.close();
		}catch(Exception e){
			logger.error("Couldn't get hug",e);
		}
		
		return hug;
	}
	
	public static String storeHugmessage(CommandData data){
		try{
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertHug"), data.getSender().toLowerCase(),
					data.getMessageWithoutCommand());
			stmt.executeUpdate();
		}catch(Exception e){
			if(!e.getMessage().contains("hugs_pkey")){
				logger.error("Couldn't store the hug",e);
				
			}else{
				logger.info("Updating");
				updateHugMessage(data.getSender().toLowerCase(), data.getMessageWithoutCommand());
			}
		}
		
		return "Your hug message has been set, " + data.getSender() + ".";
	}
	
	public static void updateHugMessage(String username, String message){
		try{
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("updateHug"), message,
					username);
			stmt.executeUpdate();
			logger.info("Update finished");
		}catch(Exception e){
			logger.error("Couldn't store the hug",e);
			
		}
	}

}
