package com.helen.search;



import com.helen.database.framework.Configs;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static java.util.stream.Collectors.joining;


public class PastebinUtils {
    public static final String API_POST_URL = "http://pastebin.com/api/api_post.php";

    public static final String API_LOGIN_URL = "http://pastebin.com/api/api_login.php";

    public static class GuestPaste extends Paste {}

    public static String getPasteForLog(String message, String title) {
        final String devKey = Configs.getSingleProperty("pastebinApiKey").get().getValue();
        final String userName = Configs.getSingleProperty("pastebinPW").get().getValue();
        final String password = Configs.getSingleProperty("pastebinUsername").get().getValue();
        final AccountCredentials pasteBin = new AccountCredentials(devKey, userName, password);
        final GuestPaste guestPaste = new GuestPaste();

        guestPaste.setContent(message);
        guestPaste.setTitle(title);
        guestPaste.setExpiration("N");
        guestPaste.setVisibility("2");

        return createPaste(pasteBin, guestPaste);
    }
    public static void updateUserSessionKey(AccountCredentials accountCredentials) {
        if (!accountCredentials.getUserSessionKey().isPresent())
            accountCredentials.setUserKey(Optional.ofNullable(fetchUserKey(accountCredentials)));
    }

    public static String fetchUserKey(AccountCredentials accountCredentials) {
        if (!accountCredentials.getUserName().isPresent())
            throw new IllegalArgumentException("Missing username!");

        if (!accountCredentials.getPassword().isPresent())
            throw new IllegalArgumentException("Missing password!");

        Map<Object, Object> parameters = new HashMap<>();

        parameters.put(PasteBinApiParams.DEV_KEY, accountCredentials.getDevKey());
        parameters.put(PasteBinApiParams.USER_NAME, accountCredentials.getUserName().get());
        parameters.put(PasteBinApiParams.USER_PASSWORD, accountCredentials.getPassword().get());

        return requiresValidResponse(post(API_LOGIN_URL, parameters))
                .orElseThrow(() -> new RuntimeException("Error while fething user session key."));
    }

    private static String createPaste(AccountCredentials accountCredentials, Paste paste){
        Objects.requireNonNull(accountCredentials);
        Objects.requireNonNull(paste, "The paste can't be null, are you kidding?");
        Objects.requireNonNull(paste.getContent(), "The paste need to have some Content");

        final boolean isGuestPaste = paste instanceof GuestPaste;

        if (accountCredentials.getUserName().isPresent() && accountCredentials.getPassword().isPresent() && !isGuestPaste)
            updateUserSessionKey(accountCredentials);

        Map<Object, Object> parameters = new HashMap<>();

        //Required
        parameters.put(PasteBinApiParams.DEV_KEY, accountCredentials.getDevKey());
        parameters.put(PasteBinApiParams.OPTION, "paste");
        parameters.put(PasteBinApiParams.PASTE_CODE, paste.getContent());

        // Optionals
        // WHen is a guest paste, i don't use the api user key
        if (!isGuestPaste) {
            accountCredentials.getUserSessionKey().ifPresent(k -> parameters.put(PasteBinApiParams.USER_KEY, k));
        }

        if (StringUtils.isNotBlank(paste.getTitle()))
            parameters.put(PasteBinApiParams.PASTE_NAME, paste.getTitle());


        if (paste.getVisibility() != null)
            parameters.put("api_paste_private", paste.getVisibility());

        final String pasteUrl = requiresValidResponse(post(API_POST_URL, parameters)).get();

        // Update the paste key from the URL to the paste
        paste.setKey(getPasteKeyFromUrl(pasteUrl));
        paste.setUrl(pasteUrl);

        return pasteUrl;
    }
    public static String getPasteKeyFromUrl( String url) {
        if (url == null)
            return null;

        if (!url.contains("http://pastebin.com/") && !url.contains("https://pastebin.com")) {
            throw new IllegalArgumentException("Not a valid paste bin url!");
        }

        return url.substring(url.indexOf("http://pastebin.com/") + 20);
    }

    enum PasteBinApiParams {
      DEV_KEY("api_dev_key"),
      PASTE_CODE("api_paste_code"),
        PASTE_NAME("api_paste_name"),
        USER_KEY("api_user_key"),
        OPTION("api_option"),
        USER_NAME("api_user_name"),
        USER_PASSWORD("api_user_password");

        private final String param;

        PasteBinApiParams(String param) {
            this.param = param;
        }

        @Override
        public String toString() {
            return param;
        }
    }


    public static void validateResponse(Optional<String> response) {
        if (response == null || !response.isPresent())
            throw new RuntimeException("Empty response");

        final String resp = response.get();

        if (resp.toLowerCase().contains("bad api request"))
            throw new RuntimeException(("Error: " + resp));
    }

    public static Optional<String> requiresValidResponse(Optional<String> response) {
        validateResponse(response);
        return response;
    }

    public static Optional<String> post( String url,  Map<Object, Object> parameters) {
        try {
            final URL url1 = new URL(url);

            final HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.addRequestProperty("Accept", Locale.getDefault().getLanguage());
            urlConnection.setConnectTimeout(30000);

            final String params = getParams(parameters);

            String result = doRequest(url, urlConnection, params, false);

            return Optional.of(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String getParams( Map<Object, Object> parameters) {
        return parameters.entrySet()
                .stream()
                .map(e -> String.format("%s=%s", encodeUTF8(e.getKey().toString()), encodeUTF8(e.getValue().toString())))
                .collect(joining("&"));
    }
    static String encodeUTF8( String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    static String doRequest( String url,  HttpURLConnection urlConnection,  String params, boolean keepResponseMultiLine) throws IOException {
        if (StringUtils.isNotBlank(params)) {
            urlConnection.setDoOutput(true);

            try (DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream())) {
                dataOutputStream.writeBytes(params);
            }
        }

        final int responseCode = urlConnection.getResponseCode();

        if (responseCode != 200)
            throw new RuntimeException(String.format("Error posting to %s using the params: %s", url, params));

        String result;


        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            result = br.lines().collect(joining(keepResponseMultiLine ? "\n" : ""));
        }

        return result;
    }
}
