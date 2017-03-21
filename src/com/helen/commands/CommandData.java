package com.helen.commands;

import com.helen.bots.PropertiesManager;

public class CommandData {
	private String channel;
	private String sender;
	private String login;
	private String hostname;
	private String message;

	
	public CommandData(String channel, String sender, String login, String hostname, String message) {
		this.channel = channel;
		this.sender = sender;
		this.login = login;
		this.hostname = hostname;
		this.message = message;
	}


	public String getChannel() {
		return channel;
	}


	public String getSender() {
		return sender;
	}


	public String getLogin() {
		return login;
	}


	public String getHostname() {
		return hostname;
	}


	public String getMessage() {
		return message;
	}
	
	public String getCommand() {
		return message.split(" ")[0];
	}
	
	public String getTarget() {
		return message.split(" ")[1];
	}
	
	public String getPayload() {
		return message.substring(message.indexOf(getTarget()) + getTarget().length()).trim();
	}
	
	public boolean isAuthenticatedUser(boolean magnusMode, boolean lowLevel) {
		if(!magnusMode && lowLevel) {
			return true;
		}else {
			return PropertiesManager.getPropertyList("registeredNicks").contains(sender);
		}
	}
	
	
}
