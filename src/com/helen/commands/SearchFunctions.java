package com.helen.commands;

import com.helen.commandframework.CommandClass;
import com.helen.commandframework.CommandData;
import com.helen.commandframework.CommandResponse;
import com.helen.commandframework.IRCCommand;
import com.helen.search.WebSearch;
import com.helen.search.YouTubeSearch;
import org.apache.log4j.Logger;

import java.io.IOException;

class SearchFunctions implements CommandClass {

    private static final Logger logger = Logger.getLogger(SearchFunctions.class);

    @IRCCommand(command = { ".g", ".google" }, securityLevel = 1)
    public CommandResponse webSearch(CommandData data) {
        try {
            return new CommandResponse(data.getSender() + ": " + WebSearch.search(data.getMessage()).toString());
        } catch (IOException e) {
            logger.error("Exception during web search", e);
        }
        return new CommandResponse();
    }

    @IRCCommand(command = { ".y", ".yt", ".youtube" }, securityLevel = 1)
    public CommandResponse youtubeSearch(CommandData data) {
        return new CommandResponse(
                data.getSender() + ": " + YouTubeSearch.youtubeSearch(data.getMessage()));

    }

    @IRCCommand(command = {".helen",".helenHelp"}, securityLevel = 1, coexistWithJarvis = true)
    public CommandResponse helenHelp(CommandData data){
        return help(data);
    }

    @IRCCommand(command = ".help", securityLevel = 1)
    private CommandResponse help(CommandData data){
        return new CommandResponse( data.getSender() + ": You can find a list of my job responsibilities here:  http://home.helenbot.com/usage.html");
    }
}
