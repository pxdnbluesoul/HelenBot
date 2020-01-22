package com.helen.database.users;

import com.helen.commands.Command;
import com.helen.commands.CommandData;
import com.helen.database.framework.*;
import org.apache.log4j.Logger;
import org.jibble.pircbot.Colors;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.*;

public class Pronouns {

    private static final Logger logger = Logger.getLogger(Pronouns.class);

    private static ArrayList<String> bannedNouns = new ArrayList<String>();

    static {
        reload();
    }

    private static String getPronounsByUsername(List<String> users, String username) throws Exception {
        StringBuilder str = new StringBuilder();
        Array a = Connector.getConnection().createArrayOf("text", users.toArray());

        try(CloseableStatement stmt = Connector.getStatement(
                Queries.getQuery("getPronounByArray"), a)) {
            try (ResultSet rs = stmt != null ? stmt.getResultSet() : null) {
                Set<String> accepted = new HashSet<>();
                Set<String> pronouns = new HashSet<>();
                Set<String> unaccepted = new HashSet<>();
                if (rs != null) {
                    while (rs.next()) {
                        if(rs.getBoolean("unaccepted")){
                            unaccepted.add(rs.getString("pronoun"));
                        }else if (rs.getBoolean("accepted")) {
                            accepted.add(rs.getString("pronoun"));
                        } else
                            pronouns.add(rs.getString("pronoun"));
                    }
                }
                if (accepted.size() > 0 || pronouns.size() > 0 || unaccepted.size() > 0) {
                    if (pronouns.size() > 0) {
                        str.append(username);
                        str.append(" uses the following pronouns: ");
                        str.append(String.join(", ", pronouns));
                        str.append(";");
                    } else {
                        str.append(" I have no record of pronouns;");
                    }
                    if (accepted.size() > 0) {
                        str.append(" ");
                        str.append(username);
                        str.append(" accepts the following pronouns: ");
                        str.append(String.join(", ", accepted));
                    } else {
                        str.append(" I have no record of accepted pronouns");
                    }
                    if (unaccepted.size() > 0) {
                        str.append(" ");
                        str.append(username);
                        str.append(Colors.BOLD);
                        str.append(" DOES NOT ");
                        str.append(Colors.NORMAL);
                        str.append("accept the following pronouns: ");
                        str.append(String.join(", ", unaccepted));
                    } else {
                        str.append(" I have no record of unaccepted pronouns");
                    }
                    str.append(".");
                } else {
                    str.append("I'm sorry, I don't have any record of pronouns for ").append(username);
                }
                return str.toString();
            }
        }
    }

    public static String getPronouns(String user) {
        try {
            Integer groupId = Nicks.getNickGroup(user);
            if (groupId != null && groupId != -1) {
                List<String> nicks = Nicks.getNicksByGroup(groupId);
                if (nicks != null) {
                    return getPronounsByUsername(nicks, user);
                } else {
                    return "I'm sorry, something went wrong.";
                }
            } else {
                return getPronounsByUsername(Collections.singletonList(user), user);
            }
        } catch (Exception e) {
            logger.error("Error retreiving pronouns", e);
        }
        return Command.ERROR;
    }

    public static String insertPronouns(CommandData data) {
        if (data.getSplitMessage().length > 1) {
            try {
                boolean accepted = false;
                boolean unaccepted = false;
                if(data.getSplitMessage()[1].equalsIgnoreCase("accepted")){
                    accepted = true;
                }else if(data.getSplitMessage()[1].equalsIgnoreCase("unaccepted")){
                    unaccepted = true;
                }
                String nounData = data.getMessage().substring(
                        data.getMessage().split(" ")[0].length()
                );
                StringBuilder str = new StringBuilder();
                try(CloseableStatement stmt = Connector.getStatement(Queries
                        .getQuery("establishPronoun"), data.getSender()
                        .toLowerCase(), accepted,unaccepted)){
                    try(ResultSet rs = stmt != null ? stmt.execute() : null){


                        String[] nouns = nounData.replace(",", " ").replace("/", " ")
                                .replace("\\", " ").trim().replaceAll(" +", " ")
                                .split(" ");
                        if (rs != null && rs.next()) {
                            int pronounID = rs.getInt("pronounID");
                            int j = 0;
                            if (nouns[0].equalsIgnoreCase("accepted") || nouns[0].equalsIgnoreCase("unaccepted")) {
                                j = 1;
                            }

                            for (int i = j; i < nouns.length; i++) {
                                if (bannedNouns.contains(nouns[i].trim().toLowerCase())) {
                                    return "Your noun list contains a banned term: "
                                            + nouns[i];
                                }
                            }

                            for (int i = j; i < nouns.length; i++) {

                                try(CloseableStatement insertStatement = Connector
                                        .getStatement(
                                                Queries.getQuery("insertPronoun"),
                                                pronounID, nouns[i])){
                                    if (insertStatement != null) {
                                        insertStatement.executeUpdate();
                                        if (str.length() > 0) {
                                            str.append(", ");
                                        }
                                        str.append(nouns[i]);
                                    }

                                }

                            }
                        }
                    }
                }
                return "Inserted the following pronouns: "
                        + str.toString()
                        + " as "
                        + (data.getSplitMessage()[1]
                        .equalsIgnoreCase("accepted") ? "accepted pronouns."
                        : "pronouns");
            } catch (Exception e) {
                logger.error("Error retreiving pronouns", e);
            }
            return Command.ERROR;
        } else {
            return "Usage: .setPronouns (accepted) (unaccepted) pronoun1 pronoun2 pronoun3 ... pronoun[n]";
        }
    }

    public static String clearPronouns(String username) {
        try (CloseableStatement stmt = Connector.getStatement(
                Queries.getQuery("deleteNouns"), username.toLowerCase())){
            stmt.executeUpdate();

            try(CloseableStatement secondStatement = Connector.getStatement(Queries.getQuery("deleteNounRecord"), username.toLowerCase())){
                secondStatement.executeUpdate();
            }
            return "Deleted all pronoun records for " + username + ".";
        } catch (Exception e) {
            logger.error("Error retreiving pronouns", e);
        }
        return Command.ERROR;
    }

    public static void reload() {
        bannedNouns = new ArrayList<String>();
        // Just a couple examples.
        bannedNouns.add("apache");
        bannedNouns.add("helicopter");
        // More are added on the back end.
        for (Config c : Configs.getProperty("bannedNouns")) {
            bannedNouns.add(c.getValue());
        }
    }

}
