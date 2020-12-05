package com.helen.bots;

import org.jibble.pircbot.User;

public interface BotFramework {

    void sendOutgoingMessage(String target,String message);

    void sendOutgoingNotice(String target, String message);

    User[] getChannelUsers(String channel);

    void kickUser(String channel, String sender, String message);

    String[] getConnectedChannels();

    void leaveChannel(String channel, String message);

    void disconnectFromServer();

    void sendBotAction(String channel, String message);

    void joinAChannel(String channel);
}
