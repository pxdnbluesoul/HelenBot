package com.helen.search;

import java.util.List;

import com.google.gson.JsonObject;

public class GoogleResults {

    private String kind = null;
    private String title = null;
    private String htmlTitle = null;
    private String link = null;
    private String displayLink = null;
    private String snippet = null;
    private String htmlSnippet = null;
    private String cached = null;
    private String formattedUrl = null;
    private String htmlFormattedUrl = null;
    
    
    public GoogleResults(JsonObject object){
    	kind = object.get("kind").toString();
    	title = object.get("title").toString();
    	link = object.get("link").toString();
    	displayLink = object.get("displayLink").toString();
    	snippet = object.get("snippet").toString();
    	htmlSnippet = object.get("htmlSnippet").toString();
    	cached = object.get("cacheId").toString();
    	formattedUrl = object.get("formattedUrl").toString();
    	htmlFormattedUrl = object.get("htmlFormattedUrl").toString();
    }
    @Override
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	
    	str.append(title);
    	str.append(" - ");
    	str.append(link);
    	str.append(" :");
    	str.append(htmlSnippet);
    	return str.toString();
    }
    
    

}
