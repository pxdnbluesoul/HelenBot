package com.helen.commands;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jibble.pircbot.PircBot;

import com.helen.database.Config;
import com.helen.database.Configs;
import com.helen.database.DatabaseObject;
import com.helen.database.Pages;
import com.helen.database.Queries;
import com.helen.database.Roll;
import com.helen.database.Rolls;
import com.helen.database.Tell;
import com.helen.database.Tells;
import com.helen.database.Users;
import com.helen.search.WebSearch;
import com.helen.search.WikidotSearch;
import com.helen.search.YouTubeSearch;

public class Command {
	private static final Logger logger = Logger.getLogger(Command.class);

	private PircBot helen;

	private boolean magnusMode = false;

	private static HashMap<String, Method> hashableCommandList = new HashMap<String, Method>();
	private static HashMap<String, Method> slowCommands = new HashMap<String, Method>();

	public Command() {

	}

	public Command(PircBot ircBot) {
		helen = ircBot;
	}

	static {
		for (Method m : Command.class.getDeclaredMethods()) {
			if (m.isAnnotationPresent(IRCCommand.class)) {
				if (m.getAnnotation(IRCCommand.class).startOfLine()) {
					for(String s: ((IRCCommand) m.getAnnotation(IRCCommand.class)).command()){
						hashableCommandList.put(s, m);
					}
				} else {
					for(String s: ((IRCCommand) m.getAnnotation(IRCCommand.class)).command()){
						slowCommands.put(s, m);
					}
				}

				logger.info(((IRCCommand) m.getAnnotation(IRCCommand.class)).command());
			}
		}
		logger.info("Finished Initializing commandList.");
	}

	private void checkTells(CommandData data) {
		ArrayList<Tell> tells = Tells.getTells(data.getSender());
		
		if(tells.size() > 0){
			helen.sendNotice(data.getSender(), "You have " + tells.size() + " pending tell(s).");
		}
		for (Tell tell : tells) {
			Tells.clearTells(tell.getTarget());
			helen.sendMessage(tell.getTarget(), tell.toString());

		}
	}

	public void dispatchTable(CommandData data) {
		/*
		new Thread() {
	        public void run() {
	                     //do something here....
	        }
	    }.start();
		 */
		
		
		checkTells(data);

		logger.info("Entering dispatch table with command: \"" + data.getCommand() + "\"");
		if (hashableCommandList.containsKey(data.getCommand())) {
			try {
				hashableCommandList.get(data.getCommand()).invoke(this, data);
			} catch (Exception e) {
				logger.error("Exception invoking start-of-line command: " + data.getCommand(), e);
			}
		} else {
			for (String command : slowCommands.keySet()) {
				if (data.getMessage().contains(command)) {
					try {
						slowCommands.get(command).invoke(this, data);
					} catch (Exception e) {
						logger.error("Exception invoking command: " + command, e);
					}
				}
			}

		}
	}

