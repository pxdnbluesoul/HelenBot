package com.helen.bots;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import com.helen.database.Configs;

class TestBot extends PircBot {
	
	
	public TestBot(String bot_name) throws IOException, IrcException, InterruptedException{
		this.setLogin("bothost");
		this.setName(bot_name);
		try {
			this.connect("sirocco.tx.us.synirc.net");
		} catch (NickAlreadyInUseException e) {
			this.identify(Configs.getSingleProperty("pass").getValue());
		}
		this.setVerbose(true);
		
		this.identify("password");
		Thread.sleep(3000L);
		this.joinChannel("#magnusteaparty");
	}
	
	
	public void onUserList(String channel, User[] user){
		System.out.println("Got list");
	}
	
	
	
	public void onServerResponse(int code, String response){
		if(code == 352){
			System.out.println(response.split(" ")[5]);
		}
	}

}
