package com.helen.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.helen.bots.HelenBot;

public class BanUser {
	
	private static HashSet<BanInfo> bansIn19;
	
	public static boolean checkIfBanned(String userName, String hostMask, String channel) {
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.add(userName);
		ArrayList<String> tempList2 = new ArrayList<String>();
		tempList.add(hostMask);
		BanInfo info = new BanInfo(tempList, tempList2, null, null);
		
		if(bansIn19.contains(info)) {
			return true;
		}
		return false;
	}
	
	public static void set19Bans(HashSet<BanInfo> bans) {
		bansIn19 = bans;
	}
}
