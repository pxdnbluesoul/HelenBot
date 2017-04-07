package com.helen.search;

import org.jibble.pircbot.Colors;

import com.google.gson.JsonObject;

public class GoogleResults {

   
    private String title = null;
    private String link = null;
    private String snippet = null;
    
    
    public GoogleResults(JsonObject object){
    	title = object.get("title").toString().replace("\"","");
    	link = object.get("link").toString().replace("\"","");
    	snippet = object.get("snippet").toString().replace("\"","").replace("\\n","");
    }
    @Override
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	str.append(Colors.BOLD);
    	str.append(title);
    	str.append(Colors.NORMAL);
    	str.append(" - ");
    	str.append(link);
    	str.append(" :");
    	str.append(snippet);
    	return str.toString();
    }
    
    

}
