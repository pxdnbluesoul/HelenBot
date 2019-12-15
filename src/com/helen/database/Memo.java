package com.helen.database;

import java.sql.ResultSet;

public class Memo implements DatabaseObject {


    public static String addMemo(String memoTitle, String message){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertMemo"), memoTitle, message, memoTitle, message, memoTitle);
            if (stmt.executeUpdate()) {
                return "*Jots that down on her clipboard* Gotcha, I'll keep that memo.";
            }else{
                return "Hmm, I didn't quite get that.  Magnus, a word?";
            }
        }catch(Exception e){
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String getMemo(String memoTitle){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("findMemo"), memoTitle);
            ResultSet rs = stmt.execute();
            if (rs != null && rs.next()) {
                return rs.getString("message");
            }else{
                return "I didn't find anything with the title: " + memoTitle;
            }
        }catch(Exception e){
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static String deleteMemo(String memoTitle){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("deleteMemo"), memoTitle);
            if (stmt.executeDelete()) {
                return "Memo deleted (or didn't exist) with title: " + memoTitle;
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
