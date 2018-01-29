package com.helen.database;

import com.helen.commands.CommandData;

import java.sql.ResultSet;

public class UserNick {

    private int groupId;
    private String nickToGroup;


    public UserNick(CommandData data) {
        try {

            Integer id = Nicks.getNickGroup(data.getSender());
            if(id != null && id != -1){
                this.groupId = -1;
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