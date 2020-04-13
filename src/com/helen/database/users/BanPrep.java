package com.helen.database.users;

import com.helen.commands.CommandData;
import org.apache.commons.lang.StringUtils;
import org.jibble.pircbot.Colors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BanPrep {

    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd;HH:mm:ss");
    private List<String> users = new ArrayList<>();
    private List<String> hostmasks = new ArrayList<>();
    private Optional<String> time = Optional.empty();
    private Optional<String> duration = Optional.empty();
    private Set<String> flagSet = new HashSet<>();
    private LocalDateTime endTime = null;
    private String response = "";
    private String reason;
    private String thread;
    private Integer banid;
    private String channel;
    public BanPrep(CommandData d) {

        String[] tokens = Arrays.stream(d.getMessageWithoutCommand().split("\\|")).map(String::trim).toArray(String[]::new);
        try {

            for (String flags : tokens) {
                String[] parts = flags.split(" ", 2);
                if (!parts[0].startsWith("-")) {
                    throw new RuntimeException();
                }

                switch (parts[0]) {
                    case "-i":
                        flagSet.add("i");
                        banid = Integer.parseInt(parts[1]);
                        break;
                    case "-o":
                        flagSet.add("o");
                        thread = parts[1].trim();
                        break;
                    case "-c":
                        flagSet.add("c");
                        channel = parts[1].trim();
                        break;
                    case "-r":
                        flagSet.add("r");
                        reason = parts[1].trim();
                        break;
                    case "-u":
                        flagSet.add("u");
                        Arrays.stream(parts[1].split(",")).forEach(s -> users.add(s.trim()));
                        break;
                    case "-h":
                        flagSet.add("h");
                        Arrays.stream(parts[1].split(",")).forEach(s -> hostmasks.add(s.trim()));
                        break;
                    case "-t":
                        flagSet.add("t");
                        if (duration.isPresent()) {
                            throw new RuntimeException();
                        } else {
                            endTime = LocalDateTime.from(timeFormatter.parse(parts[1].trim()));
                        }
                        break;
                    case "-d":
                        flagSet.add("d");
                        if (time.isPresent()) {
                            throw new RuntimeException();
                        } else {
                            endTime = LocalDateTime.now();
                            if (parts[1].length() > 2) {
                                throw new RuntimeException();
                            } else {
                                switch (parts[1].substring(1).toLowerCase()) {
                                    case "d":
                                        endTime = endTime.plusDays(Integer.parseInt(parts[1].substring(0, 1)));
                                        break;
                                    case "w":
                                        endTime = endTime.plusWeeks(Integer.parseInt(parts[1].substring(0, 1)));
                                        break;
                                    case "m":
                                        endTime = endTime.plusMonths(Integer.parseInt(parts[1].substring(0,1)));
                                    case "y":
                                        endTime = endTime.plusYears(Integer.parseInt(parts[1].substring(0, 1)));
                                        break;
                                    case "":
                                        if(parts[1].equalsIgnoreCase("p")) {
                                            endTime = LocalDateTime.of(2999, 12, 31,0,0,0);
                                        }
                                        break;

                                }
                            }
                        }
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
                StringBuilder str = new StringBuilder();
                if (!users.isEmpty() || !hostmasks.isEmpty()) {
                    str.append("You are banning ");
                }
                if (!users.isEmpty()) {
                    str.append("usernames: ").append(Colors.BOLD).append(String.join(",", users)).append(Colors.NORMAL).append("; ");
                }
                if (!hostmasks.isEmpty()) {
                    str.append("hostmasks: ").append(Colors.BOLD).append(String.join(",", hostmasks)).append(Colors.NORMAL).append("; ");
                }
                if(!StringUtils.isEmpty(channel)) {
                    str.append("from: ").append(Colors.BOLD).append(channel).append(Colors.NORMAL).append("; ");
                }
                if (!StringUtils.isEmpty(reason)) {
                    str.append(" for the reason of: ").append(Colors.BOLD).append(reason).append(Colors.NORMAL).append("; ");
                }
                if (!StringUtils.isEmpty(thread)) {
                    str.append(" with O5 thread: ").append(Colors.BOLD).append(thread).append(Colors.NORMAL).append("; ");
                }
                if (endTime != null) {
                    str.append(" EndTime: ").append(Colors.BOLD).append(endTime.getYear() == 2999 ? "forever" : DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(endTime)).append(Colors.NORMAL);
                }
                if (!(str.length() == 0)) {
                    response = str.toString();
                }


        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Integer getBanid() {
        return banid;
    }

    public Set<String> getFlagSet() {
        return flagSet;
    }

    public String getReason() {
        return reason;
    }

    public String getThread() {
        return thread;
    }

    public String getChannel() {
        return channel;
    }

    public String getResponse() {
        return response;
    }

    public List<String> getUsers() {
        return users;
    }

    public List<String> getHostmasks() {
        return hostmasks;
    }

    public Optional<String> getTime() {
        return time;
    }

    public Optional<String> getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
