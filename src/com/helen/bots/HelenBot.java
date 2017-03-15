package com.helen.bots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.helen.search.GoogleResults;
import com.helen.search.WebSearch;

public class HelenBot extends PircBot {

	private static Boolean MagnusMode = true;
	private static InputStream propsIn;
	private static Boolean propsOpen = false;
	private static Properties props = new Properties();

	public HelenBot() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		System.out.println("Initializing HelenBot v" + getProperty("version"));
		this.setVerbose(true);
		connect();
		joinChannels();

	}
	
	

	private void connect() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		this.setLogin(getProperty("hostname"));
		this.setName(getProperty("bot_name"));
		try{
			this.connect(getProperty("server"));
		} catch(NickAlreadyInUseException e){
			this.identify(getProperty("pass"));
		}
		Thread.sleep(1000l);
		this.identify(getProperty("pass"));
		Thread.sleep(2000l);
	}

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

	private void joinChannels() {
		for (String channel : getPropertyList("prejoinChannels")) {
			this.joinChannel(channel);
		}
	}

	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (verifyMagnus(sender)) {
			dispatchTable(channel, sender, login, hostname, message);
		}
	}

	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if(verifyMagnus(sender)){
			dispatchTable(sender, login, hostname, message);
		}
	}

	private void dispatchTable(String sender, String login, String hostname, String message) {
		dispatchTable("", sender, login, hostname, message);
	}

	private void dispatchTable(String channel, String sender, String login, String hostname, String message) {

		if (message.contains(".HelenBot")) {
			versionCheck(message, channel, sender);
		} else if (message.contains(".join")) {
			enterChannel(message);
		} else if (message.contains(".leave")) {
			leaveChannel(message);
		} else if (message.contains(".msg")) {
			String target = message.split(" ")[1];
			StringBuilder targetMessage = new StringBuilder();
			for (int i = 2; i < message.split(" ").length; i++) {
				targetMessage.append(message.split(" ")[i]);
				if (i < message.split(" ").length - 1)
					targetMessage.append(" ");
			}
			sendMessage(target, targetMessage.toString());
		} else if (message.contains("already in use")){
			
		} else if(message.contains("toggleMagnusMode")){
			if(verifyMagnus(sender)){
				toggleMagnusMode();
			}
		}else if(message.contains("currentMode")){
			this.sendMessage(channel, "I am currently in " + (MagnusMode? "Magnus Only" : "Any User") + " mode.");
		} else if(message.substring(0, 3).equalsIgnoreCase(".g")){
			try {
				webSearch(message.split(".g")[1], channel, sender);
			} catch (IOException e) {
				sendMessage(channel, sender + ": There was some kind of error.  Please contact DrMagnus, and give him the following error code: IOEx_web_search_01");
			}
		}
	}

	private void enterChannel(String message) {
		this.joinChannel(message.split(" ")[1]);
	}

	private void leaveChannel(String message) {
		this.partChannel(message.split(" ")[1], "I have been instructed to leave.");
	}

	private void versionCheck(String message, String channel, String sender) {
		if (channel.isEmpty()) {
			this.sendMessage(sender, "Greetings " + sender + ", I am HelenBot v" + props.getProperty("version"));
		}
		this.sendMessage(channel, sender + ": Greetings.  I am HelenBot v" + props.getProperty("version"));
	}

	private boolean verifyMagnus(String user) {
		if(!MagnusMode){
			return true;
		}else{
			return restrictedAccess(user);
		}
		
		
	}
	
	private boolean restrictedAccess(String user){
		for (String str : getPropertyList("registeredNicks")) {
			
			if(str.trim().equalsIgnoreCase(user.trim())){
				return true;
			}
		}	
		return false;
	}
	
	
	
	private void toggleMagnusMode(){
		MagnusMode = !MagnusMode;
	}
	
	private String getProperty(String key) {
		if (!propsOpen) {
			openProperties();
		}
		return props.getProperty(key) == null ? "" : props.getProperty(key);
	}

	private String[] getPropertyList(String key) {
		return getProperty(key).split(",");
	}

	private void openProperties() {
		// open file for reading
		if(propsOpen){
			closeProperties();
		}
		try {
			propsIn = HelenBot.class.getResourceAsStream("/Helen.properties");
		//	propsIn = new FileInputStream(new File("Helen.properties").getAbsolutePath());
		//	System.out.println(new File("Helen.properties").getAbsolutePath());
			props.load(propsIn);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String currentDirectory = new File("").getAbsolutePath();
			System.out.println(currentDirectory);

		} catch (Exception e) {
			e.printStackTrace();

		}
		propsOpen = true;
	}
/*
	private void writeProperty(String prop, String value) {

		closeProperties();

		try {
			URL resourceUrl = HelenBot.class.getResource("/Helen.properties");
//			propsOut = HelenBot.class.getResourceAsStream("/Helen.properties");
			propsOut = new FileOutputStream(new File(resourceUrl.toURI()));
			props.setProperty(prop, value);
			props.store(propsOut, null);
			propsOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		openProperties();
	}
*/
	private void closeProperties() {
		try {
			propsIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		propsOpen = false;
	}
	
	private void webSearch(String searchTerm, String channel, String sender) throws IOException{
		GoogleResults results = WebSearch.search(searchTerm);
		
		this.sendMessage(channel, sender + ": " + results.getResponseData().getResults().get(0).getTitle() + results.getResponseData().getResults().get(0).getUrl());
		
	}

}
