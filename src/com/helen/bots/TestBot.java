package com.helen.bots;

import org.jibble.pircbot.PircBot;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TestBot extends PircBot {


    public static void main(String[] args) throws Exception {
        String timezone = "GMT-05:00";
        String t = timezone.substring(3, 6);
        String m = timezone.substring(7, 8);
        ZoneOffset offset = ZoneOffset.ofHoursMinutes(Integer.parseInt(timezone.substring(3, 6)), Integer.parseInt(timezone.substring(7, 8)));
        System.out.println(t);
        System.out.println(m);

        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(DATE_TIME_FORMATTER.format(Instant.now().atOffset(offset)) + " in that timezone");

    }

}
