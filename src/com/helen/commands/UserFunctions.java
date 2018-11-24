package com.helen.commands;

import com.helen.commandframework.CommandClass;
import com.helen.commandframework.CommandData;
import com.helen.commandframework.CommandResponse;
import com.helen.commandframework.IRCCommand;
import com.helen.database.*;
import com.helen.database.entities.Config;

class UserFunctions implements CommandClass {

    @IRCCommand(command = {".addNick"}, arguments = ".addNick [nickname to group with]",
            description = "Adds a nick to your group.  If your nick doesn't exist in a group, it'll create one.",coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse addNick(CommandData data){
        return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + Nicks.addNick(data));
    }

    @IRCCommand(command = {".deleteNick"}, arguments = ".deleteNick [nickname to delete from your nick group]", description = "Removes a nick from your group.",coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse deleteNick(CommandData data){
        return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + Nicks.deleteNick(data));
    }

    @IRCCommand(command = {".deleteAllNicks"}, description = "Deletes all of your nicks.", coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse deleteAllNicks(CommandData data){
        return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + Nicks.deleteAllNicks(data, true));
    }

    @IRCCommand(command = {".deleteNicksAdmin"}, coexistWithJarvis = true, securityLevel = 4)
    public CommandResponse deleteNicksAdmin(CommandData data){
        return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + Nicks.deleteAllNicks(data, false));
    }

    @IRCCommand(command = ".seen", securityLevel = 1)
    public CommandResponse seen(CommandData data) {
        return new CommandResponse( Users.seen(data));
    }

    @IRCCommand(command = {".pronouns", ".pronoun"}, coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse getPronouns(CommandData data) {
        return new CommandResponse( data.getSender() + ": " +
                Pronouns.getPronouns((data.getCommandAsParameters().length > 1) ? data.getTarget() : data.getSender()));

    }

    @IRCCommand(command = ".myPronouns", coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse myPronouns(CommandData data) {
        return new CommandResponse( data.getSender() + ": " + Pronouns.getPronouns(data.getSender()));
    }


    @IRCCommand(command = ".setPronouns", coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse setPronouns(CommandData data) {
        String response = Pronouns.insertPronouns(data);
        if (response.contains("banned term")) {
            for(Config c : Configs.getProperty("registeredNicks")){
                Tells.sendTell(c.getValue(), "Secretary_Helen",
                        "User " + data.getSender() + " attempted to add a banned term:" + response +". Their full message "
                                + "was: " + data.getMessage(), true);

            }
        }
        return new CommandResponse( data.getSender() + ": " + response);
    }

    @IRCCommand(command = ".clearPronouns", coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse clearPronouns(CommandData data) {
        return new CommandResponse( data.getSender() + ": " + Pronouns.clearPronouns(data.getSender()));
    }

    @IRCCommand(command = ".deletePronouns", coexistWithJarvis = true, securityLevel = 2)
    public CommandResponse removePronouns(CommandData data) {
        if(data.getTarget() != null){
            return new CommandResponse( data.getSender() + ": " + Pronouns.clearPronouns(data.getTarget()));
        }
        else{
            return new CommandResponse( data.getSender() + ": Please specify the user to delete pronouns for.");
        }

    }

    @IRCCommand(command = ".tell", securityLevel = 1)
    public CommandResponse tell(CommandData data) {
        String str = Tells.sendTell(data.getTarget(), data.getSender(), data.getTellMessage(),
                (data.getChannel().isEmpty()));
        return new CommandResponse( data.getSender() + ": " + str);
    }

    @IRCCommand(command = ".mtell", securityLevel = 1,coexistWithJarvis = true)
    public CommandResponse multiTell(CommandData data) {
        String str = Tells.sendMultitell(data);
        return new CommandResponse( data.getSender() + ": " + str);
    }
}
