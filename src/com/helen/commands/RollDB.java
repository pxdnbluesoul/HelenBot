package com.helen.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class RollDB {
	private static HashMap<String, Queue<RollData>> usersRolls = new HashMap<String, Queue<RollData>>();

	static void saveRoll(String user, RollData data) {
		if (usersRolls.keySet().contains(user)) {
			usersRolls.get(user).remove();
			usersRolls.get(user).offer(data);
		} else {
			usersRolls.put(user, new LinkedList<RollData>());
			for (int i = 0; i < 5; i++) {
				usersRolls.get(user).offer(null);
			}
			usersRolls.get(user).add(data);
		}
	}

	static String getUserRolls(String user) {
		if (usersRolls.keySet().contains(user)) {
			StringBuilder str = new StringBuilder();
			str.append("{");
			for (RollData roll : usersRolls.get(user)) {
				if (roll != null) {
					str.append(roll.getRoll());
					str.append(",");
				}
			}
			str.delete(str.length() - 1, str.length());
			str.append("}");
			return str.toString();
			
		}
		return null;
	}

}
