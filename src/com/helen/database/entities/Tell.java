package com.helen.database.entities;

import java.text.SimpleDateFormat;

public class Tell {

	private final String sender;
	private final String target;
	private final java.sql.Timestamp tell_time;
	private final String message;
	private final boolean privateMessage;
	private final Integer nickGroupId = null;
	
	
	public Tell(String sender, String target, java.sql.Timestamp tell_time, String message, boolean privateMessage){
		this.sender = sender;
		this.target = target;
		this.tell_time = tell_time;
		this.message = message;
		this.privateMessage = privateMessage;
	}

	public String toString(){
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
	
	public String getSender(){
		return sender;
	}

	public String getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}
	
	public boolean isPrivate(){
		return privateMessage;
	}
}
