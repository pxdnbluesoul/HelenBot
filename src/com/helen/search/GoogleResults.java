package com.helen.search;

import org.jibble.pircbot.Colors;

import com.google.gson.JsonObject;

public class GoogleResults {

   
    private String title;
    private String link;
    private String snippet;
    
    
    public GoogleResults(JsonObject object){
    	title = object.get("title").toString().replace("\"","");
    	link = object.get("link").toString().replace("\"","");
    	snippet = object.get("snippet").toString().replace("\"","").replace("\\n","");
    }
    @Override
    public String toString(){
		return Colors.BOLD +
				title +
				Colors.NORMAL +
				" - " +
				link +
				" :" +
				snippet;
    }
    
    

}
