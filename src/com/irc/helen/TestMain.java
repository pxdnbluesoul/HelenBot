package com.irc.helen;

import java.io.IOException;
import java.text.ParseException;

import org.apache.xmlrpc.XmlRpcException;
import org.jibble.pircbot.IrcException;

import com.helen.bots.TestBot;

public class TestMain {

	
	

	
	public static void main(String args[]) throws XmlRpcException, ParseException, IOException, IrcException, InterruptedException {
		TestBot bot = new TestBot("testy_bot_magnus");
		
		bot.sendRawLine("WHO #magnusteaparty");
	}
	 
	

	
}
