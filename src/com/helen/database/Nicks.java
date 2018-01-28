package com.helen.database;

import com.helen.commands.CommandData;
import org.apache.log4j.Logger;

import java.sql.ResultSet;

public class Nicks {

    final static Logger logger = Logger.getLogger(Nicks.class);

    public String addNick(CommandData data) {
        try {

            UserNick nick = new UserNick(data);
            if (nick.getGroupId()!= -1) {
                CloseableStatement insertStatement = Connector.getStatement(Queries.getQuery("insert_grouped_nick"),
                        nick.getGroupId(), nick.getNickToGroup());
                if (insertStatement.executeUpdate()) {
                    return "Inserted " + nick.getNickToGroup() + " for user " + data.getSender() + ".";
                } else {
                    return "Failed to insert grouped nick, please contact DrMagnus.";
                }

            } else {
                return "Failed to insert grouped nick, please contact DrMagnus.";
            }
        } catch (Exception e) {
            //TODO figure this out
        }
        return "Failed to insert grouped nick, please contact DrMagnus.";
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


    private class UserNick {

        private int groupId;
        private String nickToGroup;


        public UserNick(CommandData data) {
            try {

                Integer id = getNickGroup(data.getSender());
                if(id != null && id != -1){
                    this.groupId = id;
                    this.nickToGroup = data.getTarget();
                }else {
                    CloseableStatement newStmt = Connector.getStatement(Queries
                            .getQuery("create_nick_group"));
                    ResultSet newId = newStmt.execute();
                    if (newId != null && newId.next()) {
                        this.groupId = newId.getInt("id");
                    } else {
                        this.groupId = -1;
                    }
                    newId.close();
                    this.nickToGroup = data.getSender();
                }
            } catch (Exception e) {

            }
        }

        public int getGroupId() {
            return groupId;
        }

        public String getNickToGroup() {
            return nickToGroup;
        }
    }


}
