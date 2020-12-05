package com.irc.helen;

import com.helen.bots.HelenBot;
import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;

import java.io.IOException;
import java.util.Date;

public class helen_main {

    final static Logger logger = Logger.getLogger(helen_main.class);

    public static void main(String[] args)
            throws IOException, IrcException, InterruptedException {
        logger.info("Starting up HelenBot process at: " + new Date().toString());
        HelenBot helen = new HelenBot();
        logger.info("Initialized " + helen.toString());
        logger.info("Shutting down HelenBot process at: " + new Date().toString());

    }
}
