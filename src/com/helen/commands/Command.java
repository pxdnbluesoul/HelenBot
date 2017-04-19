package com.helen.commands;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import com.helen.database.Config;
import com.helen.database.Configs;
import com.helen.database.DatabaseObject;
import com.helen.database.Pages;
import com.helen.database.Pronouns;
import com.helen.database.Queries;
import com.helen.database.Roll;
import com.helen.database.Rolls;
import com.helen.database.Tell;
import com.helen.database.Tells;
import com.helen.database.Users;
import com.helen.search.WebSearch;
import com.helen.search.YouTubeSearch;

public class Command {
	private static final Logger logger = Logger.getLogger(Command.class);

	private PircBot helen;

	private boolean adminMode = false;
	private final int adminSecurity = 2;
	private int bullets = 6;

	private static HashMap<String, Method> hashableCommandList = new HashMap<String, Method>();
	private static HashMap<String, Method> slowCommands = new HashMap<String, Method>();
	private static HashMap<String, Method> regexCommands = new HashMap<String, Method>();

	public Command() {

	}

	public Command(PircBot ircBot) {
		helen = ircBot;
	}

	static {
		for (Method m : Command.class.getDeclaredMethods()) {
			if (m.isAnnotationPresent(IRCCommand.class)) {
				if (m.getAnnotation(IRCCommand.class).startOfLine() && !m.getAnnotation(IRCCommand.class).reg()) {
					for (String s : ((IRCCommand) m.getAnnotation(IRCCommand.class)).command()) {
						hashableCommandList.put(s.toLowerCase(), m);
					}
				} else if (!m.getAnnotation(IRCCommand.class).reg()) {
					for (String s : ((IRCCommand) m.getAnnotation(IRCCommand.class)).command()) {
						slowCommands.put(s.toLowerCase(), m);
					}
				} else {
					for (String s : ((IRCCommand) m.getAnnotation(IRCCommand.class)).regex()) {
						regexCommands.put(s, m);
					}
				}

				logger.info(((IRCCommand) m.getAnnotation(IRCCommand.class)).command());
			}
		}
		logger.info("Finished Initializing commandList.");
	}

	private void checkTells(CommandData data) {
		ArrayList<Tell> tells = Tells.getTells(data.getSender());

		if (tells.size() > 0) {
			helen.sendNotice(data.getSender(), "You have " + tells.size() + " pending tell(s).");
		}
		for (Tell tell : tells) {
			Tells.clearTells(tell.getTarget());
			helen.sendMessage(tell.getTarget(), tell.toString());

		}
	}

	private Integer getSecurityLevel(User[] userlist, CommandData data) {
		if (data.isWhiteList()) {
			return 4;
		} else if (userlist != null) {

			User user = null;
			for (User u : userlist) {
				if (data.getSender().equalsIgnoreCase(u.getNick())) {
					user = u;
				}
			}

			if (user != null) {
				if(user.isOp()){
					return 3;
				}
				switch (user.getPrefix()) {
				case "~":
				case "&":
					return 3;
				case "%":
					return 2;
				case "":
					return 1;
				}
			}
		}

		return 1;

	}

	private User[] getUserlist(CommandData data) {
		User[] list = null;
		if (!(data.getChannel() == null || data.getChannel().isEmpty())) {
			list = helen.getUsers(data.getChannel());
		}
		return list;
	}

	private boolean jarvisInChannel(User[] userlist) {
		if(userlist != null){
			for (User u : userlist) {
				if (u.getNick().equalsIgnoreCase("jarvis")) {
					return true;
				}
			}
		}
		return false;
	}

