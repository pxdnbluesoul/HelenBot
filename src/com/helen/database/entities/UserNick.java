package com.helen.database.entities;

import com.helen.commandframework.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.Nicks;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.List;

public class UserNick {
    private final Logger logger = Logger.getLogger(UserNick.class);

    private int groupId;
    private String nickToGroup;
    private boolean newNick = false;


    public UserNick(CommandData data) {
        try {

            Integer id = Nicks.getNickGroup(data.getSender());

            if(id != null && id != -1){
                this.groupId = id;
                List<String> nicks = Nicks.getNicksByGroup(id);
                if(!nicks.contains(data.getTarget())) {
                    this.nickToGroup = data.getTarget();
                }else{
                    this.nickToGroup = null;
                }
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
                newStmt.close();
                newNick = true;
                this.nickToGroup = data.getTarget();

            }
        } catch (Exception e) {
            logger.error("Exception instantiating usernick",e);
        }
        logger.info("groupid " + groupId + ". NickToGroup: " + nickToGroup);
    }

    public int getGroupId() {
        return groupId;
    }

    public String getNickToGroup() {
        return nickToGroup;
    }

    public boolean isNewNick() {
        return newNick;
    }
}