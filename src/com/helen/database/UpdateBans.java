package com.helen.database;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.util.converter.LocalDateStringConverter;

public class UpdateBans {

	public static void updateBans() throws IOException {
		
		URL url = new URL("http://05command.wikidot.com/chat-ban-page");
		Document result = Jsoup.parse(url, 3000);
		
		Element table = result.select("table").get(0);
		Elements rows = table.select("tr");
		HashSet<BanInfo> bannedUsers = new HashSet<BanInfo>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDateStringConverter conv = new LocalDateStringConverter(formatter, null);
		for(int i = 0; i < rows.size(); i++) {
			Element row = rows.get(i);
			//skip first two rows
			if (i > 1) {
				Elements entries = row.select("td");
				String names = entries.get(0).text();
				String ips = entries.get(1).text();
				String date = entries.get(2).text();
				String reason = entries.get(3).text();
				
				List<String> nameList = Arrays.asList(names.split(" "));
				List<String> ipList = Arrays.asList(ips.split(" "));
				LocalDate bdate;
				//TODO replace this with regex
				if(date.contains("/")) {
					bdate = conv.fromString(date);
					
				} else {

					bdate = conv.fromString("12/31/2999");
				}
				
				BanInfo info = new BanInfo(nameList, ipList, reason, bdate);
				bannedUsers.add(info);
			}
		}
		
		BanUser.set19Bans(bannedUsers);
	}
}