	public void dispatchTable(CommandData data) {

		checkTells(data);
		User[] userList = getUserlist(data);
		boolean jarvisInChannel = (data.isPrivate()) ? false :  jarvisInChannel(userList);
		Integer securityLevel = getSecurityLevel(userList, data);
		//logger.info("Entering dispatch table with command: \"" + data.getCommand() + "\"");

		// If we can use hashcommands, do so
		if (hashableCommandList.containsKey(data.getCommand().toLowerCase())) {
			try {

				Method m = hashableCommandList.get(data.getCommand().toLowerCase());
				if (m.getAnnotation(IRCCommand.class).coexistWithJarvis() || !jarvisInChannel) {
					if (securityLevel >= (adminMode
							? Math.max(m.getAnnotation(IRCCommand.class).securityLevel(), adminSecurity)
							: m.getAnnotation(IRCCommand.class).securityLevel())) {
						m.invoke(this, data);
					} else {
						logger.info("User " + data.getSender() + " attempted to use command: "
								+ data.getCommand() + " which is above their security level of: "
								+ securityLevel + (adminMode ? ".  I am currently in admin mode." : "."));
					}
				}

			} catch (Exception e) {
				logger.error("Exception invoking start-of-line command: " + data.getCommand(), e);
			}
			// otherwise, run the command string against all the contains
			// commands
		} else {
			for (String command : slowCommands.keySet()) {
				if (data.getMessage().toLowerCase().contains(command.toLowerCase())) {
					try {
						Method m = slowCommands.get(command);
						if (m.getAnnotation(IRCCommand.class).coexistWithJarvis() || !jarvisInChannel) {
							if (securityLevel >= (adminMode
									? Math.max(m.getAnnotation(IRCCommand.class).securityLevel(), adminSecurity)
									: m.getAnnotation(IRCCommand.class).securityLevel())) {
								m.invoke(this, data);
							} else {
								logger.info("User " + data.getSender() + " attempted to use command: "
										+ data.getCommand() + " which is above their security level of: "
										+ securityLevel + (adminMode ? ".  I am currently in admin mode." : "."));
							}
						}
					} catch (Exception e) {
						logger.error("Exception invoking command: " + command, e);
					}
				}
			}

			// lastly check the string against any regex commands
			for (String regex : regexCommands.keySet()) {
				Pattern r = Pattern.compile(regex);

				if (!(data.getSplitMessage().length > 1)) {
					Matcher match = r.matcher(data.getSplitMessage()[0]);
					if (match.matches()) {
						try {
							Method m = regexCommands.get(regex);
							if (m.getAnnotation(IRCCommand.class).coexistWithJarvis() || !jarvisInChannel) {
								if (securityLevel >= (adminMode
										? Math.max(m.getAnnotation(IRCCommand.class).securityLevel(), adminSecurity)
										: m.getAnnotation(IRCCommand.class).securityLevel())) {
									m.invoke(this, data);
								} else {
									logger.info("User " + data.getSender() + " attempted to use command: "
											+ data.getCommand() + " which is above their security level of: "
											+ securityLevel + (adminMode ? ".  I am currently in admin mode." : "."));

								}
							}
						} catch (Exception e) {
							logger.error("Exception invoking command: "
									+ regexCommands.get(regex).getAnnotation(IRCCommand.class).command(), e);
						}
					}
				}
			}
		}
	}