	// Relateively unregulated commands (anyone can try these)
	@IRCCommand(command = {".HelenBot"}, startOfLine = false)
	public void versionResponse(CommandData data) {
		if (data.getChannel().isEmpty()) {
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": Greetings, I am HelenBot v"
					+ Configs.getSingleProperty("version").getValue());
		}
		helen.sendMessage(data.getChannel(),
				data.getSender() + ": Greetings, I am HelenBot v" + Configs.getSingleProperty("version").getValue());
	}

	@IRCCommand(command = {".modeToggle"}, startOfLine = true)
	public void toggleMode(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			magnusMode = !magnusMode;
		}
	}
	
	@IRCCommand(command = {".ch",".choose"}, startOfLine = true)
	public void choose(CommandData data){
		if(data.isAuthenticatedUser(magnusMode, true)){
			String[] choices = data.getMessage().substring(data.getMessage().indexOf(" ")).split(",");
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " 
						+ choices[((int) (Math.random() * (choices.length - 1)) + 1)]);
		}
	}

	@IRCCommand(command = {".mode"}, startOfLine = true)
	public void displayMode(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, false)) {
			helen.sendMessage(data.getResponseTarget(),
					data.getSender() + ": I am currently in " + (magnusMode ? "Magnus Only" : " Any User") + " mode.");
		}
	}

	@IRCCommand(command = {".msg"}, startOfLine = true)
	public void sendMessage(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, false)) {
			String target = data.getTarget();
			String payload = data.getPayload();

			helen.sendMessage(target, data.getSender() + " said:" + payload);

		}
	}

	@IRCCommand(command = {".roll"}, startOfLine = true)
	public void roll(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			Roll roll = new Roll(data.getMessage(), data.getSender());
			Rolls.insertRoll(roll);
			helen.sendMessage(data.getChannel(), data.getSender() + ": " + roll.toString());
		}
	}

	@IRCCommand(command = {".myRolls", ".myrolls"}, startOfLine = true)
	public void getRolls(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			ArrayList<Roll> rolls = Rolls.getRolls(data.getSender());
			if (rolls.size() > 0) {
				helen.sendMessage(data.getResponseTarget(), buildResponse(rolls));
			} else {
				helen.sendMessage(data.getResponseTarget(),
						data.getSender() + ": Apologies, I do not have any saved rolls for you at this time.");
			}

		}
	}
	
	@IRCCommand(command = {".average",".avg"}, startOfLine = true)
	public void getAverage(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			String average = Rolls.getAverage(data.getSplitMessage()[1], data.getSender());
			if(average != null){
				helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " +  average);
			}

		}
	}

	@IRCCommand(command = {".g",".google"}, startOfLine = true)
	public void webSearch(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			try {
				helen.sendMessage(data.getResponseTarget(),
						data.getSender() + ": " + WebSearch.search(data.getMessage()).toString());
			} catch (IOException e) {
				logger.error("Exception during web search", e);
			}
		}

	}

	@IRCCommand(command = {".y",".yt",".youtube"}, startOfLine = true)
	public void youtubeSearch(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			helen.sendMessage(data.getResponseTarget(),
					data.getSender() + ": " + YouTubeSearch.youtubeSearch(data.getMessage()).toString());
		}

	}

	@IRCCommand(command = ".seen", startOfLine = true)
	public void seen(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			helen.sendMessage(data.getResponseTarget(), Users.seen(data));
		}
	}

	// Authentication Required Commands
	@IRCCommand(command = ".join", startOfLine = true)
	public void enterChannel(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true))
			helen.joinChannel(data.getTarget());

	}

	@IRCCommand(command = ".leave", startOfLine = true)
	public void leaveChannel(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true))
			helen.partChannel(data.getTarget());

	}

	@IRCCommand(command = ".tell", startOfLine = true)
	public void tell(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			String str = Tells.sendTell(data.getTarget(), data.getSender(), data.getTellMessage(),
					(data.getChannel().isEmpty() ? true : false));
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + str);
		}
	}

	@IRCCommand(command = ".exit", startOfLine = true)
	public void exitBot(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, true)) {
			for(String channel : helen.getChannels()){
				helen.partChannel(channel,"Stay out of the revolver's sights...");
			}
			try{
				Thread.sleep(5000);
			}catch(Exception e){
				
			}
			helen.disconnect();
			System.exit(0);

		}
	}

	@IRCCommand(command = ".allProperties", startOfLine = true)
	public void getAllProperties(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, false)) {
			ArrayList<Config> properties = Configs.getConfiguredProperties(true);
			helen.sendMessage(data.getResponseTarget(),
					data.getSender() + ": Configured properties: " + buildResponse(properties));
		}
	}

	@IRCCommand(command = ".property", startOfLine = true)
	public void getProperty(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, false)) {
			ArrayList<Config> properties = Configs.getProperty(data.getTarget());
			helen.sendMessage(data.getResponseTarget(),
					data.getSender() + ": Configured properties: " + buildResponse(properties));
		}
	}

	@IRCCommand(command = ".setProperty", startOfLine = true)
	public void setProperty(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, false)) {
			String properties = Configs.setProperty(data.getSplitMessage()[1], data.getSplitMessage()[2],
					data.getSplitMessage()[3]);
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + properties);
		}
	}

	@IRCCommand(command = ".updateProperty", startOfLine = true)
	public void updateProperty(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, false)) {
			String properties = Configs.updateSingle(data.getSplitMessage()[1], data.getSplitMessage()[2],
					data.getSplitMessage()[3]);
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + properties);
		}
	}

	@IRCCommand(command = ".deleteProperty", startOfLine = true)
	public void deleteProperty(CommandData data) {
		if (data.isAuthenticatedUser(magnusMode, false)) {
			String properties = Configs.removeProperty(data.getSplitMessage()[1], data.getSplitMessage()[2]);
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + properties);
		}
	}
	
	@IRCCommand(command = ".clearCache", startOfLine = true)
	public void clearCache(CommandData data){
		if(data.isAuthenticatedUser(magnusMode, false)){
			Queries.clear();
			Configs.clear();
		}
	}
	@IRCCommand(command = ".searchTest", startOfLine = true)
	public void search(CommandData data){
		if(data.isAuthenticatedUser(magnusMode, false)){
			Pages.uploadSeries();
		}
	}

	private String buildResponse(ArrayList<? extends DatabaseObject> dbo) {
		StringBuilder str = new StringBuilder();
		str.append("{");
		for(int i = 0; i < dbo.size(); i++){
			if(i != 0){
				str.append(dbo.get(i).getDelimiter());
			}
			str.append(dbo.get(i).toString());
		}
		str.append("}");
		return str.toString();
	}
}
