package com.helen.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helen.database.framework.Config;
import com.helen.database.framework.Configs;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;

public class WebSearch {

    final static Logger logger = Logger.getLogger(WebSearch.class);

    private static Optional<GoogleResults> eitherSearch(String searchTerm, boolean image) throws IOException {
        Optional<Config> googleUrl = Configs.getSingleProperty("googleurl");
        Optional<Config> apiKey = Configs.getSingleProperty("apiKey");
        Optional<Config> customEngine = Configs.getSingleProperty("customEngine");
        if (googleUrl.isPresent() && apiKey.isPresent() && customEngine.isPresent()) {
            URL url = new URL(googleUrl.get().getValue()
                    + apiKey.get().getValue()
                    + "&cx="
                    + customEngine.get().getValue()
                    + "&q="
                    + URLEncoder.encode(searchTerm.substring(searchTerm.indexOf(' ') + 1), "UTF-8")
                    + "&alt=json"
                    + (image ? "&searchType=image" : ""));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if(conn.getResponseCode() == 403){
                throw new RuntimeException();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            GoogleResults searchResult = null;
            JsonParser json = new JsonParser();
            JsonElement jsonTree = json.parse(br);
            if (jsonTree != null && jsonTree.isJsonObject()) {
                JsonObject jsonObject = jsonTree.getAsJsonObject();

                JsonElement items = jsonObject.get("items");
                if (items != null && items.isJsonArray()) {
                    JsonArray itemsArray = items.getAsJsonArray();

                    JsonElement result = itemsArray.get(0);
                    if (result != null && result.isJsonObject()) {
                        JsonObject resultMap = result.getAsJsonObject();
                        searchResult = new GoogleResults(resultMap, !image);
                    }
                }
            }

            conn.disconnect();

            return searchResult == null ? Optional.empty() : Optional.of(searchResult);
        } else {
            return Optional.empty();
        }

    }

    public static Optional<GoogleResults> search(String searchTerm) throws IOException, RuntimeException {
        return eitherSearch(searchTerm, false);
    }

    public static Optional<GoogleResults> imageSearch(String searchTerm) throws IOException {
        return eitherSearch(searchTerm, true);
    }
}
