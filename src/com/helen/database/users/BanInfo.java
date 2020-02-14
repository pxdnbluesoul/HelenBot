package com.helen.database.users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BanInfo {
    List<String> userNames = new ArrayList<>();
    List<String> hostmasks = new ArrayList<>();
    private boolean isSpecial = false;
    private String reason = "";
    private LocalDate duration;
    private String thread;

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDuration(LocalDate duration) {
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
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BanInfo(String reason, String duration, String thread, String channel){
        this.thread = thread;
        this.duration = LocalDate.from(formatter.parse(duration));
        this.reason = reason;
        this.channel = channel;
    }



    BanInfo(List<String> userNames, List<String> hostmasks, String reason, LocalDate bdate, boolean isSpecial, String thread, String channel) {
        this.userNames = userNames;
        this.hostmasks = hostmasks;
        this.reason = reason;
        this.duration = bdate;
        this.isSpecial = isSpecial;
        this.channel = channel;
        this.thread = thread;
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

    public LocalDate getDuration() {
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
        return "BanInfo{" +
                "userNames=" + String.join(",",userNames) +
                ", hostmasks=" + String.join(",",hostmasks) +
                ", reason='" + reason + '\'' +
                ", duration=" + duration +
                ", thread='" + thread + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
