package com.helen.database.data;

import com.helen.database.DatabaseObject;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;

public class Quotes implements DatabaseObject {

    public static String setQuote(String userName, String message, String channel) {
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("addQuote"), userName, channel, message, userName);
            if (stmt.executeUpdate()) {
                return "*Jots that down on her clipboard* Gotcha, I'll remember they said that.";
            } else {
                return "Hmm, I didn't quite get that.  Magnus, a word?";
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String getQuote(String username) {
        return getQuote(username, -1);
    }

    public static String getQuote(String username, Integer quoteNumber, String channel) {
        try {
            ResultSet rs = null;
            if (quoteNumber == -1) {
                try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getRandomQuote"), username, username, channel)){
                    if (stmt != null) {
                        rs = stmt.execute();
                    }
                }
            } else {
                try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getQuote"), username, username, channel, quoteNumber)){
                    if (stmt != null) {
                        rs = stmt.execute();
                    }
                }
            }
            if (rs != null && rs.next()) {
                String returnString = "[" + rs.getString("index") + "/" + rs.getString("maxIndex") + "] " + rs.getString("message");
                rs.close();
                return returnString;
            } else {
                return "I didn't find anything for: " + username;
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String deleteQuote(String userName, Integer quoteNumber) {
        try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("deleteQuote"), userName, quoteNumber)) {
            if (stmt != null && stmt.executeDelete()) {
                try(CloseableStatement adjustmentstmt = Connector.getStatement(Queries.getQuery("adjustQuoteIndexes"), userName, quoteNumber)) {
                    if (adjustmentstmt != null) {
                        adjustmentstmt.executeUpdate();
                        return "Quote number " + quoteNumber + " deleted for: " + userName;
                    }
                }
            } else {
                return "That didn't quite delete properly.  Do they have at least " + quoteNumber + " quote(s)?";
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
        return "That didn't quite delete properly.  Do they have at least " + quoteNumber + " quote(s)?";
    }

    @Override
    public String getDelimiter() {
        return null;
    }

    @Override
    public boolean displayToUser() {
        return false;
    }
}
