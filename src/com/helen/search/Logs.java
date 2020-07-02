package com.helen.search;

import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Logs {

    private static final Logger logger = Logger.getLogger("Logs");

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        CommandData data = CommandData.getTestData(".flog #site19;2020-07-01 19:59:59;2020-07-01 20:04:02;duhon","#site00");
        String[] bits = data.getMessageWithoutCommand().split(";");
        String channel = bits[0].trim();

                String start = bits[1].trim();
                String end = bits[2].trim();


        getPasteForTimeRangeAndChannel(channel,start, end);
        int i = 0;
    }
    public static String getPasteForTimeRangeAndChannel(String channel, String start, String end) {
        try {
            formatter.parse(start);
            formatter.parse(end);
            StringBuilder str = new StringBuilder();
            try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("searchLogs"), channel, start, end)) {
                try (ResultSet rs = stmt != null ? stmt.getResultSet() : null) {
                    while (rs != null && rs.next()) {
                        str.append(rs.getString("timestamp")).append(" ").append(rs.getString("username"))
                                .append(": ").append(rs.getString("message")).append("\n");
                    }
                }
            }
            if(str.toString().isEmpty() ) {
                return "I'm sorry I have no data for that time period....was I taking a nap?";
            }else{
                logger.info("This is my string: _" + str.toString() + "_");
            }
            return PastebinUtils.getPasteForLog(str.toString(), "Requested Log");
        } catch (Exception e) {
            logger.error("Issue with the date: " + channel + " " + start + " " + end, e);
            return "There was a problem making the call.  Consult with my developers for how to use this function.";
        }
    }




    public static String getFormattedPasteForTimeRangeAndChannel(String channel, String start, String end, List<String> usernamesToHighlight) {
        try {
            formatter.parse(start);
            formatter.parse(end);
            StringBuilder str = new StringBuilder();
            try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("searchLogs"), channel, start, end)) {
                try (ResultSet rs = stmt != null ? stmt.getResultSet() : null) {
                    while (rs != null && rs.next()) {

                        str.append("> ").append(rs.getString("timestamp").split("\\.")[0]).append(" ");
                        String user = rs.getString("username");
                        if (usernamesToHighlight.contains(user.toLowerCase())) {
                            String color = "**##red|";
                            switch(usernamesToHighlight.indexOf(user.toLowerCase())){
                                case 0:
                                    break;
                                case 1:
                                    color = "**##blue|";
                                    break;
                                case 2:
                                    color = "**##green|";
                                    break;
                                default:
                                    color="**";
                            }
                            str.append(color).append(user).append("##").append(": ").append(rs.getString("message").trim()).append("**\n");
                        } else {
                            str.append(rs.getString("username"))
                                    .append(": ").append(rs.getString("message").trim()).append("\n");
                        }
                    }
                }
            }
            return PastebinUtils.getPasteForLog(str.toString(), "Requested Log");
        } catch (Exception e) {
            logger.error("Issue with the date: " + channel + " " + start + " " + end, e);
            return "There was a problem making the call.  Consult with my developers for how to use this function.";
        }
    }
}
