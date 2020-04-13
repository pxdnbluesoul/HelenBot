package com.helen.database.users;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Configs;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Users {

    private static final Logger logger = Logger.getLogger(Users.class);
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final Long YEARS = 1000 * 60 * 60 * 24 * 365l;
    private static final Long DAYS = 1000 * 60 * 60 * 24l;
    private static final Long HOURS = 1000 * 60l * 60;
    private static final Long MINUTES = 1000 * 60l;

    public static List<String> getUserO5Thread(String searchquery){
        List<String> returnStrings = new ArrayList<>();
        try {
            HttpGet request = new HttpGet("https://o5.scuttle.bluesoul.net/open-api/thread/" + searchquery);

            // add request headers
            request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                System.out.println(response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);

                    Type myType = new TypeToken<ArrayList<ForumThread>>() {}.getType();
                    List<ForumThread> test = new Gson().fromJson(result, myType);
                    for(ForumThread thread : test){
                        String str = thread.getTitle()  + (thread.getSubtitle() != null ? " - " + thread.getSubtitle() : "") + " - http://05command.wikidot.com/forum/t-"  + thread.getWd_thread_id();
                        if(thread.getForum_id().equalsIgnoreCase("55")){
                            returnStrings.add(str + " (Disc)");//disc
                        }else if(thread.getForum_id().equalsIgnoreCase("56")){
                            returnStrings.add(str + " (Non-Disc)");//disc
                        }else if(thread.getForum_id().equalsIgnoreCase("59")){
                            returnStrings.add(str + " (Chat)");//disc
                        }
                    }
                }

            }
        } catch (Exception e) {
            logger.error("THere was an issue with one of bluesoul's interactions.",e);
        }

        return returnStrings;
    }

    public static void main(String[] args) {
        System.out.println(seen(CommandData.getTestData(".seen -c hammermaiden #site19", "#site67")));
        int i = 0;
    }

    public static String seen(CommandData data) {
        try {
            String channel;
            if(data.getChannel() != null && Configs.getFastConfigs("staffchannels").contains(data.getChannel()) && data.getSplitMessage().length > 3){
                    channel = data.getSplitMessage()[3].toLowerCase();
            }else{
                channel = data.getChannel();
            }
            if(!(Configs.getFastConfigs("staffchannels").contains(data.getChannel()) || Configs.getFastConfigs("logChannels").contains(data.getChannel()))){
                return "I'm sorry, .seen doesn't work in channels without logging enabled.";
            }

            if(StringUtils.isEmpty(channel)){
                channel = data.getChannel();
            }
            if (data.getSplitMessage()[1].equals("-f")) {
                CloseableStatement stmt = Connector.getStatement(Queries.getQuery("seenFirst"),
                        data.getSplitMessage()[2].toLowerCase(), channel.toLowerCase());
                ResultSet rs = stmt.executeQuery();
                if (rs != null && rs.next()) {
                    return "I first met " + data.getSplitMessage()[2] + " " + findTime(rs.getTimestamp("timestamp").getTime()) + " in " + channel + " saying: " + rs.getString("message");
                } else {
                    return "I have never seen someone by that name";
                }
            } else {
                CloseableStatement stmt = Connector.getStatement(Queries.getQuery("seen"),
                        data.getSplitMessage()[1].toLowerCase(), channel.toLowerCase());
                ResultSet rs = stmt.executeQuery();
                if (rs != null && rs.next()) {
                    return "I last saw " + data.getSplitMessage()[1] + " " + findTime(rs.getTimestamp("timestamp").getTime()) + " in " + channel + " saying: " + rs.getString("message");
                } else {
                    return "I have never seen someone by that name";
                }
            }

        } catch (Exception e) {
            logger.error("There was an exception trying to look up seen.", e);

        }

        return "There was some kind of error with looking up seen targets.";

    }

    public static String findTime(Long time) {
        time = System.currentTimeMillis() - time;
        Long diff = 0l;
        if (time >= YEARS) {
            diff = time / YEARS;
            return (time / YEARS) + " year" + (diff > 1 ? "s" : "") + " ago";

        } else if (time >= DAYS) {
            diff = time / DAYS;
            return (time / DAYS) + " day" + (diff > 1 ? "s" : "") + " ago";

        } else if (time >= HOURS) {
            diff = (time / HOURS);
            return (time / HOURS) + " hour" + (diff > 1 ? "s" : "") + " ago";

        } else if (time >= MINUTES) {
            diff = time / MINUTES;
            return (time / MINUTES) + " minute" + (diff > 1 ? "s" : "") + " ago";

        } else {
            return "a few seconds ago";
        }

    }

    private static class ForumThread implements Serializable {
        String title;
        String subtitle;
        String wd_thread_id;
        String forum_id;

        public ForumThread(){

        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getWd_thread_id() {
            return wd_thread_id;
        }

        public void setWd_thread_id(String wd_thread_id) {
            this.wd_thread_id = wd_thread_id;
        }

        public String getForum_id() {
            return forum_id;
        }

        public void setForum_id(String forum_id) {
            this.forum_id = forum_id;
        }

    }
}
