package com.helen.database.users;

import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;

public class Users {

    private static final Logger logger = Logger.getLogger(Users.class);

    private static final Long YEARS = 1000 * 60 * 60 * 24 * 365l;
    private static final Long DAYS = 1000 * 60 * 60 * 24l;
    private static final Long HOURS = 1000 * 60l * 60;
    private static final Long MINUTES = 1000 * 60l;

    private static final String query_text = "insert into hostmasks (username, hostmask, established) values (?,?,?);\n" +
            " on conflict (username, hostmask) \n" +
            "do update set (username, hostmask, established) = (?,?,?) where username = ?;";

    public static void insertUser(String username, String hostmask, String message, String channel) {
        try {
            java.sql.Timestamp time = new java.sql.Timestamp(System.currentTimeMillis());
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertUser"),
                    username.toLowerCase(),
                    time,
                    time,
                    message,
                    message,
                    channel,
                    time,
                    message,
                    username.toLowerCase(),
                    channel);
            if (stmt.executeUpdate()) {
                CloseableStatement hostStatement = Connector.getStatement(Queries.getQuery("insertHostmask"),
                        username.toLowerCase(),
                        hostmask,
                        time,
                        username.toLowerCase(),
                        hostmask,
                        time);
                hostStatement.executeUpdate();
            }

        } catch (Exception e) {
            logger.error("Exception updating users", e);
        }
    }

    public static String seen(CommandData data) {
        try {
            if (data.getSplitMessage()[1].equals("-f")) {
                CloseableStatement stmt = Connector.getStatement(Queries.getQuery("seenFirst"),
                        data.getSplitMessage()[2].toLowerCase(), data.getChannel().toLowerCase());
                ResultSet rs = stmt.executeQuery();
                if (rs != null && rs.next()) {
                    return "I first met " + data.getSplitMessage()[2] + " " + findTime(rs.getTimestamp("first_seen").getTime()) + " saying: " + rs.getString("first_message");
                } else {
                    return "I have never seen someone by that name";
                }
            } else {
                CloseableStatement stmt = Connector.getStatement(Queries.getQuery("seen"),
                        data.getSplitMessage()[1].toLowerCase(), data.getChannel().toLowerCase());
                ResultSet rs = stmt.executeQuery();
                if (rs != null && rs.next()) {
                    return "I last saw " + data.getSplitMessage()[1] + " " + findTime(rs.getTimestamp("last_seen").getTime()) + " saying: " + rs.getString("last_message");
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
}
