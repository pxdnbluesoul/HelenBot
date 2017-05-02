package com.helen.bots;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

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
	
	private static final Logger logger = Logger.getLogger(HelenBot.class);
	private static HashMap<String, Boolean> jarvisPresent = new HashMap<String, Boolean>();

	public HelenBot() throws NickAlreadyInUseException, IOException,
			IrcException, InterruptedException {
		logger.info("Initializing HelenBot v"
				+ Configs.getSingleProperty("version").getValue());
		this.setVerbose(true);
		connect();
		joinChannels();
		cmd = new Command(this);
	}

	private void connect() throws NickAlreadyInUseException, IOException,
			IrcException, InterruptedException {

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
			this.sendRawLine("WHO " + channel.getValue());
		}
	}

	public void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		Users.insertUser(sender, new Date(), hostname, message, channel.toLowerCase());
		cmd.dispatchTable(new CommandData(channel, sender, login, hostname,
				message));
	}

	public void onPrivateMessage(String sender, String login, String hostname,
			String message) {
		dispatchTable(sender, login, hostname, message);
	}

	private void dispatchTable(String sender, String login, String hostname,
			String message) {
		cmd.dispatchTable(new CommandData("", sender, login, hostname, message));
	}

	public void log(String line) {
		if (!line.contains("PING :") && !line.contains(">>>PONG")) {
			logger.info(System.currentTimeMillis() + " " + line);
		}
	}

	public void onUserList(String channel, Users[] users) {
		logger.info("Recieved user list for a channel" + channel);
	}

	public void onServerResponse(int code, String response) {
		if (code == 352) {
			if(response.split(" ")[5].equalsIgnoreCase("jarvis")){
				jarvisPresent.put(response.split(" ")[1].toLowerCase(), true);
			}
		}
	}

	public void onPart(String channel, String sender, String login,
			String hostname) {
		if (sender.equalsIgnoreCase("jarvis")) {
			if (jarvisPresent.containsKey(channel.toLowerCase())) {
				jarvisPresent.put(channel.toLowerCase(), false);
			}
		}
	}
	
	public void onQuit(String sourceNick,
            String sourceLogin,
            String sourceHostname,
            String reason) {
		
		if (sourceNick.equalsIgnoreCase("jarvis")) {
			for(String channel : jarvisPresent.keySet()){
				jarvisPresent.put(channel, false);
			}
		}
	}

	public Boolean jarvisCheck(String channel) {
		if (jarvisPresent.containsKey(channel.toLowerCase())) {
			return jarvisPresent.get(channel.toLowerCase());
		} else {
			return false;
		}
	}

	public void onJoin(String channel, String sender, String login,
			String hostname) {
		if (sender.equalsIgnoreCase("jarvis")) {
			if (jarvisPresent.containsKey(channel.toLowerCase())) {
				jarvisPresent.put(channel.toLowerCase(), true);
			}
		}
	}

}
