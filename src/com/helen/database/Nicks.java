package com.helen.database;

import com.helen.commandframework.CommandData;
import com.helen.database.entities.UserNick;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Nicks {

    private final static Logger logger = Logger.getLogger(Nicks.class);

    public static String addNick(CommandData data) {
        try {
            UserNick nick = new UserNick(data);
            if(nick.getNickToGroup() != null) {
                CloseableStatement insertStatement = Connector.getStatement(Queries.getQuery("insert_grouped_nick"),
                        nick.getGroupId(), nick.getNickToGroup().toLowerCase());
                if (insertStatement.executeUpdate()) {
                    if (nick.isNewNick()) {
                        return "Established a new nickgroup under " + data.getSender() + " and added the nick " + nick.getNickToGroup() + " as a grouped nick.";
                    }
                    return "Inserted " + nick.getNickToGroup() + " for user " + data.getSender() + ".";
                } else {
                    return "Failed to insert grouped nick during final insert, please contact DrMagnus.";
                }
            }else{
                return "Your nick is already grouped with ID: " + nick.getGroupId();
            }

        } catch (Exception e) {
            logger.error("Error inserting in to the database",e);
        }
        return "Failed to insert grouped nick, please contact DrMagnus.";
    }

    public static String deleteNick(CommandData data){
        Integer id = getNickGroup(data.getSender());
        if(id != null && id != -1){
            try {
                Connector.getStatement(Queries.getQuery("deleteGroupedNick"), data.getTarget().toLowerCase()).executeUpdate();
            }catch(Exception e){
                logger.error("Exception trying to delete nick.",e);
            }
            return "Deleted " + data.getTarget() + " from your grouped nicks.";
        }else{
            return "I didn't find any grouped nicks for your username.";
        }
    }

    private static boolean deleteNick(String data){
            try {
                return Connector.getStatement(Queries.getQuery("deleteGroupedNick"), data.toLowerCase()).executeUpdate();
            }catch(Exception e){
                logger.error("Exception trying to delete nick.",e);
            }
            return false;
    }

    public static String deleteAllNicks(CommandData data, boolean admin){
        Integer id = getNickGroup(admin ? data.getTarget() : data.getSender());
        if(id != null && id != -1) {
            List<String> nicks = getNicksByGroup(id);
            boolean flag;
            for (String nick : nicks) {
                flag = deleteNick(nick);
                if (!flag) {
                    return "There was a problem deleting nicks.";
                }
            }
            return "Deleted all nicks for your group.";
        }else{
            return "Your nick is not grouped";
        }
    }

    public static List<String> getNicksByGroup(Integer id){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getNicks"),
                    id);
            ResultSet rs = stmt.execute();
            ArrayList<String> nicks = new ArrayList<>();
            while(rs != null && rs.next()){
                nicks.add(rs.getString("nick"));
            }
            return nicks;
        }catch(Exception e){
            logger.error("There was an exception returning nick grouped nicks.",e);
        }
        return null;

    }

    public static Integer getNickGroup(String username){
        try {
            CloseableStatement stmt = Connector.getStatement(Queries
                    .getQuery("find_nick_group"), username
                    .toLowerCase());
            ResultSet rs = stmt.execute();
            if (rs != null && rs.next()) {
                Integer id = rs.getInt("id");
                stmt.close();
                return id;
            }else{
                stmt.close();
                return null;
            }
        }catch(Exception e){
            logger.error("Error looking up the nick_group id for " + username,e);
        }
        return null;
    }


}
