package com.helen.database.users;


import com.helen.commands.Command;
import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;
import org.jibble.pircbot.Colors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bans {

    private static final Logger logger = Logger.getLogger(Bans.class);
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static ConcurrentHashMap<String, HashSet<BanInfo>> bans = new ConcurrentHashMap<>();
    private static List<String> problematicEntries = new ArrayList<>();
    private static Map<String, BanPrep> confirmations = new ConcurrentHashMap<>();
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void updateBans() throws IOException {
        bans.clear();
/*
        URL url = new URL("http://05command.wikidot.com/chat-ban-page");
        Document result = Jsoup.parse(url, 3000);
        Element table19 = result.select("table").get(0);
        Element table17 = result.select("table").get(1);

        HashSet<BanInfo> site19Bans = populateBanList(table19, "#site19");
        bans.put("#site19", site19Bans);
        bans.put("#thecritters", site19Bans);
        bans.put("#workshop", site19Bans);
        bans.put("#site17", populateBanList(table17, "#site17"));

 */
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("loadBans"));
            ResultSet rs = stmt.getResultSet();
            Map<Integer, BanInfo> infoMap = new ConcurrentHashMap<>();

            while (rs != null && rs.next()) {
                int banId = rs.getInt("banid");
                String reason = rs.getString("reason");
                String duration = rs.getString("duration");
                String thread = rs.getString("thread");
                String value = rs.getString("value");
                String channel = rs.getString("channel");
                infoMap.computeIfAbsent(banId, i -> new BanInfo(reason, duration, thread, channel));
                String type = rs.getString("type");
                switch (type) {
                    case "username":
                        infoMap.get(banId).addUsername(value);
                        break;
                    case "hostmask":
                        infoMap.get(banId).addHostmask(value);
                        break;
                }

            }
            Map<String, HashSet<BanInfo>> completeMaps = new ConcurrentHashMap<>();
            for (Integer i : infoMap.keySet()) {
                BanInfo info = infoMap.get(i);
                completeMaps.computeIfAbsent(info.getChannel(), channel -> new HashSet<>());
                completeMaps.get(info.getChannel()).add(info);
            }

            bans.put("#site19", completeMaps.get("#site19"));
            bans.put("#thecritters", completeMaps.get("#site19"));
            bans.put("#workshop", completeMaps.get("#site19"));
            bans.put("#site17", completeMaps.get("#site17"));
        }catch(Exception e){
            logger.error("There was an exception pulling bans from the database.",e);
        }

    }

    private static HashSet<BanInfo> populateBanList(Element table, String channel) {
        HashSet<BanInfo> banList = new HashSet<>();
        Elements rows = table.select("tr");
        problematicEntries.clear();
        for (int i = 2; i < rows.size(); i++) {
            Element row = rows.get(i);
            //skip first two rows
            Elements entries = row.select("td");
            String names = entries.get(0).text();
            String ips = entries.get(1).text();
            String date = entries.get(2).text();
            String reason = entries.get(3).text();
            String thread = entries.get(4).text();
            try {
                List<String> nameList = Arrays.asList(names.split(" "));
                List<String> ipList = Arrays.asList(ips.split(" "));
                boolean isSpecial = false;
                for (String s : ipList) {
                    if (s.contains("@") || s.contains("*")) {
                        isSpecial = true;
                        break;
                    }
                }
                LocalDate bdate;

                if (date.contains("/")) {
                    bdate = LocalDate.parse(date, formatter);
                } else {
                    bdate = LocalDate.parse("12/31/2999", formatter);
                }
                banList.add(new BanInfo(nameList, ipList, reason, bdate, isSpecial, thread, channel));
            } catch (Exception e) {
                logger.error("There was an issue with this entry: " + names + " " + ips + " " + date + " " + reason);
                problematicEntries.add(names + "|" + date);
            }

        }
        return banList;
    }

    public static boolean getSuperUserBan(String username, String hostmask, String login) {
        return getUserBan(username, hostmask, "#site19", login) != null &&
                getUserBan(username, hostmask, "#site17", login) != null;
    }


    public static List<String> getProblematicEntries() {
        return problematicEntries;
    }

    public static BanInfo getUserBan(String username, String hostmask, String channel, String login) {
        LocalDate today = LocalDate.now();
        if (bans.containsKey(channel)) {
            for (BanInfo info : bans.get(channel)) {
                if ((info.getHostmasks().contains(hostmask) || info.getUserNames().contains(username)) && info.getDuration().isAfter(today)) {
                    return info;
                } else if (info.isSpecial()) {
                    for (String infoHostMask : info.getHostmasks()) {
                        if (infoHostMask.contains("@")) {
                            if (infoHostMask.contains("@*")) {
                                if (infoHostMask.split("@")[0].equalsIgnoreCase(login)) {
                                    return info;
                                }
                            }
                            if (infoHostMask.equalsIgnoreCase(login + "@" + hostmask)) {
                                return info;
                            }
                        } else if (infoHostMask.contains("*")) {
                            if (isMatch(hostmask, infoHostMask)) {
                                return info;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean isMatch(String s, String p) {
        int i = 0;
        int j = 0;
        int starIndex = -1;
        int iIndex = -1;

        while (i < s.length()) {
            if (j < p.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
                ++i;
                ++j;
            } else if (j < p.length() && p.charAt(j) == '*') {
                starIndex = j;
                iIndex = i;
                j++;
            } else if (starIndex != -1) {
                j = starIndex + 1;
                i = iIndex + 1;
                iIndex++;
            } else {
                return false;
            }
        }
        while (j < p.length() && p.charAt(j) == '*') {
            ++j;
        }
        return j == p.length();
    }

    public static String enactConfirmedBan(String username) {
        BanPrep prep = confirmations.remove(username);
        if(prep == null){
            return "You have no pending ban confirmations.";
        }else {
                try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("addBan"), prep.getReason(), prep.getChannel(), prep.getThread() == null ? "" : prep.getThread(), prep.getT().format(timeFormatter))) {
                    ResultSet rs = stmt.execute();
                    Integer value = (rs != null && rs.next()) ? rs.getInt("banid") : -1;
                    for (String user : prep.getUsers()) {
                        CloseableStatement usernamestmt = Connector.getStatement(Queries.getQuery("addUsernameBan"), value, user);
                        usernamestmt.executeUpdate();
                    }
                    for (String hostmask : prep.getHostmasks()) {
                        CloseableStatement hostmaskstmt = Connector.getStatement(Queries.getQuery("addHostmaskBan"), value, hostmask);
                        hostmaskstmt.executeUpdate();
                    }
                    updateBans();
                }catch(Exception e){
                    logger.error("There was an exception enacting a ban.",e);
                    return Command.ERROR;
                }

            return "Ban enacted for preparation: " + prep.getResponse();
        }
    }

    public static List<String> queryBan(CommandData data) {
        BanPrep prep = new BanPrep(data);
        if(prep.getFlagSet().contains("u") && prep.getFlagSet().contains("h")){
            return Collections.singletonList("You should specify EITHER -u or -h to search for a ban.");
        }else if(!(prep.getFlagSet().contains("u") || prep.getFlagSet().contains("h"))){
            return Collections.singletonList("You must specify at least -u or -h to search for a ban.");
        }
        String searchTerm = prep.getFlagSet().contains("u") ? "%" + prep.getUsers().get(0).toLowerCase() + "%" : "%" + prep.getHostmasks().get(0).toLowerCase() + "%";
        Map<Integer, BanInfo> returnInfo = new HashMap<>();
        try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("findBanByUsername"),searchTerm, searchTerm)){
            try(ResultSet rs = stmt != null ? stmt.getResultSet() : null){
                while(rs != null && rs.next()){
                    int banId = rs.getInt("banid");
                    String reason = rs.getString("reason");
                    String duration = rs.getString("duration");
                    String thread = rs.getString("thread");
                    String value = rs.getString("value");
                    String channel = rs.getString("channel");
                    returnInfo.computeIfAbsent(banId, i -> new BanInfo(reason, duration, thread, channel));
                    String type = rs.getString("type");
                    switch (type) {
                        case "username":
                            returnInfo.get(banId).addUsername(value);
                            break;
                        case "hostmask":
                            returnInfo.get(banId).addHostmask(value);
                            break;
                    }

                }
            }
            List<String> responses = new ArrayList<>();
            if(returnInfo.size() > 5) {
                Iterator<Integer> itr = returnInfo.keySet().iterator();
                for(int i = 0; i < 5; i++){
                    Integer banid = itr.next();
                    responses.add(Colors.BOLD + banid + Colors.NORMAL + " :" + returnInfo.get(banid).toString());
                }
                responses.add(" there were: " + (returnInfo.size() - 5) + " additional results.  You might want to be more specific.");
            }else{
                returnInfo.forEach((key, value) -> responses.add(Colors.BOLD + key + Colors.NORMAL + " :" + value.toString()));
            }
            return responses;
        }catch(Exception ex){
            logger.error("Something blew up in the new ban search",ex);
        }
        return Collections.emptyList();

    }

    public static String updateBan(CommandData data){
        try {
            BanPrep prep = new BanPrep(data);
            if(prep.getFlagSet().contains("i")){
                StringBuilder str = new StringBuilder();
                str.append("Added the following values for banid: " + prep.getBanid());
                for (String user : prep.getUsers()) {
                    CloseableStatement usernamestmt = Connector.getStatement(Queries.getQuery("addUsernameBan"), prep.getBanid(), user);
                    usernamestmt.executeUpdate();
                }
                str.append(" Usernames: ").append(String.join(",",prep.getUsers()));
                for (String hostmask : prep.getHostmasks()) {
                    CloseableStatement hostmaskstmt = Connector.getStatement(Queries.getQuery("addHostmaskBan"), prep.getBanid(), hostmask);
                    hostmaskstmt.executeUpdate();
                }
                str.append(" Hostmasks: ").append(String.join(",",prep.getHostmasks()));
                if(prep.getFlagSet().contains("o")){
                    str.append(" Thread: ").append(prep.getThread());
                    CloseableStatement threadStmt = Connector.getStatement(Queries.getQuery("updateBanThread"), prep.getThread(), prep.getBanid());
                    threadStmt.executeUpdate();
                }
                if(prep.getFlagSet().contains("r")){
                    str.append(" Reason: ").append(prep.getReason());
                    CloseableStatement reasonStatement = Connector.getStatement(Queries.getQuery("updateBanReason"), prep.getReason(), prep.getBanid());
                    reasonStatement.executeUpdate();
                }
                if(prep.getFlagSet().contains("t") || prep.getFlagSet().contains("d")){
                    str.append(" Duration: ").append(prep.getT().format(timeFormatter));
                    CloseableStatement durationStatement = Connector.getStatement(Queries.getQuery("updateBanDuration"),  prep.getT().format(timeFormatter), prep.getBanid());
                    durationStatement.executeUpdate();
                }
                updateBans();
                return str.toString();
            }else{
                return "You must specify the banid with -i to update a ban.";
            }
        }catch(Exception e){
            logger.error("Something went wrong updating a ban.",e);
            return Command.ERROR;
        }
    }

    public static String prepareBan(CommandData data) {

        try {
            BanPrep prep = new BanPrep(data);
            if (!prep.getResponse().isEmpty()) {
                confirmations.put(data.getSender(), prep);
                return prep.getResponse();
            } else {
                return Command.ERROR;
            }
        } catch (Exception e) {
            logger.error("Something went pretty wrong: ", e);
            return Command.ERROR;
        }
    }

    public static String cancelBan(String username) {
        confirmations.remove(username);
        return "I have successfully removed your pending ban entry, " + username;
    }
}
