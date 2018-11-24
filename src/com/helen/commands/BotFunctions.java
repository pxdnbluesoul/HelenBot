package com.helen.commands;

import com.helen.commandframework.*;
import com.helen.database.entities.Config;
import com.helen.database.Configs;
import com.helen.database.Pronouns;
import com.helen.database.framework.Queries;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.helen.commandframework.CommandRegistry.getClasses;
import static com.helen.util.Utils.buildResponse;

class BotFunctions implements CommandClass {


    @IRCCommand(command = ".help", securityLevel = 0, description = "Get the list of commands for a given command class.", arguments = "[Name of the command class]")
    public CommandResponse help(CommandData data){
        CommandClass commandClass = CommandRegistry.getInstance(data.getCommandAsParameters()[1]);
        StringBuilder builder = new StringBuilder();
        Arrays.stream(commandClass.getClass().getDeclaredMethods())
                .forEach(method -> {
                    if(method.isAnnotationPresent(IRCCommand.class)){
                        IRCCommand command = method.getAnnotation(IRCCommand.class);
                        builder.append(StringUtils.join(command.command(),", "));
                        builder.append("; ");
                        builder.append("arguments: ");
                        builder.append(StringUtils.join(command.arguments(),","));
                        if(!command.description().isEmpty()) {
                            builder.append("; Description: ");
                            builder.append(command.description());
                        }
                    }
                    builder.append("\n");

                });
        return new CommandResponse(builder.toString());
    }

    @IRCCommand(command = ".commands", securityLevel = 0, description = "Gets all command types.")
    public String getCommandClasses(CommandData data){
        return StringUtils.join(getClasses(),"; ");
    }

    @IRCCommand(command = { ".HelenBot" }, coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse versionResponse(CommandData data) {
        if (data.getChannel().isEmpty()) {
            return new CommandResponse(data.getSender() + ": Greetings, I am HelenBot v" + Objects.requireNonNull(Configs.getSingleProperty("version")).getValue(), "Version couldn't be retrieved");
        }
       return new CommandResponse(data.getSender() + ": Greetings, I am HelenBot v" + Objects.requireNonNull(Configs.getSingleProperty("version")).getValue());
    }

    @IRCCommand(command = { ".modeToggle" }, coexistWithJarvis = true, securityLevel = 3)
    public CommandResponse toggleMode(CommandData data) {
        return new CommandResponse(data.getSender() + ": " + " Mode toggled.", "toggleMode");
    }

    @IRCCommand(command = ".exit", coexistWithJarvis = true, securityLevel = 4)
    public CommandResponse exitBot(CommandData data) {
        return new CommandResponse("", "exit");
    }

    @IRCCommand(command = { ".checkJarvis" }, coexistWithJarvis = true, securityLevel = 2)
    public CommandResponse findJarvisInChannel(CommandData data) {
        return new CommandResponse(data.getSender() + ": Checking channel members...","checkJarvis");
    }

    @IRCCommand(command = { ".mode" }, coexistWithJarvis = true, securityLevel = 2)
    public CommandResponse displayMode(CommandData data) {
        return new CommandResponse("","checkMode");
    }

    @IRCCommand(command = ".allProperties", securityLevel = 3)
    public CommandResponse getAllProperties(CommandData data) {
        ArrayList<Config> properties = Configs.getConfiguredProperties(true);
        return new CommandResponse(
                data.getSender() + ": Configured properties: " + buildResponse(properties));
    }

    @IRCCommand(command = ".property", coexistWithJarvis = true, securityLevel = 2)
    public CommandResponse getProperty(CommandData data) {
        ArrayList<Config> properties = Configs.getProperty(data.getTarget());
        return new CommandResponse(
                data.getSender() + ": Configured properties: " + buildResponse(properties));
    }

    @IRCCommand(command = ".setProperty", coexistWithJarvis = true, securityLevel = 4)
    public CommandResponse setProperty(CommandData data) {
        String properties = Configs.setProperty(data.getCommandAsParameters()[1], data.getCommandAsParameters()[2],
                data.getCommandAsParameters()[3]);
        return new CommandResponse( data.getSender() + ": " + properties);
    }

    @IRCCommand(command = ".updateProperty", coexistWithJarvis = true, securityLevel = 4)
    public CommandResponse updateProperty(CommandData data) {
        String properties = Configs.updateSingle(data.getCommandAsParameters()[1], data.getCommandAsParameters()[2],
                data.getCommandAsParameters()[3]);
        return new CommandResponse( data.getSender() + ": " + properties);
    }

    @IRCCommand(command = ".deleteProperty", securityLevel = 4)
    public CommandResponse deleteProperty(CommandData data) {
        String properties = Configs.removeProperty(data.getCommandAsParameters()[1], data.getCommandAsParameters()[2]);
        return new CommandResponse( data.getSender() + ": " + properties);
    }

    @IRCCommand(command = {".clearCache",".clear"}, securityLevel = 4)
    public CommandResponse clearCache(CommandData data) {
        Queries.clear();
        Configs.clear();
        Pronouns.reload();
        return new CommandResponse(data.getSender() + ": caches cleared!");
    }

}
