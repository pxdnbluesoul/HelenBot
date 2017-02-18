package com.irc.helen;
import java.io.IOException;
import java.util.Date;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.helen.bots.HelenBot;

public class helen_main {


	
	public static void main(String[] args) throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		System.out.println("Starting up HelenBot process at: " + new Date().toString());
		HelenBot helen = new HelenBot();
	}
}
