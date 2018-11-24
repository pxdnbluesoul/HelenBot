package com.helen.commands;

import com.helen.commandframework.CommandClass;
import com.helen.commandframework.CommandData;
import com.helen.commandframework.CommandResponse;
import com.helen.commandframework.IRCCommand;
import com.helen.database.Pages;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class WikiFunctions implements CommandClass {

    @IRCCommand(command = ".sm", securityLevel = 1)
    public CommandResponse selectResult(CommandData data){
        return new CommandResponse( data.getSender() + ": " + Pages.getStoredInfo(data.getTarget(), data.getSender()));
    }

    @IRCCommand(command = {".lc",".l"}, securityLevel = 1)
    public CommandResponse lastCreated(CommandData data){
        ArrayList<String> pages = Pages.lastCreated();
        ArrayList<String> infoz = new ArrayList<>();
        if (pages != null) {
            return new CommandResponse( StringUtils.join(pages.stream().map(Pages::getPageInfo)
                    .collect(Collectors.toSet()),"\r\n"));
        } else {
            return new CommandResponse( data.getSender() + ": I can't do that yet.");
        }
    }

    @IRCCommand(command = {".hlc", ".hl"}, coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse lastCreatedHelen(CommandData data) {
        ArrayList<String> pages = Pages.lastCreated();
        ArrayList<String> infoz = new ArrayList<>();
        if (pages != null) {
            return new CommandResponse( StringUtils.join(pages.stream().map(Pages::getPageInfo)
                        .collect(Collectors.toSet()),"\r\n"));
        } else {
            return new CommandResponse( data.getSender() + ": I can't do that yet.");
        }
    }

    @IRCCommand(command = ".au", securityLevel = 1)
    public CommandResponse authorDetail(CommandData data){
        return new CommandResponse( data.getSender() + ": " + Pages.getAuthorDetail(
                data, data.getCommandAsParameters().length == 1 ? data.getSender() :  data.getMessageWithoutCommand()));
    }

    @IRCCommand(command = "SCPPAGEREGEX", reg = true, regex = { "http:\\/\\/www.scp-wiki.net\\/(.*)" }, securityLevel = 1, matcherGroup = 1)
    public CommandResponse getPageInfo(CommandData data){
        if(!data.getRegexTarget().contains("/") && !data.getRegexTarget().contains("forum")){
            return new CommandResponse( data.getSender() + ": " + Pages.getPageInfo(data.getRegexTarget()));
        }else{
            return new CommandResponse();
        }
    }

    @IRCCommand(command = "SCP", reg = true, regex = { "(scp|SCP)-([0-9]+)(-(ex|EX|j|J|arc|ARC))?" }, securityLevel = 1)
    public CommandResponse scpSearch(CommandData data) {
        return new CommandResponse( data.getSender() + ": " + Pages.getPageInfo(data.getCommand()));
    }

    @IRCCommand(command = {".s",".sea"}, securityLevel = 1)
    public CommandResponse findSkip(CommandData data){
        return new CommandResponse( data.getSender() + ": " + Pages.getPotentialTargets(data.getCommandAsParameters(), data.getSender()));
    }

    private final Pattern p = Pattern.compile("(scp|SCP)-([0-9]+)(-(ex|EX|j|J|arc|ARC))?");
    @IRCCommand(command = {".hs", ".hsea"}, coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse findSkipHelen(CommandData data) {
        if(p.matcher(data.getTarget()).matches()){
            return new CommandResponse( data.getSender() + ": " + Pages.getPageInfo(data.getTarget()));
        }else {
            return new CommandResponse( data.getSender() + ": " + Pages.getPotentialTargets(data.getCommandAsParameters(), data.getSender()));
        }
    }
}
