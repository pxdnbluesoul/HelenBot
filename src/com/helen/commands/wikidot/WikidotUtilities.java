package com.helen.commands;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class WikidotUtilities {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String CONNECTOR = "https://www.wikidot.com/ajax-module-connector.php";
    private final String USER_LOOKUP = "https://www.wikidot.com/quickmodule.php?module=UserLookupQModule&q=";
    private final String TOKEN = "bzjxik";
    private final String PM_ACTION = "DashboardMessageAction";
    private final String SEND_MESSAGE_EVENT = "send";
    private final String FORUM_ACTION = "ForumAction";
    private final String NEW_THREAD_EVENT = "newThread";
    private static CookieStore cookieStore = new BasicCookieStore();
    private static CloseableHttpClient httpclient;

    /**
     *  This constructor is useful for generic Ajax Objects
     * @param action wikidot action you wish to send
     * @param event is tied to the wikdot action
     * @return Map for further modification
     */
    private Map<String, String> AjaxGenericObjectCreator(String action, String event) {
        Map<String, String> ajaxObject = new Map<String, String>;
        ajaxObject.put("moduleName", "empty");
        ajaxObject.put("action", action);
        ajaxObject.put("event", event);

        return ajaxObject;
    }

    /**
     *  Constructor used for generting PM Objects
     * @param action "DashboardMessageAction"
     * @param event
     * @param source
     * @param subject
     * @param user_id
     * @return
     */
    private Map<String, String> AjaxPMObjectCreator(String source, String subject, String user_id) {
        Map<String, String> ajaxObject = new Map<String, String>;
        ajaxObject.put("action", PM_ACTION);
        ajaxObject.put("event", SEND_MESSAGE_EVENT);
        ajaxObject.put("moduleName", "empty");
        ajaxObject.put("source", source);
        ajaxObject.put("subject", subject);
        ajaxObject.put("user_id", user_id);
        ajaxObject.put("wikidot_token7", TOKEN);

        return ajaxObject;
    }

    /**
     * This constructor is useful for forum posts.
     * @param categoryID: the category ID number of the forum to post to
     * @param description: String that shows up under "Thread Summary"
     * @param source: The body text of the post
     * @param title: The title of the post
     * @return a new Map that can be used using sendRequest
     */
    private Map<String, String> AjaxThreadObjectCreator(String categoryID, String description, String source, String title) {
        Map<String, String> ajaxObject = new Map<String, String>;
        ajaxObject.put("action", FORUM_ACTION);
        ajaxObject.put("category_id", categoryID);
        ajaxObject.put("description", description);
        ajaxObject.put("event", NEW_THREAD_EVENT);
        ajaxObject.put("moduleName", "empty");
        ajaxObject.put("source", source);
        ajaxObject.put("title", title);

        return ajaxObject;
    }

    /**
     *  Largely reused Chris's code from SendMultiPM.java
     * @param username
     * @return
     */
    private String getUserID(String username) {
        String getUsernameURL = USER_LOOKUP + username;
        response = sendRequest(new HashMap<String, String>(), s);

        BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder total = new StringBuilder();

        String line = null;
        String id = null;

        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        r.close();
        JsonParser parser = new JsonParser();
        JsonElement jsontree = parser.parse(total.toString());
        if (jsontree.isJsonObject()) {
            System.out.println("Object");
            JsonObject jsonObject = jsontree.getAsJsonObject();
            JsonArray arr = jsonObject.getAsJsonArray("users");
            JsonObject idj = arr.get(0).getAsJsonObject();
            id = idj.getAsJsonPrimitive("user_id").toString();

        }
        EntityUtils.consume(response.getEntity());

        return id;
    }
}
