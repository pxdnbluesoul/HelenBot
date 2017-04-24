package com.helen.database;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Tags {
	
	private static HashMap<String,Tag> tags = new HashMap<String,Tag>();
	private final static Logger logger = Logger.getLogger(Tags.class);
	
	public static Tag getTag(String tagname){
		
		if(!tags.containsKey(tagname)){
			tags.put(tagname, new Tag(tagname));
		}
		
		return tags.get(tagname);
	}
	
	public static void recordTag(Tag tag, Page p){
		if(tags.containsKey(tag.tagName)){
			if(!tags.get(tag).getPages().contains(p)){
				tags.get(tag).addPage(p);
			}
		}
	}
	
	static ArrayList<Tag> getTags(String pagename) {
		ArrayList<Tag> tags = null;
		try {
			tags = new ArrayList<Tag>();
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getTags"), pagename);
			ResultSet tagSet = stmt.getResultSet();
			while (tagSet != null && tagSet.next()) {
				tags.add(Tags.getTag(tagSet.getString("tag")));
			}
		} catch (Exception e) {
			logger.error("There was an exception attempting to grab tags", e);
		}
		return tags;
	}
	
	static void reloadTags(){
		tags = new HashMap<String,Tag>();
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getAllTags"));
			ResultSet tagSet = stmt.getResultSet();
			while (tagSet != null && tagSet.next()) {
				tags.put(tagSet.getString("pagename"), Tags.getTag(tagSet.getString("tag")));
			}
		} catch (Exception e) {
			logger.error("There was an exception attempting to grab tags", e);
		}
	}

}
