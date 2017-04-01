package com.helen.bots;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.helen.commands.Command;
import com.helen.commands.CommandData;
import com.helen.database.Users;

public class HelenBot extends PircBot {
	
	private static Command cmd = null;
	
	private static final Logger logger = Logger.getLogger(HelenBot.class);

	public HelenBot() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		logger.info("Initializing HelenBot v" + PropertiesManager.getProperty("version"));
		this.setVerbose(true);
		connect();
		joinChannels();
		cmd = new Command(this);
	}

	private void connect() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		this.setLogin(PropertiesManager.getProperty("hostname"));
		this.setName(PropertiesManager.getProperty("bot_name"));
		try {
			this.connect(PropertiesManager.getProperty("server"));
		} catch (NickAlreadyInUseException e) {
			this.identify(PropertiesManager.getProperty("pass"));
		}
		Thread.sleep(1000l);
		this.identify(PropertiesManager.getProperty("pass"));
		Thread.sleep(2000l);
	}

	
	private void joinChannels() {
		for (String channel : PropertiesManager.getPropertyList("prejoinChannels")) {
			this.joinChannel(channel);
		}
	}

	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		final String mask = hostname;
		final String user = sender;
		new Thread(){
			public void run(){
				Users.insertUser(user, new Date(), mask);
			}
		}.start();
		
		cmd.dispatchTable(new CommandData(channel, sender, login, hostname, message));
	}

	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		dispatchTable(sender, login, hostname, message);
	}

	private void dispatchTable(String sender, String login, String hostname, String message) {
		cmd.dispatchTable(new CommandData("", sender, login, hostname, message));
	}

}
