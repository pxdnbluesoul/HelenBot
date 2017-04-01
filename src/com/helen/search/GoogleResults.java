package com.helen.search;

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
    	kind = object.get("kind").toString().replace("\"","");
    	title = object.get("title").toString().replace("\"","");
    	link = object.get("link").toString().replace("\"","");
    	displayLink = object.get("displayLink").toString().replace("\"","");
    	snippet = object.get("snippet").toString().replace("\"","").replace("\\n","");
    	htmlSnippet = object.get("htmlSnippet").toString().replace("\"","");
    	cached = object.get("cacheId").toString().replace("\"","");
    	formattedUrl = object.get("formattedUrl").toString().replace("\"","");
    	htmlFormattedUrl = object.get("htmlFormattedUrl").toString().replace("\"","");
    }
    @Override
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	
    	str.append(title);
    	str.append(" - ");
    	str.append(link);
    	str.append(" :");
    	str.append(snippet);
    	return str.toString();
    }
    
    

}
