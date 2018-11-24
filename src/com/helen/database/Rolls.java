package com.helen.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.helen.database.entities.Roll;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

public class Rolls {

	private static final Logger logger = Logger.getLogger(Rolls.class);

	public static void insertRoll(Roll roll) {
		if (roll.getDiceType().equals("d")) {
			try {
				CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertRolls"),
						new java.sql.Timestamp(System.currentTimeMillis()), roll.getUsername().toLowerCase(), roll.getBonus(),
						roll.getDiceMessage());

				ResultSet rs = stmt.execute();

				if (rs != null && rs.next()) {
					Integer rollId = rs.getInt("rollID");
					stmt.close();
					for (Integer i : roll.getValues()) {
						Connector.getStatement(Queries.getQuery("insertRoll"), rollId, i, roll.getDiceSize()).executeUpdate();
					}
				}
				stmt.close();
			} catch (Exception e) {
				logger.error("Exception inserting dice roll", e);
			}
		}
	}

	public static String getAverage(String size, String username) {
		String average = null;
		int diceSize;
		try {
			diceSize = Integer.parseInt(size);
		} catch (Exception e) {
			return size + " is not a valid integer";
		}
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("average"), diceSize, username.toLowerCase());

			ResultSet rs = stmt.getResultSet();

			if (rs != null && rs.next()) {
				average = "The average roll for a d" + diceSize + " for you is: " + rs.getString("average") + ".";
			}
			stmt.close();
		} catch (Exception e) {
			logger.error("There was an exception retreiving average", e);
		}

		return average;

	}

	public static ArrayList<Roll> getRolls(String username) {
		ArrayList<Roll> rolls = new ArrayList<>();
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getRolls"),username.toLowerCase(), username.toLowerCase());
			ResultSet rs = stmt.getResultSet();

			if (rs != null) {
				HashMap<Integer, Roll> rollMap = new HashMap<>();
				while (rs.next()) {
					if (!rollMap.containsKey(rs.getInt("rollId"))) {
						rollMap.put(rs.getInt("rollId"), new Roll("d", rs.getInt("size"), rs.getInt("bonus"),
								rs.getString("text"), rs.getString("username")));
					}
					rollMap.get(rs.getInt("rollId")).addRoll(rs.getInt("value"));
				}
				for (Integer i : rollMap.keySet()) {
					rolls.add(rollMap.get(i));
				}

			}
			return rolls;
		} catch (Exception e) {
			logger.error("There was an exception getting rolls", e);
		}
		return null;
	}

}
