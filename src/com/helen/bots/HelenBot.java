package com.helen.bots;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

public class HelenBot extends PircBot {

	private final String version = "0.2 (Alpha)";
	private final String[] prejoinChannels = new String[]{"#metaphysicsdept", "#helen_testing_ground"};
	
	private final String server = "avarice.wa.us.synirc.net";
	
	private final String bot_name = "Secretary_Helen";
	private final String pass = "rancor12";
	
	
	public HelenBot() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		System.out.println("Initializing HelenBot v" + version);
		this.setVerbose(true);
		connect();


		joinChannels();
		
	}
	
	private void connect() throws NickAlreadyInUseException, IOException, IrcException, InterruptedException{
		this.setName(bot_name);
		this.connect(server);
		Thread.sleep(1000l);
		this.identify(pass);
		Thread.sleep(5000l);
	}
	
	private void joinChannels(){
		for(String channel : prejoinChannels){
			this.joinChannel(channel);
		}
	}
	
	public void onMessage(String channel, String sender,
            String login, String hostname, String message) {
		if(verifyMagnus(sender)){
			dispatchTable( channel,  sender,
	             login,  hostname,  message);
			}
	}
	
	public void onPrivateMessage(String sender, String login, String hostname, String message){
		dispatchTable( sender, login, hostname, message);
	}
	
	private void dispatchTable(String sender, String login, String hostname, String message){
		dispatchTable("", sender, login, hostname, message);
	}
	
	private void dispatchTable(String channel, String sender,
            String login, String hostname, String message){
		
		if (message.contains(".HelenBot")) {
			versionCheck(message, channel, sender);
		}else if(message.contains(".join")){
			enterChannel(message);
		}else if(message.contains(".leave")){
			leaveChannel(message);
		}
	}
	
	private void enterChannel(String message){
		this.joinChannel(message.split(" ")[1]);
	}
	
	private void leaveChannel(String message){
		this.partChannel(message.split(" ")[1], "I have been instructed to leave.");
	}
	
	private void versionCheck(String message, String channel, String sender){
		if(channel.isEmpty()){
			this.sendMessage(sender, "Greetings " + sender + ", I am HelenBot v" + version);
		}
		this.sendMessage(channel, sender + ": Greetings.  I am HelenBot v"+ version);
	}
	
	
	private boolean verifyMagnus(String user){
		return user.contains("Magnus");
	}
}
