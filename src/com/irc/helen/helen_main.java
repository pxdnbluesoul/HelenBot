package com.irc.helen;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.helen.bots.HelenBot;

class helen_main {

	private final static Logger logger = Logger.getLogger(helen_main.class);

	public static void main(String[] args)
			throws IOException, IrcException, InterruptedException {
		logger.info("Starting up HelenBot process at: " + new Date().toString());
		HelenBot helen = new HelenBot();
		logger.info("Initialized " + helen.toString());
		logger.info("Shutting down HelenBot process at: " + new Date().toString());

	}
}
