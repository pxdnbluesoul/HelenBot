package com.helen.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helen.commands.CommandData;
import com.helen.database.Configs;
import com.helen.database.Pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class WikipediaSearch {

	private final static String NOT_FOUND = "No results found.";

	private static String wikiEncode(String unencoded) throws IOException {
		return URLEncoder.encode(unencoded, "UTF-8").replaceAll("\\+", "%20");
	}

	private static String cleanContent(String content) {
		content = content.replaceAll("\\s*\\([^()]+\\)", "");
		content = content.substring(0, Math.min(300, content.length()));
		int lastPeriod = content.lastIndexOf('.');
		if(lastPeriod == -1){
			return content;
		}
		else {
			return content.substring(0, lastPeriod + 1);
		}
	}

	private static int getPage(String searchTerm) throws IOException {
		int page = -1;
		URL url = new URL(Configs.getSingleProperty("wikipediaSearchUrl").getValue() + searchTerm);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		JsonParser json = new JsonParser();
		JsonElement jsonTree = json.parse(br);
		if(jsonTree != null && jsonTree.isJsonObject()){
			JsonElement query = jsonTree.getAsJsonObject().get("query");
			if(query != null && query.isJsonObject()){
				JsonElement search = query.getAsJsonObject().get("search");
				if(search != null && search.isJsonArray()){
					JsonArray results = search.getAsJsonArray();
					if(results != null && results.size() > 0){
						JsonElement result = results.get(0);
						if(result != null && result.isJsonObject()){
							JsonElement pageid = result.getAsJsonObject().get("pageid");
							if(pageid != null && pageid.isJsonPrimitive()){
								page = pageid.getAsInt();
							}
						}
					}
				}
			}
		}

		conn.disconnect();
		return page;
	}

	public static String search(CommandData data, String searchTerm) throws IOException {
		searchTerm = searchTerm.substring(searchTerm.indexOf(' ') + 1);
		int page = getPage(wikiEncode(searchTerm));
		if(page == -1){
			return NOT_FOUND;
		}
		String pageString = "" + page;
		String link = null;
		String content = null;
		ArrayList<String> disambiguate = new ArrayList<>();
		URL url = new URL(Configs.getSingleProperty("wikipediaEntryUrl").getValue() + pageString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		JsonParser json = new JsonParser();
		JsonElement jsonTree = json.parse(br);
		if(jsonTree != null && jsonTree.isJsonObject()){
			JsonElement query = jsonTree.getAsJsonObject().get("query");
			if(query != null && query.isJsonObject()){
				JsonElement search = query.getAsJsonObject().get("pages");
				if(search != null && search.isJsonObject()){
					JsonElement result = search.getAsJsonObject().get(pageString);
					if(result != null && result.isJsonObject()){
						JsonObject resultObj = result.getAsJsonObject();
						JsonElement title = resultObj.get("title");
						if(title != null && title.isJsonPrimitive()){
							link = "(en.wikipedia.org/wiki/" + wikiEncode(title.getAsString()) + ")";
						}
						JsonElement extract = resultObj.get("extract");
						if(extract != null && extract.isJsonPrimitive()){
							content = extract.getAsString();
							if(content.endsWith(":") && content.contains("refer")){
								JsonElement links = resultObj.get("links");
								if(links != null && links.isJsonArray()){
									for(JsonElement sublink : links.getAsJsonArray()){
										if(sublink.isJsonObject()){
											JsonElement subtitle = sublink.getAsJsonObject().get("title");
											if(subtitle != null && subtitle.isJsonPrimitive()){
												String subtitleString = subtitle.getAsString();
												if(subtitleString != null && !subtitleString.contains("disambiguation")){
													disambiguate.add(subtitle.getAsString());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		conn.disconnect();

		if(content == null){
			return NOT_FOUND;
		} else if(disambiguate.isEmpty()){
			return link + " " + cleanContent(content);
		} else{
			ArrayList<String> verbatim = new ArrayList<>();
			for(String title : disambiguate){
				if(title.contains(searchTerm + " (")){
					verbatim.add(title);
				}
			}
			if(verbatim.isEmpty()){
				return Pages.disambiguateWikipedia(data, disambiguate);
			} else{
				return Pages.disambiguateWikipedia(data, verbatim);
			}
		}
	}
}
