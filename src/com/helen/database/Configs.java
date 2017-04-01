package com.helen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Configs {
	
	private static final Logger logger = Logger.getLogger(Configs.class);
	
	private final static String kvQuery = "select key, value from properties";
	private final static String keysQuery = "select distinct key from properties";
	private final static String propertySet = "insert into properties (key, value, updated) values (?,?,?)";
	private final static String getProperty = "select value from properties where key like '?'";
	public static ArrayList<String> getProperty(String key){
		Connection conn = Connector.getConnection();
		ResultSet rs = null;
		ArrayList<String> results = new ArrayList<String>();
		try{
			PreparedStatement stmt = conn.prepareStatement(getProperty);
			stmt.setString(1, key);
			
			rs = stmt.executeQuery();
			while(rs.next()){
				results.add(key + ":" + rs.getString("value"));
			}
			return results;
		}catch (SQLException e) {
			logger.error("Exception attempting to retreive properties list",e);
		}finally{
			try{
				rs.close();
				conn.close();
			}catch (NullPointerException e){
				//nothing to do here!
			}catch(Exception e){
				logger.error("There was an exception closing result set",e);
			}
		}
		
		return results;
	}
	
	public static String setProperty(String key, String value){
		Connection conn = Connector.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(propertySet);
			stmt.setString(1, key);
			stmt.setString(2, value);
			stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			int updated = stmt.executeUpdate();
			if(updated > 0){
				return "Property " + key + " has been set to " + value;
			}else{
				return "Failure to set new property, please check the logs.";
			}
		} catch (SQLException e) {
			logger.error("Exception attempting to retreive properties list",e);
		}finally{
			try{
				conn.close();
			}catch (NullPointerException e){
				//nothing to do here!
			}catch(Exception e){
				logger.error("There was an exception closing result set",e);
			}
		}
		
		return "There was an error during the update process.  Please check the logs.";
	}
	
	public static ArrayList<String> getConfiguredProperties(){
		Connection conn = Connector.getConnection();
		ArrayList<String> keyValues = new ArrayList<String>();
		ResultSet rs = null;
		try {
			PreparedStatement stmt = conn.prepareStatement(kvQuery);
			
			rs = stmt.executeQuery();
			while(rs.next()){
				keyValues.add(rs.getString("key") + ":" + rs.getString("value"));
			}
		} catch (SQLException e) {
			logger.error("Exception attempting to retreive properties list",e);
		}finally{
			try{
				rs.close();
				conn.close();
			}catch (NullPointerException e){
				//nothing to do here!
			}catch(Exception e){
				logger.error("There was an exception closing result set",e);
			}
		}
		
		return keyValues;
		
	}
	
	public static ArrayList<String> getPropertyTypes(){
		Connection conn = Connector.getConnection();
		ArrayList<String> keyValues = new ArrayList<String>();
		ResultSet rs = null;
		try {
			PreparedStatement stmt = conn.prepareStatement(keysQuery);
			
			rs = stmt.executeQuery();
			while(rs.next()){
				keyValues.add(rs.getString("key"));
			}
		} catch (SQLException e) {
			logger.error("Exception attempting to retreive properties list",e);
		}finally{
			try{
				rs.close();
				conn.close();
			}catch (NullPointerException e){
				//nothing to do here!
			}catch(Exception e){
				logger.error("There was an exception closing result set",e);
			}
		}
		
		return keyValues;
		
	}

}