	// Relateively unregulated commands (anyone can try these)
	@IRCCommand(command = { ".HelenBot" }, startOfLine = true, coexistWithJarvis = true, securityLevel = 1)
	public void versionResponse(CommandData data) {
		if (data.getChannel().isEmpty()) {
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": Greetings, I am HelenBot v"
					+ Configs.getSingleProperty("version").getValue());
		}
		helen.sendMessage(data.getResponseTarget(),
				data.getSender() + ": Greetings, I am HelenBot v" + Configs.getSingleProperty("version").getValue());
	}

	@IRCCommand(command = { ".modeToggle" }, startOfLine = true, coexistWithJarvis = true, securityLevel = 3)
	public void toggleMode(CommandData data) {
		adminMode = !adminMode;
	}

	@IRCCommand(command = { ".ch", ".choose" }, startOfLine = true, securityLevel = 1)
	public void choose(CommandData data) {
		String[] choices = data.getMessage().substring(data.getMessage().indexOf(" ")).split(",");
		helen.sendMessage(data.getResponseTarget(),
				data.getSender() + ": " + choices[((int) (Math.random() * (choices.length - 1)) + 1)]);
	}

	@IRCCommand(command = { ".mode" }, startOfLine = true, coexistWithJarvis = true, securityLevel = 2)
	public void displayMode(CommandData data) {
		helen.sendMessage(data.getResponseTarget(),
				data.getSender() + ": I am currently in " + (adminMode ? "Admin" : "Any User") + " mode.");
	}

	@IRCCommand(command = { ".msg" }, startOfLine = true, securityLevel = 1)
	public void sendMessage(CommandData data) {
		String target = data.getTarget();
		String payload = data.getPayload();
		helen.sendMessage(target, data.getSender() + " said:" + payload);
	}

	@IRCCommand(command = { ".roll" }, startOfLine = true, securityLevel = 1)
	public void roll(CommandData data) {
		Roll roll = new Roll(data.getMessage(), data.getSender());
		Rolls.insertRoll(roll);
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + roll.toString());
	}

	@IRCCommand(command = { ".myRolls", ".myrolls" }, startOfLine = true, securityLevel = 1)
	public void getRolls(CommandData data) {
		ArrayList<Roll> rolls = Rolls.getRolls(data.getSender());
		if (rolls.size() > 0) {
			helen.sendMessage(data.getResponseTarget(), buildResponse(rolls));
		} else {
			helen.sendMessage(data.getResponseTarget(),
					data.getSender() + ": Apologies, I do not have any saved rolls for you at this time.");
		}

	}

	@IRCCommand(command = { ".average", ".avg" }, startOfLine = true, securityLevel = 1)
	public void getAverage(CommandData data) {
		String average = Rolls.getAverage(data.getSplitMessage()[1], data.getSender());
		if (average != null) {
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + average);
		}

	}

	@IRCCommand(command = { ".g", ".google" }, startOfLine = true, securityLevel = 1)
	public void webSearch(CommandData data) {
		try {
			helen.sendMessage(data.getResponseTarget(),
					data.getSender() + ": " + WebSearch.search(data.getMessage()).toString());
		} catch (IOException e) {
			logger.error("Exception during web search", e);
		}

	}

	@IRCCommand(command = { ".y", ".yt", ".youtube" }, startOfLine = true, securityLevel = 1)
	public void youtubeSearch(CommandData data) {
		helen.sendMessage(data.getResponseTarget(),
				data.getSender() + ": " + YouTubeSearch.youtubeSearch(data.getMessage()).toString());

	}
	
	@IRCCommand(command = {".helen",".helenHelp"}, startOfLine = true, securityLevel = 1, coexistWithJarvis = true)
	public void helenHelp(CommandData data){
		help(data);
	}
	
	@IRCCommand(command = ".help", startOfLine = true, securityLevel = 1)
	public void help(CommandData data){
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": http://home.helenbot.com/usage.html");
	}

	@IRCCommand(command = ".seen", startOfLine = true, securityLevel = 1)
	public void seen(CommandData data) {
		helen.sendMessage(data.getResponseTarget(), Users.seen(data));
	}

	@IRCCommand(command = "SCP", startOfLine = true, reg = true, regex = { "(scp|SCP)-([0-9]+)" }, securityLevel = 1)
	public void scpSearch(CommandData data) {
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + Pages.getPageInfo(data.getCommand()));
	}

	@IRCCommand(command = ".tagLoad", startOfLine = true, coexistWithJarvis = true, securityLevel = 4)
	public void updateTags(CommandData data) {
		Pages.getTags();
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": Tags have been updated in my database.");
	}

	@IRCCommand(command = { ".pronouns", ".pronoun" }, startOfLine = true, coexistWithJarvis = true, securityLevel = 1)
	public void getPronouns(CommandData data) {
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + Pronouns.getPronouns(data.getTarget()));
	}

	@IRCCommand(command = ".myPronouns", startOfLine = true, coexistWithJarvis = true, securityLevel = 1)
	public void myPronouns(CommandData data) {
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + Pronouns.getPronouns(data.getSender()));
	}

	@IRCCommand(command = ".setPronouns", startOfLine = true, coexistWithJarvis = true, securityLevel = 1)
	public void setPronouns(CommandData data) {
		String response = Pronouns.insertPronouns(data);
		if (response.contains("banned term")) {
			Tells.sendTell("DrMagnus", "Secretary_Helen",
					"User " + data.getSender() + " attempted to add a banned term:" + response, true);
		}
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + response);
	}

	@IRCCommand(command = ".clearPronouns", startOfLine = true, coexistWithJarvis = true, securityLevel = 1)
	public void clearPronouns(CommandData data) {
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + Pronouns.clearPronouns(data.getSender()));
	}

	@IRCCommand(command = ".removePronouns", startOfLine = true, coexistWithJarvis = true, securityLevel = 2)
	public void removePronouns(CommandData data) {
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + Pronouns.clearPronouns(data.getTarget()));
	}
	
	@IRCCommand(command = {".def",".definition"}, startOfLine = true, coexistWithJarvis = false, securityLevel = 1)
	public void define(CommandData data){
		
	}

	// Authentication Required Commands
	@IRCCommand(command = ".join", startOfLine = true, coexistWithJarvis = true, securityLevel = 3)
	public void enterChannel(CommandData data) {
		helen.joinChannel(data.getTarget());
	}

	@IRCCommand(command = ".leave", startOfLine = true, coexistWithJarvis = true, securityLevel = 3)
	public void leaveChannel(CommandData data) {
		helen.partChannel(data.getTarget());
	}

	@IRCCommand(command = ".tell", startOfLine = true, securityLevel = 1)
	public void tell(CommandData data) {
		String str = Tells.sendTell(data.getTarget(), data.getSender(), data.getTellMessage(),
				(data.getChannel().isEmpty() ? true : false));
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + str);
	}

	@IRCCommand(command = ".exit", startOfLine = true, coexistWithJarvis = true, securityLevel = 4)
	public void exitBot(CommandData data) {
		for (String channel : helen.getChannels()) {
			helen.partChannel(channel, "Stay out of the revolver's sights...");
		}
		try {
			Thread.sleep(5000);
		} catch (Exception e) {

		}
		helen.disconnect();
		System.exit(0);

	}

	@IRCCommand(command = ".allProperties", startOfLine = true, securityLevel = 3)
	public void getAllProperties(CommandData data) {
		ArrayList<Config> properties = Configs.getConfiguredProperties(true);
		helen.sendMessage(data.getResponseTarget(),
				data.getSender() + ": Configured properties: " + buildResponse(properties));
	}

	@IRCCommand(command = ".property", startOfLine = true, securityLevel = 2)
	public void getProperty(CommandData data) {
		ArrayList<Config> properties = Configs.getProperty(data.getTarget());
		helen.sendMessage(data.getResponseTarget(),
				data.getSender() + ": Configured properties: " + buildResponse(properties));
	}

	@IRCCommand(command = ".setProperty", startOfLine = true, securityLevel = 4)
	public void setProperty(CommandData data) {
		String properties = Configs.setProperty(data.getSplitMessage()[1], data.getSplitMessage()[2],
				data.getSplitMessage()[3]);
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + properties);
	}

	@IRCCommand(command = ".updateProperty", startOfLine = true, securityLevel = 4)
	public void updateProperty(CommandData data) {
		String properties = Configs.updateSingle(data.getSplitMessage()[1], data.getSplitMessage()[2],
				data.getSplitMessage()[3]);
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + properties);
	}

	@IRCCommand(command = ".deleteProperty", startOfLine = true, securityLevel = 4)
	public void deleteProperty(CommandData data) {
		String properties = Configs.removeProperty(data.getSplitMessage()[1], data.getSplitMessage()[2]);
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + properties);
	}

	@IRCCommand(command = {".clearCache",".clear"}, startOfLine = true, securityLevel = 4)
	public void clearCache(CommandData data) {
		Queries.clear();
		Configs.clear();
		Pronouns.reload();
	}
	
	@IRCCommand(command = ".shoot", startOfLine = true, securityLevel = 4)
	public void shootUser(CommandData data) {
		if(data.getTarget().equalsIgnoreCase("Secretary_Helen")){
			bullets--;
			helen.sendAction(data.getChannel(), "shoots " + data.getSender());
			if(bullets < 1){
				reload(data);
			}
		}else{
			helen.sendAction(data.getChannel(), "shoots " + data.getTarget());
			bullets--;
			if(bullets < 1){
				reload(data);
			}
			helen.sendMessage(data.getChannel(), "Be careful " + data.getTarget() + ". I still have " + 
			(bullets > 1 ? bullets + " bullets left." : "one in the chamber."));
		}
	}
	
	@IRCCommand(command = ".reload", startOfLine = true, securityLevel = 4)
	public void reload(CommandData data) {
		helen.sendAction(data.getChannel(), "reloads all six barrels.");
		bullets = 6;
	}
	
	@IRCCommand(command = ".unload", startOfLine = true, securityLevel = 4)
	public void unload(CommandData data) {
		if(data.getTarget().equalsIgnoreCase("Secretary_Helen")){
			bullets--;
			helen.sendAction(data.getChannel(), "shoots " + data.getSender());
			if(bullets < 1){
				reload(data);
			}
		}else{
			helen.sendAction(data.getChannel(), "calmly thumbs back the hammer and unleashes"
					+ (bullets == 6 ? " all six barrels on " : " the remaining " + bullets + " chambers on ")
			+ data.getTarget() + ".");
			helen.sendMessage(data.getChannel(), "Stay out of the revolver's sights.");
			reload(data);
		}
	}

	private String buildResponse(ArrayList<? extends DatabaseObject> dbo) {
		StringBuilder str = new StringBuilder();
		str.append("{");
		for (int i = 0; i < dbo.size(); i++) {
			if (i != 0) {
				str.append(dbo.get(i).getDelimiter());
			}
			str.append(dbo.get(i).toString());
		}
		str.append("}");
		return str.toString();
	}
}
