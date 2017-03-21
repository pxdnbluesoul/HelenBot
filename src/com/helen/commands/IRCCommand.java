package com.helen.commands;

public @interface IRCCommand {

	String command();
	boolean startOfLine();
}
