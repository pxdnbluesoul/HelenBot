package com.helen.bots;

import com.helen.database.Timezone;
import org.jibble.pircbot.PircBot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestBot extends PircBot {


	public static void main(String[] args) throws Exception {
		String timezone="GMT-05:00";
		String t = timezone.substring(3,6);
		String m = timezone.substring(7,8);
		ZoneOffset offset = ZoneOffset.ofHoursMinutes(Integer.parseInt(timezone.substring(3,6)),Integer.parseInt(timezone.substring(7,8)));
		System.out.println(t);
		System.out.println(m);

		DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println(DATE_TIME_FORMATTER.format(Instant.now().atOffset(offset)) + " in that timezone");

	}

}
