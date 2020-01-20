package com.helen.database;


import java.sql.ResultSet;

public class Timezone implements DatabaseObject {

    public static String setTimezone(String userName, String timeZone){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("setTimezone"), userName, timeZone);
            if (stmt.executeUpdate()) {
                return "*Jots that down on her clipboard* Gotcha, I'll make a note of that.";
            }else{
                return "Hmm, I didn't quite get that.  Magnus, a word?";
            }
        }catch(Exception e){
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String getTimezone(String username){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getTimezone"), username);
            ResultSet rs = stmt.execute();
            if (rs != null && rs.next()) {
                return rs.getString("timezone");
            }else{
                return "I didn't find anything for: " + username;
            }
        }catch(Exception e){
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String deleteMemo(String userName){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("deleteTimezone"), userName);
            if (stmt.executeDelete()) {
                return "Timezone deleted for: " + userName;
            }else{
                return "Hmm, I didn't quite get that.  Magnus, a word?";
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
