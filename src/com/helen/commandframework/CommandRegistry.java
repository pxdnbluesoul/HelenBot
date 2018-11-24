package com.helen.commandframework;

import com.helen.bots.HelenBot;
import com.helen.database.*;
import com.helen.database.entities.Tell;
import com.helen.search.WebsterSearch;
import com.helen.util.Pair;
import org.apache.log4j.Logger;
import org.jibble.pircbot.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandRegistry {
	private static final Logger logger = Logger.getLogger(CommandRegistry.class);

	private final HelenBot helen;


	private final ConcurrentHashMap<String, Method> commands = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Method> slowCommands = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Pattern, Method> regexCommands = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Method, CommandClass> commandMethodInstances = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, CommandClass> commandInstances = new ConcurrentHashMap<>();

	private boolean adminMode = false;

	private static HashMap<String, Method> hashableCommandList = new HashMap<>();
	//private static HashMap<String, Method> slowCommands = new HashMap<String, Method>();
	//private static HashMap<String, Method> regexCommands = new HashMap<String, Method>();

	public CommandRegistry(HelenBot ircBot) {
		helen = ircBot;
		getAnnotatedClasses();
	}

	private void internalCommand(String command, CommandData data){
		switch(command){
			case "modeToggle":
				this.adminMode = !this.adminMode;
				break;
			case "mode" :
				sendMessage(data.getSender() + ": I am currently in " + (adminMode ? "Admin" : "Any User") + " mode.", data);
			case "exit":
				exit();
				break;
			case "checkJarvis":
				helen.jarvisReset(data.getChannel());
				helen.sendWho(data.getChannel());
			break;
			default:
				break;
		}
	}

	private void exit(){
		for (String channel : helen.getChannels()) {
			helen.partChannel(channel, "Stay out of the revolver's sights...");
		}
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			logger.error("Exception!");
		}
		helen.disconnect();
		System.exit(0);
	}

	public static CommandClass getInstance(String className) {
		return commandInstances.get(className);
	}

	public static Set<String> getClasses() {
		return commandInstances.keySet();
	}

	private boolean securityCheck(Method m, int securityLevel, CommandData commandData){
		IRCCommand command = m.getAnnotation(IRCCommand.class);
		if(command.coexistWithJarvis() || !helen.jarvisCheck(commandData.getChannel())) {
			int adminSecurity = 2;
			return securityLevel >= (adminMode
					? Math.max(command.securityLevel(), adminSecurity)
					: command.securityLevel());
		}
		logger.info("User " + commandData.getSender() + " attempted to use command: "
				+ commandData.getCommand() + " which is above their security level of: "
				+ securityLevel + (adminMode ? ".  I am currently in admin mode." : "."));
		return false;
	}

	private Set<Pair<CommandClass, Method>> validCommands(CommandData input, int securityLevel) {

		if(commands.keySet().contains(input.getCommand())){
			Method m = commands.get(input.getCommand());
			if (securityCheck(m,securityLevel, input)) {
				return new HashSet<>(Collections.singletonList(Pair.of(commandMethodInstances.get(m), m)));
			}
		}

		for(String slowCommand : slowCommands.keySet()){
			if(input.getMessage().contains(slowCommand)){
				Method m = slowCommands.get(slowCommand);
				if(securityCheck(m,securityLevel,input)) {
					return new HashSet<>(Collections.singletonList(Pair.of(commandMethodInstances.get(m), m)));
				}
			}
		}

		return regexCommands.keySet().stream()
				.filter(pattern -> pattern.matcher(input.getMessage()).matches())
				.map(regexCommands::get)
				.filter(m -> securityCheck(m, securityLevel,input))
				.map(method -> Pair.of(commandMethodInstances.get(method),method))
				.collect(Collectors.toSet());
	}

	public void handleCommand(CommandData commandData){
		checkTells(commandData);
		User[] userList = getUserlist(commandData);
		Integer securityLevel = getSecurityLevel(userList, commandData);
		validCommands(commandData, securityLevel)
				.forEach(methodClassPair -> {
					try {
						CommandResponse result = (CommandResponse)methodClassPair.getSecond().invoke(methodClassPair.getFirst(), commandData);
						if(!result.getMessage().isEmpty()) {
							if(result.getType().equals(CommandResponse.ResponseType.Message)) {
								sendMessage(result.getMessage(), commandData);
							}else{
								sendAction(result.getMessage(),commandData);
							}
						}
						result.getCommands().forEach(command -> internalCommand(command, commandData));
					}catch(Exception e){
						if(e instanceof MalformedCommandException){
							sendMessage(e.getMessage(),commandData);
						}else{
						logger.error("There was an exception attempting to invoke command: "
								+ methodClassPair.getSecond().getName(),e);
						}
					}
				});
	}

	private void getAnnotatedClasses() {
		try {
			getClasses("com.helen.commands")
					.forEach(classType -> {
						for (Method m : classType.getDeclaredMethods()) {
							if (m.isAnnotationPresent(IRCCommand.class)) {
								try {
									IRCCommand command = m.getAnnotation(IRCCommand.class);
									if(command.reg()){
										Arrays.stream(command.command()).forEach(str -> regexCommands.put(Pattern.compile(str), m));
									}else if(!command.startOfLine()) {
										Arrays.stream(command.command()).forEach(commString ->slowCommands.put(commString, m));
									}else {
										Arrays.stream(command.command()).forEach(commString ->commands.put(commString, m));
									}
									Constructor<CommandClass> ctor = classType.getConstructor();
									CommandClass cClass = ctor.newInstance();
									commandMethodInstances.put(m, cClass);
									commandInstances.putIfAbsent(cClass.getClass().getSimpleName(), cClass);
								} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
					});
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Class> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			try {
				dirs.add(Paths.get(resource.toURI()).toFile());
			}catch(Exception e){
				logger.error("Exception getting files");
			}
		}
		ArrayList<Class> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}


	private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	private void checkTells(CommandData data) {
		ArrayList<Tell> tells = Tells.getTells(data.getSender());

		if (tells.size() > 0) {
			helen.sendNotice(data.getSender(), "You have " + tells.size() + " pending tell(s).");
		}
		for (Tell tell : tells) {
			Tells.clearTells(tell.getNickGroupId() != null ? tell.getNickGroupId().toString() : tell.getTarget());
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
	private void sendMessage(String message, CommandData data) {
		helen.sendMessage(data.getTarget(), message);
	}

	private void sendAction(String message, CommandData data) {
		helen.sendAction(data.getTarget(), message);
	}

	@IRCCommand(command = { ".jarvistest" }, coexistWithJarvis = true, securityLevel = 4)
	public void listTest(CommandData data) {
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + helen.jarvisCheck(data.getTarget()));
	}





	//TODO make this less stupid
	@IRCCommand(command = ".helenconf", coexistWithJarvis = true, securityLevel = 4)
	public void configure(CommandData data) {
		if(data.getCommandAsParameters().length == 1){
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + "{shoot|lcratings}");
		}else{
			helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + Configs.insertToggle(data, data.getTarget(),
					data.getCommandAsParameters()[2].equalsIgnoreCase("true")));
		}
	}



	



	
	@IRCCommand(command = {".def",".definition"}, securityLevel = 1)
	public void define(CommandData data){
		helen.sendMessage(data.getResponseTarget(), data.getSender() + ": " + WebsterSearch.dictionarySearch(data.getTarget()));
	}

	// Authentication Required Commands
	@IRCCommand(command = ".join", coexistWithJarvis = true, securityLevel = 3)
	public void enterChannel(CommandData data) {
		helen.joinJarvyChannel(data.getTarget());

	}

	@IRCCommand(command = ".leave", coexistWithJarvis = true, securityLevel = 3)
	public void leaveChannel(CommandData data) {
		helen.partChannel(data.getTarget());
	}









}
