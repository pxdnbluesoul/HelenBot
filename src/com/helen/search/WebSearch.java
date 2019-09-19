package com.helen.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helen.database.Configs;

public class WebSearch {

	final static Logger logger = Logger.getLogger(WebSearch.class);

	private static GoogleResults eitherSearch(String searchTerm, boolean image) throws IOException {
		URL url = new URL(Configs.getSingleProperty("googleurl").getValue()
				+ Configs.getSingleProperty("apiKey").getValue()
				+ "&cx="
				+ Configs.getSingleProperty("customEngine").getValue()
				+ "&q="
				+ URLEncoder.encode(searchTerm.substring(searchTerm.indexOf(' ') + 1), "UTF-8")
				+ "&alt=json"
				+ (image ? "&searchType=image" : ""));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		GoogleResults searchResult = null;
		JsonParser json = new JsonParser();
		JsonElement jsonTree = json.parse(br);
		if(jsonTree != null && jsonTree.isJsonObject()){
			JsonObject jsonObject = jsonTree.getAsJsonObject();

			JsonElement items = jsonObject.get("items");
			if(items != null && items.isJsonArray()){
				JsonArray itemsArray = items.getAsJsonArray();

				JsonElement result = itemsArray.get(0);
				if(result != null && result.isJsonObject()){
					JsonObject resultMap = result.getAsJsonObject();
					searchResult = new GoogleResults(resultMap, !image);
				}
			}
		}

		conn.disconnect();

		return searchResult;
	}

	public static GoogleResults search(String searchTerm) throws IOException {
		return eitherSearch(searchTerm, false);
	}

	public static GoogleResults imageSearch(String searchTerm) throws IOException {
		return eitherSearch(searchTerm, true);
	}
}
