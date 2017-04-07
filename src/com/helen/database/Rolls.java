package com.helen.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Rolls {

	private static final Logger logger = Logger.getLogger(Rolls.class);

	private static final String insert = "insert into Rolls (rollID, time, username, bonus, text) values (DEFAULT,?,?,?,?) returning rollID";
	private static final String rollS = "insert into roll (rollID, value, size) values (?,?,?)";
	private static final String last5 = "select roller.rollID, username, text, bonus, value, size from roll, (select rollID, username,bonus, time, text from rolls order by time desc limit 5) as roller where roll.rollID = roller.rollID and roll.rollID in (select roller.rollID) and roller.username like ? order by roller.rollID";
	private static final String avg = "select round(avg(value),2) as average from roll, rolls where size = ? and username = ? and roll.rollID = rolls.rollID";
	public static void insertRoll(Roll roll) {
		if (roll.getDiceType().equals("d")) {
			try {
				CloseableStatement stmt = Connector.getStatement(insert,
						new java.sql.Timestamp(System.currentTimeMillis()), roll.getUsername(), roll.getBonus(),
						roll.getDiceMessage());

				ResultSet rs = stmt.execute();

				if (rs != null && rs.next()) {
					Integer rollId = rs.getInt("rollID");
					stmt.close();
					for (Integer i : roll.getValues()) {
						Connector.getStatement(rollS, rollId, i, roll.getDiceSize()).executeUpdate();
					}
				}
				stmt.close();
			} catch (Exception e) {
				logger.error("Exception inserting dice roll",e);
			}
		}
	}
	
	public static String getAverage(String size, String username){
		String average = null;
		Integer diceSize = 0;
		try{
			diceSize = Integer.parseInt(size);
		}catch(Exception e){
			return size + " is not a valid integer";
		}
		try {
			CloseableStatement stmt = Connector.getStatement(avg,
					diceSize,
					username);
			
			ResultSet rs = stmt.getResultSet();
			
			if(rs != null && rs.next()){
				average = "The average for " + diceSize + " is: " + rs.getString("average");
			}
			stmt.close();
		}catch(Exception e){
			logger.error("There was an exception retreiving average",e);
		}
		
		return average;
		
	}

	

	public static ArrayList<Roll> getRolls(String username) {
		ArrayList<Roll> rolls = new ArrayList<Roll>();
		try {
			CloseableStatement stmt = Connector.getStatement(last5, username);
			ResultSet rs = stmt.getResultSet();

			if (rs != null) {
				HashMap<Integer, Roll> rollMap = new HashMap<Integer, Roll>();
				while (rs.next()) {
					if (rollMap.containsKey(rs.getInt("rollId"))) {
						rollMap.get(rs.getInt("roll.Id")).addRoll(rs.getInt("value"));
					} else {
						rollMap.put(rs.getInt("rollId"), new Roll("d", rs.getInt("size"), rs.getInt("bonus"),
								rs.getString("text"), rs.getString("username")));
					}
				}
				for (Integer i : rollMap.keySet()) {
					rolls.add(rollMap.get(i));
				}

			}
			return rolls;
		} catch (Exception e) {
			logger.error("There was an exception getting rolls",e);
		}
		return null;
	}

}
