package com.helen.database;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class BanInfo {
	public List<String> userNames;
	public List<String> IPs;
	public String banReason;
	public LocalDate banEnd;
	
	public BanInfo (List<String> userNames, List<String> IPs, String banReason, LocalDate bdate) {
		this.userNames = userNames;
		this.IPs = IPs;
		this.banReason = banReason;
		this.banEnd = bdate;
	}
	
	@Override
	public boolean equals (Object compare) {
		if(!(compare instanceof BanInfo)) {
			return false;
		}
		BanInfo temp = (BanInfo)compare;
		if(userNames.contains(temp.userNames.get(0))) {
			return true;
		}
		if(IPs.contains(temp.IPs.get(0))) {
			return true;
		}
		
		return false;
	}
}
