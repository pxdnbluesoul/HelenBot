package com.helen.commands;

import java.util.HashMap;
import java.util.LinkedList;

public class RollDB {
	private static HashMap<String, LinkedList<RollData>> usersRolls = new HashMap<String, LinkedList<RollData>>();
	
	
	static void saveRoll(String user, RollData data) {
		if(usersRolls.keySet().contains(user)){
			if(usersRolls.get(user).size() >= 5) {
				usersRolls.get(user).removeLast();
				usersRolls.get(user).add(0, data);
			}
		}else {
			usersRolls.put(user, new LinkedList<RollData>());
			usersRolls.get(user).add(data);
		}
	}
	
	static LinkedList<RollData> getUserRolls(String user) {
		if(usersRolls.keySet().contains(user)) {
			if(usersRolls.get(user).size() > 0) {
				return usersRolls.get(user);
			}
		}
		return new LinkedList<RollData>();
	}
	
	
}
