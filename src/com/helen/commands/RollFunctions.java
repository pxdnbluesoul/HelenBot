package com.helen.commands;

import com.helen.commandframework.CommandClass;
import com.helen.commandframework.CommandData;
import com.helen.commandframework.CommandResponse;
import com.helen.commandframework.IRCCommand;
import com.helen.database.entities.Roll;
import com.helen.database.Rolls;
import com.helen.util.Utils;

import java.util.ArrayList;

class RollFunctions implements CommandClass {

    @IRCCommand(command = { "rollTest" }, securityLevel = 1, reg = true,
            regex = {"([0-9]+)(d|f)([0-9]+)([+|-]?[0-9]+)?(\\s-e|-s)?\\s?(-e|-s)?\\s?(.+)?"})
    public CommandResponse regexRoll(CommandData data) {
        Roll roll = new Roll(".roll " + data.getMessage(),
                data.getSender(),
                "([0-9]+)(d|f)([0-9]+)([+|-]?[0-9]+)?(\\s-e|-s)?\\s?(-e|-s)?\\s?(.+)?");
        Rolls.insertRoll(roll);
        return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + roll.toString());
    }
    @IRCCommand(command = { ".roll" }, securityLevel = 1)
    public CommandResponse roll(CommandData data) {
        Roll roll = new Roll(data.getMessage(), data.getSender());
        Rolls.insertRoll(roll);
        return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + roll.toString());
    }

    @IRCCommand(command = { ".myRolls", ".myrolls" }, securityLevel = 1)
    public CommandResponse getRolls(CommandData data) {
        ArrayList<Roll> rolls = Rolls.getRolls(data.getSender());
        if (rolls.size() > 0) {
            return new CommandResponse(data.getResponseTarget(), Utils.buildResponse(rolls));
        } else {
            return new CommandResponse(data.getResponseTarget(),
                    data.getSender() + ": *Checks her clipboard* Apologies, I do not have any saved rolls for you at this time.");
        }
    }

    @IRCCommand(command = { ".average", ".avg" }, securityLevel = 1)
    public CommandResponse getAverage(CommandData data) {
        String average = Rolls.getAverage(data.getCommandAsParameters()[1], data.getSender());
        return new CommandResponse((average != null ? data.getSender() + ": " + average : ""));

    }
}
