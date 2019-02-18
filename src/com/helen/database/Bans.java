package com.helen.database;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Bans {

	private static final HashSet<BanInfo> bansIn19 = new HashSet<>();
	private static final HashSet<BanInfo> bansIn17 = new HashSet<>();
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	public static void updateBans() throws IOException {

		bansIn19.clear();
		bansIn17.clear();

		URL url = new URL("http://05command.wikidot.com/chat-ban-page");
		Document result = Jsoup.parse(url, 3000);
		Element table19 = result.select("table").get(0);
		Element table17 = result.select("table").get(1);

		populateBanList(bansIn19, table19);
		populateBanList(bansIn17, table17);


	}

	private static void populateBanList(HashSet<BanInfo> banList, Element table){
		Elements rows = table.select("tr");
		for(int i = 2; i < rows.size(); i++) {
			Element row = rows.get(i);
			//skip first two rows
			Elements entries = row.select("td");
			String names = entries.get(0).text();
			String ips = entries.get(1).text();
			String date = entries.get(2).text();
			String reason = entries.get(3).text();

			List<String> nameList = Arrays.asList(names.split(" "));
			List<String> ipList = Arrays.asList(ips.split(" "));
			LocalDate bdate;



			if(date.contains("/")) {
				bdate = LocalDate.parse(date,formatter);
			} else {
				bdate = LocalDate.parse("12/31/2999",formatter);
			}

			banList.add(new BanInfo(nameList, ipList, reason, bdate));
		}
	}



	public static BanInfo getUserBan(String username, String hostmask, String channel){
		LocalDate today = LocalDate.now();
		if(channel.equalsIgnoreCase("#site17")){
			for(BanInfo info : bansIn17){
				if((info.getIPs().contains(hostmask) || info.getUserNames().contains(username)) && info.getBanEnd().isAfter(today)){
					return info;
				}
			}
		}else if(channel.equalsIgnoreCase("#site19") || channel.equalsIgnoreCase("#thecritters") ){
			for(BanInfo info : bansIn19){
				if((info.getIPs().contains(hostmask) || info.getUserNames().contains(username)) && info.getBanEnd().isAfter(today)){
					return info;
				}
			}
		}else{
			return null;
		}


		return null;
	}
}
