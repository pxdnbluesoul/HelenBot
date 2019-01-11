package com.helen.search;

import org.jibble.pircbot.Colors;

import com.google.gson.JsonObject;

public class GoogleResults {

   
    private String title;
    private String link;
    private String snippet;
    private boolean showSnippet;
    
    
    public GoogleResults(JsonObject object, boolean showSnippet){
    	this.title = object.get("title").toString().replace("\"","");
    	this.link = object.get("link").toString().replace("\"","");
    	this.snippet = object.get("snippet").toString().replace("\"","").replace("\\n","");
    	this.showSnippet = showSnippet;
    }
    @Override
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	str.append(link);
    	str.append(" - ");
    	str.append(Colors.BOLD);
    	str.append(title);
    	str.append(Colors.NORMAL);
    	if(showSnippet){
    		str.append(": ");
    		str.append(snippet);
			}
    	return str.toString();
    }
    
    

}
