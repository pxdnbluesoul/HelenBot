package com.helen.database.entities;

import com.helen.database.Configs;
import com.helen.database.framework.DatabaseObject;

public class Config implements DatabaseObject {
	
	private final String key;
	private final String value;
	private final String lastUpdated;
	private final boolean displayToPublic;
	
	
	public Config(String key, String value, String lastUpdated, boolean displayToPublic){
		this.key = key;
		this.value = value;
		this.lastUpdated = lastUpdated;
		this.displayToPublic = displayToPublic;
	}
	
	public boolean isPublic(){
		return displayToPublic;
	}
	
	public String getKey(){
		return key;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getUpdated(){
		return lastUpdated;
	}
	
	public String getDelimiter(){
		return Configs.getSingleProperty("configDelim").getValue();
	}
	
	public String toString(){

		return key +
				":" +
				value;
	}
	
	public boolean displayToUser(){
		return displayToPublic;
	}

}
