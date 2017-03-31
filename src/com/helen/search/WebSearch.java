package com.helen.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.helen.bots.PropertiesManager;

public class WebSearch {

	final static Logger logger = Logger.getLogger(WebSearch.class);
	public static GoogleResults search(String searchTerm) throws IOException {

		URL url = new URL(PropertiesManager.getProperty("googleurl")
				+ PropertiesManager.getProperty("apiKey")
				+ "&cx="
				+ PropertiesManager.getProperty("customEngine")
				+ "&q="
				+ searchTerm +
				"&alt=json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		GoogleResults result = new Gson().fromJson(br, GoogleResults.class);
		logger.info(result.toString());
		conn.disconnect();
		
		return result;
	}
}
