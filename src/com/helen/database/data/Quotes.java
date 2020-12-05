package com.helen.database.data;

import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Configs;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;

import java.sql.ResultSet;

public class Quotes {

    public static String setQuote(String userName, String message, String channel) {
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("addQuote"), userName, channel, message);
            if (stmt.executeUpdate()) {
                return "*Jots that down on her clipboard* Gotcha, I'll remember they said that.";
            } else {
                return "Hmm, I didn't quite get that.  Magnus, a word?";
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String getQuote(String username, String channel) {
        return getQuote(username, -1, channel);
    }

    public static void main(String[] args) {
        CommandData data = new CommandData(null, "DrMagnus","test","test",".q DrMagnus #site67");

        String[] tokens = data.getSplitMessage();
        if(data.getChannel() != null){
            if (Configs.getProperty("quoteChannels").stream().anyMatch(config -> config.getValue().equalsIgnoreCase(data.getChannel()))) {
                if (tokens.length > 1) {
                    if (tokens.length > 2) {
                        System.out.println( Quotes.getQuote(tokens[1], Integer.parseInt(tokens[2]), data.getChannel()));
                    } else {
                        System.out.println( Quotes.getQuote(tokens[1], data.getChannel()));
                    }
                } else if (tokens.length == 1){
                    System.out.println( Quotes.getQuote(data.getSender().toLowerCase(), data.getChannel()));
                }else {
                   System.out.println( ": Please specify a username and optionally an index.  E.g. .q username 1");
                }
            }
        }else{
            if (tokens.length > 3) {
                System.out.println( Quotes.getQuote(tokens[1], Integer.parseInt(tokens[3]), tokens[2].toLowerCase()));
            } else if(tokens.length == 3){
                System.out.println( Quotes.getQuote(tokens[1], tokens[2].toLowerCase()));
            } else{
               System.out.println( ": Please specify a username and channel, optinally an index.  E.g. .q username #channel 1");
            }
        }
    }

    public static String getQuote(String username, Integer quoteNumber, String channel) {
        try {
            ResultSet rs = null;
            if (quoteNumber == -1) {
                try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getRandomQuote"), username, channel, username, channel)){
                    if (stmt != null) {
                        rs = stmt.execute();
                    }
                }
            } else {
                try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getQuote"), username, channel, username, channel, quoteNumber)){
                    if (stmt != null) {
                        rs = stmt.execute();
                    }
                }
            }
            if (rs != null && rs.next()) {
                String returnString = "[" + rs.getString("rowNum") + "/" + rs.getString("maxIndex") + "] " + rs.getString("message");
                rs.close();
                return returnString;
            } else {
                return "I didn't find anything for: " + username;
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String deleteQuote(String userName, Integer quoteNumber, String channel) {
        try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("deleteQuote"), userName, channel, quoteNumber)) {
            if (stmt != null && stmt.executeDelete()) {
                       return "Quote number " + quoteNumber + " deleted for: " + userName;

            } else {
                return "That didn't quite delete properly.  Do they have at least " + quoteNumber + " quote(s)?";
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }
}
