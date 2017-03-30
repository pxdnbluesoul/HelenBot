package com.irc.helen;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.helen.bots.HelenBot;

public class helen_main {

	final static Logger logger = Logger.getLogger(helen_main.class);
	
	public static void main(String[] args) throws NickAlreadyInUseException, IOException, IrcException, InterruptedException {
		HelenBot helen = null;
			logger.info("Starting up HelenBot process at: " + new Date().toString());
			helen = new HelenBot();
			logger.info("Shutting down HelenBot process at: " + new Date().toString());
		
	}
}
