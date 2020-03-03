package com.helen.bots;

import org.jibble.pircbot.User;

public class TestBot implements BotFramework {


    public TestBot() {
    }


    public void sendOutgoingMessage(String target, String message) {
        System.out.println("Target: " + target + " Message: " + message);
    }

    @Override
    public void sendOutgoingNotice(String target, String message) {
        System.out.println("Target: " + target + " Message: " + message);

    }


    @Override
    public User[] getChannelUsers(String channel) {
        return null;
    }

    public void kickUser(String channel, String sender, String message){

    }

    @Override
    public String[] getConnectedChannels() {
        return new String[0];
    }

    @Override
    public void leaveChannel(String channel, String message) {

    }

    @Override
    public void disconnectFromServer() {

    }

    @Override
    public void sendBotAction(String channel, String message) {
        System.out.println("Target: " + channel + " Message: " + message);

    }

    @Override
    public void joinAChannel(String channel) {

    }


}
