package com.helen.database;

import java.text.SimpleDateFormat;

public class Tell {

	private String sender;
	private String target;
	private java.sql.Timestamp tell_time;
	private String message;
	
	
	public Tell(String sender, String target, java.sql.Timestamp tell_time, String message){
		this.sender = sender;
		this.target = target;
		this.tell_time = tell_time;
		this.message = message;
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(sender);
		str.append(" said at ");
		str.append(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(tell_time));
		str.append(": ");
		str.append(message);
		return str.toString();
	}
	
	public String getSender(){
		return sender;
	}

	public String getTarget() {
		return target;
	}

	public java.sql.Timestamp getTell_time() {
		return tell_time;
	}

	public String getMessage() {
		return message;
	}
}
