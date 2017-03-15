package com.helen.search;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.Gson;

public class WebSearch {
	static String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
	static String charset = "UTF-8";
	public static GoogleResults search(String searchTerm) throws IOException{
		
		URL url = new URL(google + URLEncoder.encode(searchTerm,charset));
		Reader reader = new InputStreamReader(url.openStream(),charset);
		
		return  new Gson().fromJson(reader, GoogleResults.class);
	}
}
