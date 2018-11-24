package com.helen.database;

import com.helen.database.entities.Tag;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Objects;

class Tags {
	
	private static HashMap<String, Tag> tags = new HashMap<>();
	private final static Logger logger = Logger.getLogger(Tags.class);
	
	private static Tag getTag(String tagname){
		
		if(!tags.containsKey(tagname)){
			tags.put(tagname, new Tag(tagname));
		}
		
		return tags.get(tagname);
	}

	static void reloadTags(){
		tags = new HashMap<>();
		try {
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getAllTags"));
			ResultSet tagSet = stmt.getResultSet();
			while (tagSet != null && tagSet.next()) {
				tags.put(tagSet.getString("pagename"), Tags.getTag(tagSet.getString("tag")));
			}
			Objects.requireNonNull(tagSet).close();
			stmt.close();
		} catch (Exception e) {
			logger.error("There was an exception attempting to grab tags", e);
		}
	}

}
