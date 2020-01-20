package com.helen.database;

import java.sql.ResultSet;

public class Quotes implements DatabaseObject {

    public static String setQuote(String userName, String message){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("addQuote"), userName, message, userName);
            if (stmt.executeUpdate()) {
                return "*Jots that down on her clipboard* Gotcha, I'll remember they said that.";
            }else{
                return "Hmm, I didn't quite get that.  Magnus, a word?";
            }
        }catch(Exception e){
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String getQuote(String username){
        return getQuote(username, -1);
    }

    public static String getQuote(String username, Integer quoteNumber){
        try {
            ResultSet rs;
            if(quoteNumber == -1){
                CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getRandomQuote"),username, username);
                rs = stmt.execute();
            }else{
                CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getQuote"), username, username, quoteNumber);
                rs = stmt.execute();
            }
            if (rs != null && rs.next()) {
                return "[" + rs.getString("index") + "/" + rs.getString("maxIndex") + "] " + rs.getString("message");
            }else{
                return "I didn't find anything for: " + username;
            }
        }catch(Exception e){
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String deleteQuote(String userName, String quoteNumber){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("deleteQuote"), userName, quoteNumber);
            if (stmt.executeDelete()) {
                stmt = Connector.getStatement(Queries.getQuery("adjustQuoteIndexes"));
                stmt.executeUpdate();
                return "Quote number " + quoteNumber + " deleted for: " + userName;
            }else{
                return "That didn't quite delete properly.  Do they have at least " + quoteNumber + " quote(s)?";
            }
        }catch(Exception e){
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
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
