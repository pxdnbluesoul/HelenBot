package com.helen.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IRCCommand {

	String command();
	boolean startOfLine();
}
