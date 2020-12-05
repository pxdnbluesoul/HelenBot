package com.helen.database.users;

import java.text.SimpleDateFormat;

public class Tell {

    private String sender;
    private String target;
    private java.sql.Timestamp tell_time;
    private String message;
    private Integer nickGroupId = null;


    public Tell(String sender, String target, java.sql.Timestamp tell_time, String message) {
        this.sender = sender;
        this.target = target;
        this.tell_time = tell_time;
        this.message = message;
    }

    public String toString() {
        return target +
                ": " +
                sender +
                " said at " +
                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(tell_time) +
                ": " +
                message;
    }

    public Integer getNickGroupId() {
        return nickGroupId;
    }

    public String getTarget() {
        return target;
    }

}
