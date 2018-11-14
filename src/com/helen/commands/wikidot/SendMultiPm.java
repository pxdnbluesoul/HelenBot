
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

public class SendMultiPm {
    public static void main(String[] args) throws Exception{
        sendPost(args);

    }
    private final String USER_AGENT = "Mozilla/5.0";
    private static CookieStore cookieStore = new BasicCookieStore();

    private static CloseableHttpClient httpclient;

    private static void sendPost(String[] usernames) throws Exception {

        httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        for(String username: usernames) {
            String url = "https://www.wikidot.com/default--flow/login__LoginPopupScreen";


            Map<String, String> params = new LinkedHashMap<>();
            params.put("login", "SecretaryHelenBot@gmail.com");
            params.put("password", "HelenBot");
            params.put("action", "Login2Action");
            params.put("event", "login");
            HttpResponse response = sendRequest(params, url);
            Reader in2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            for (int c; (c = in2.read()) >= 0; )
                System.out.print((char) c);
            EntityUtils.consume(response.getEntity());

            String s = "https://www.wikidot.com/quickmodule.php?module=UserLookupQModule&q=" + username;
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

            String urlForPm = "https://www.wikidot.com/ajax-module-connector.php";

            HttpPost httpPost2 = new HttpPost(urlForPm);
            Map<String, String> nvps = new HashMap<String, String>();
            nvps.put("moduleName", "Empty");
            nvps.put("source", "Testing");
            nvps.put("subject", "TestPM");
            nvps.put("to_user_id", id);
            nvps.put("action", "DashboardMessageAction");
            nvps.put("event", "send");
            nvps.put("wikidot_token7", "bzjxik");
            response = sendRequest(nvps, urlForPm);


            System.out.println(response.getStatusLine());
            HttpEntity entity4 = response.getEntity();
            in2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            for (int c; (c = in2.read()) >= 0; )
                System.out.print((char) c);
            EntityUtils.consume(entity4);

        }
    }

    private static HttpResponse sendRequest(Map<String, String> keyValues, String url){
        try {
            BasicClientCookie cookie = new BasicClientCookie("wikidot_token7","bzjxik");
            cookie.setDomain("www.wikidot.com");
            cookie.setPath("/");
            cookieStore.addCookie(cookie);
            httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();
            HttpPost post = new HttpPost(url);


            List<NameValuePair> nvps = new ArrayList<>();
            for (String key : keyValues.keySet()) {
                nvps.add(new BasicNameValuePair(key, keyValues.get(key)));
            }

            post.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = httpclient.execute(post);
            for(Cookie c: cookieStore.getCookies()){
                System.out.println(c.getName() + ": " + c.getValue());
            }

            return response;
        } catch (Exception e){
            System.out.println("error");
            e.printStackTrace();
        }
        return null;
    }

}
