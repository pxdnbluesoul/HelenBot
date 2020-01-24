package com.helen.search;

import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

public class Logs {

    private static final Logger logger = Logger.getLogger("Logs");

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getPasteForTimeRangeAndChannel(String channel, String start, String end){
        try{
            formatter.parse(start);
            formatter.parse(end);
            StringBuilder str = new StringBuilder();
            try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("searchLogs"),channel, start, end)){
                try(ResultSet rs = stmt != null ? stmt.getResultSet() : null){
                    while(rs != null && rs.next()){
                        str.append(rs.getString("timestamp")).append(" ").append(rs.getString("username"))
                                .append(": ").append(rs.getString("message")).append("\n");
                    }
                }
            }
            String url = PastebinUtils.getPasteForLog(str.toString(),"Requested Log");
            return url;
        }catch(Exception e){
            logger.error("Issue with the date: " + channel + " " + start + " " + end,e);
            return "There was a problem making the call.  Consult with my developers for how to use this function.";
        }
    }
}
