package com.helen.bots;

import com.helen.commands.Command;
import com.helen.commands.CommandData;
import com.helen.database.framework.*;
import com.helen.database.users.BanInfo;
import com.helen.database.users.Bans;
import com.helen.database.users.Users;
import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.helen.database.users.Bans.getSuperUserBan;

public class HelenBot extends PircBot {

    private static final Logger logger = Logger.getLogger(HelenBot.class);
    private static Command cmd = null;
    private static Timer timer = new Timer();

    public HelenBot() throws IOException,
            IrcException, InterruptedException {
        Optional<Config> version = Configs.getSingleProperty("version");
        if (version.isPresent()) {
            logger.info("Initializing HelenBot v"
                    + version.get().getValue());
            this.setVerbose(true);
            connect();
            joinChannels();
            Bans.updateBans();
            cmd = new Command(this);
        } else {
            logger.error("Version info is missing, please check configs.");
            System.exit(1);
        }

    }

    private void connect() throws IOException,
            IrcException, InterruptedException {
        Optional<Config> loginName = Configs.getSingleProperty("hostname");
        Optional<Config> botName = Configs.getSingleProperty("bot_name");
        Optional<Config> server = Configs.getSingleProperty("server");
        Optional<Config> pass = Configs.getSingleProperty("pass");
        if (loginName.isPresent() && botName.isPresent() && server.isPresent() && pass.isPresent()) {
            this.setLogin(loginName.get().getValue());
            this.setName(botName.get().getValue());
            try {
                this.connect(server.get().getValue());
            } catch (NickAlreadyInUseException e) {
                this.identify(pass.get().getValue());
            }
            Thread.sleep(1000L);
            this.identify(pass.get().getValue());
            Thread.sleep(2000L);
        }


    }

    private void joinChannels() {

        for (Config channel : Configs.getProperty("autojoin")) {
            this.joinChannel(channel.getValue());
        }
    }

    public void onKick(String channel,
                       String kickerNick,
                       String kickerLogin,
                       String kickerHostname,
                       String recipientNick,
                       String reason){
        Set<String> channels = Configs.getFastConfigs("logChannels");
        if(channels.contains(channel)){
            try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("logMessage"), recipientNick, channel, recipientNick + " was kicked by " + kickerNick + " for: " +reason)){
                if(stmt != null){
                    try {
                        stmt.executeUpdate();
                    }catch(Exception e){
                        logger.error("There wsa an error logging a line",e);
                    }
                }
            }
        }
    }

    public void onSetChannelBan(String channel,
                                String sourceNick,
                                String sourceLogin,
                                String sourceHostname,
                                String hostmask){
        Set<String> channels = Configs.getFastConfigs("logChannels");
        if(channels.contains(channel)){
            try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("logMessage"), sourceNick, channel, sourceNick + " has banned " + hostmask)){
                if(stmt != null){
                    try {
                        stmt.executeUpdate();
                    }catch(Exception e){
                        logger.error("There wsa an error logging a line",e);
                    }
                }
            }
        }
    }

    public void onRemoveChannelBan(String channel,
                                String sourceNick,
                                String sourceLogin,
                                String sourceHostname,
                                String hostmask){
        Set<String> channels = Configs.getFastConfigs("logChannels");
        if(channels.contains(channel)){
            try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("logMessage"), sourceNick, channel, sourceNick + " has unbanned " + hostmask)){
                if(stmt != null){
                    try {
                        stmt.executeUpdate();
                    }catch(Exception e){
                        logger.error("There wsa an error logging a line",e);
                    }
                }
            }
        }
    }

    public void onMessage(String channel, String sender, String login,
                          String hostname, String message) {
        Set<String> channels = Configs.getFastConfigs("logChannels");
        if(channels.contains(channel)){
            try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("logMessage"), sender, channel, message)){
                if(stmt != null){
                    try {
                        stmt.executeUpdate();
                    }catch(Exception e){
                        logger.error("There wsa an error logging a line",e);
                    }
                }
            }
        }

        if (channel != null) {
            Users.insertUser(sender, hostname, message, channel.toLowerCase());
        }
        if (getSuperUserBan(sender, hostname, login)) {
            CommandData d = new CommandData(channel, sender, login, hostname,
                    message);
            sendMessage(d.getResponseTarget(), "I am sorry " + sender + " but users banned in both 17 and 19 are not permitted to use the bot.");
        } else {
            cmd.dispatchTable(new CommandData(channel, sender, login, hostname,
                    message));
        }
    }

    public void onPrivateMessage(String sender, String login, String hostname,
                                 String message) {
        dispatchTable(sender, login, hostname, message);
    }

    private void dispatchTable(String sender, String login, String hostname,
                               String message) {
        cmd.dispatchTable(new CommandData("", sender, login, hostname, message));
    }

    public void onDisconnect() {
        int tries = 0;
        while (!this.isConnected()) {
            try {
                tries++;
                this.connect();
                if (this.isConnected()) {
                    this.joinChannels();
                }
            } catch (Exception e) {
                logger.error(e);
                if (tries > 10) {
                    logger.error("Shutting down HelenBot!");
                    System.exit(1);
                }
                try {
                    Thread.sleep(10000);
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        }
    }


    public void onPart(String channel, String sender, String login,
                       String hostname) {

    }

    public void onQuit(String sourceNick,
                       String sourceLogin,
                       String sourceHostname,
                       String reason) {

    }

    public void onJoin(String channel, String sender, String login,
                       String hostmask) {

        //removing extraneous logging
        //logger.info("JOINED: " + sender + " LOGIN: " + login + " HOSTNAME: " + hostmask + " CHANNEL: " + channel);
        //Testing in separate channel
        try {
            BanInfo info = Bans.getUserBan(sender, hostmask, channel, login);
            if (info != null) { // We have a match in the ban page that is still active.

                //Kick them.
                kick(channel, sender, info.getReason());

                // Look at the hostmask from 05 and determine the appropriate banmask.
                // We do this because without prepending the mask with the appropriate wildcards,
                // pircbot sets an invalid mask.
                if(info.getHostmasks().isEmpty()){
                    ban(channel,sender);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            unBan(channel, sender);
                        }
                    }, 900000);

                }else {
                    if (hostmask.contains("@")) { // We have a username to ban as well as a hostmask.
                        hostmask = "*!" + hostmask;
                    } else { // The more usual listing in 05, just the hostmask.
                        hostmask = "*!*@" + hostmask;
                    }
                    ban(channel, hostmask);
                    String finalHostmask = hostmask;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            unBan(channel, finalHostmask);
                        }
                    }, 900000);
                }
            }
        } catch (Exception e) {
            logger.error("Exception attempting onjoin for " + channel + " , " + sender + " " + login + " " + hostmask, e);
        }
    }

}
