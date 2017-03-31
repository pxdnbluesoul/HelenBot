package com.helen.bots;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.helen.commands.Command;
import com.helen.commands.CommandData;
import com.helen.search.GoogleResults;
import com.helen.search.WebSearch;

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

	/*
	public void onDisconnect() {
		try {
			connect();
		} catch (NickAlreadyInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IrcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	private void joinChannels() {
		for (String channel : PropertiesManager.getPropertyList("prejoinChannels")) {
			this.joinChannel(channel);
		}
	}

	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		cmd.dispatchTable(new CommandData(channel, sender, login, hostname, message));
	}

	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		dispatchTable(sender, login, hostname, message);
	}

	private void dispatchTable(String sender, String login, String hostname, String message) {
		cmd.dispatchTable(new CommandData("", sender, login, hostname, message));
	}

	private void webSearch(String searchTerm, String channel, String sender) throws IOException {
		GoogleResults results = WebSearch.search(searchTerm);

		this.sendMessage(channel, sender + ": " + results.getResponseData().getResults().get(0).getTitle()
				+ results.getResponseData().getResults().get(0).getUrl());

	}

}
