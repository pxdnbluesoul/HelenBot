package com.helen.bots;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.helen.commands.Command;
import com.helen.commands.CommandData;
import com.helen.database.Config;
import com.helen.database.Configs;
import com.helen.database.Users;

public class HelenBot extends PircBot {
	
	private static Command cmd = null;
	private static FileHandler handler = null;
	
	private static final Logger logger = Logger.getLogger(HelenBot.class);
	private static final java.util.logging.Logger chatLogger = java.util.logging.Logger.getAnonymousLogger();

	public HelenBot() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		logger.info("Initializing HelenBot v" + Configs.getSingleProperty("version").getValue());
		this.setVerbose(true);
		connect();
		joinChannels();
		cmd = new Command(this);
	}

	private void connect() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		
		this.setLogin(Configs.getSingleProperty("hostname").getValue());
		this.setName(Configs.getSingleProperty("bot_name").getValue());
		try {
			this.connect(Configs.getSingleProperty("server").getValue());
		} catch (NickAlreadyInUseException e) {
			this.identify(Configs.getSingleProperty("pass").getValue());
		}
		Thread.sleep(1000l);
		this.identify(Configs.getSingleProperty("pass").getValue());
		Thread.sleep(2000l);
	}

	
	private void joinChannels() {
		for (Config channel : Configs.getProperty("autojoin")) {
			this.joinChannel(channel.getValue());
		}
	}

	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		Users.insertUser(sender, new Date(), hostname, message);
		cmd.dispatchTable(new CommandData(channel, sender, login, hostname, message));
	}

	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		dispatchTable(sender, login, hostname, message);
	}

	private void dispatchTable(String sender, String login, String hostname, String message) {
		cmd.dispatchTable(new CommandData("", sender, login, hostname, message));
	}
	
	

}
