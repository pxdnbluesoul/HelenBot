package com.helen.database.users;

import org.jibble.pircbot.Colors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BanInfo {
    List<String> userNames = new ArrayList<>();
    List<String> hostmasks = new ArrayList<>();
    private boolean isSpecial = false;
    private String reason = "";
    private LocalDateTime duration;
    private String thread;

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDuration(LocalDateTime duration) {
        this.duration = duration;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private String channel;

    public BanInfo(String reason, String duration, String thread, String channel){
        this.thread = thread;
        this.duration = LocalDateTime.from(Bans.timeFormatter.parse(duration));
        this.reason = reason;
        this.channel = channel;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public List<String> getHostmasks() {
        return hostmasks;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getDuration() {
        return duration;
    }

    public void addUsername(String username){
        this.userNames.add(username);
    }

    public void addHostmask(String hostmask){
        this.hostmasks.add(hostmask);
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    @Override
    public String toString() {
        return
                Colors.BOLD + "Nicks:" + Colors.NORMAL + String.join(";",userNames) +
                Colors.BOLD +" reason:"+ Colors.NORMAL + reason + ";" +
                Colors.BOLD +" duration:"+ Colors.NORMAL + duration + ";" +
                Colors.BOLD +" thread:"+ Colors.NORMAL + thread + ";" +
                Colors.BOLD +" channel:"+ Colors.NORMAL + channel + ";";
    }
}
