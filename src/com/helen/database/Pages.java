package com.helen.database;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.jibble.pircbot.Colors;

public class Pages {

	private static final Logger logger = Logger.getLogger(Pages.class);
	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient client;
	private static Long lastLc = System.currentTimeMillis() - 20000;
	private static HashMap<String, ArrayList<Page>> storedEvents = new HashMap<String, ArrayList<Page>>();

	static {
		config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(Configs.getSingleProperty(
					"wikidotServer").getValue()));
			config.setBasicUserName(Configs.getSingleProperty("appName")
					.getValue());
			config.setBasicPassword(Configs.getSingleProperty("wikidotapikey")
					.getValue());
			config.setEnabledForExceptions(true);
			config.setConnectionTimeout(10 * 1000);
			config.setReplyTimeout(30 * 1000);

			client = new XmlRpcClient();
			client.setTransportFactory(new XmlRpcSun15HttpTransportFactory(
					client));
			client.setTypeFactory(new XmlRpcTypeNil(client));
			client.setConfig(config);

		} catch (Exception e) {
			logger.error("There was an exception", e);
		}

	}

	private static Object pushToAPI(String method, Object... params)
			throws XmlRpcException {
		return (Object) client.execute(method, params);
	}

	public static ArrayList<String> lastCreated() {
		if (System.currentTimeMillis() - lastLc > 15000) {
			ArrayList<String> pagelist = new ArrayList<String>();
			try {
				String regex = "<td style=\"vertical-align: top;\"><a href=\"\\/(.+)\">(.+)<\\/a><\\/td>";
				Pattern r = Pattern.compile(regex);
				String s;
				URL u = new URL("http://www.scp-wiki.net/most-recently-created");
				InputStream is = u.openStream();
				DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
				int i = 0;
				while ((s = dis.readLine()) != null) {
					Matcher m = r.matcher(s);
					if (m.matches()) {
						if (i++ < 3) {
							pagelist.add(m.group(1));
						} else {
							dis.close();
							break;
						}
					}
				}
			} catch (Exception e) {
				logger.error(
						"There was an exception attempting to grab last created",
						e);
			}
			return pagelist;
		}

		return null;

	}

	public static void getMethodList() {
		try {
			Object[] result = (Object[]) pushToAPI("system.listMethods",
					(Object[]) null);

			String[] methodList = new String[result.length];
			for (int i = 0; i < result.length; i++) {
				methodList[i] = (String) result[i];
			}

			for (String str : methodList) {
				logger.info(str);
			}
			// return methodList;
		} catch (Exception e) {
			logger.error("There was an exception", e);
		}
	}

	private static String getTitle(String pagename) {
		String pageName = null;
		try {
			CloseableStatement stmt = Connector.getStatement(
					Queries.getQuery("getPageByName"), pagename);
			ResultSet rs = stmt.getResultSet();
			if (rs != null && rs.next()) {
				pageName = rs.getString("title");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			logger.error("Exception getting title", e);
		}
		return pageName;

	}

	public static String getPageInfo(String pagename) {
		String targetName = pagename.toLowerCase();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", Configs.getSingleProperty("site").getValue());
		String[] target = new String[] { targetName.toLowerCase() };
		params.put("pages", target);
		ArrayList<String> keyswewant = new ArrayList<String>();
		keyswewant.add("title_shown");
		keyswewant.add("rating");
		keyswewant.add("created_at");
		keyswewant.add("title");
		keyswewant.add("created_by");
		keyswewant.add("tags");
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<String, Object>> result = (HashMap<String, HashMap<String, Object>>) pushToAPI(
					"pages.get_meta", params);

			StringBuilder returnString = new StringBuilder();
			returnString.append(Colors.BOLD);

			String title = getTitle(targetName);
			if (title == null || title.isEmpty() || title.equals("[ACCESS DENIED]")) {
				returnString.append(result.get(targetName).get("title_shown"));
			} else {
				returnString.append(result.get(targetName).get("title_shown"));
				returnString.append(": ");
				returnString.append(title);
			}
			returnString.append(Colors.NORMAL);
			returnString.append(" (Rating: ");
			Integer rating = (Integer) result.get(targetName).get("rating");
			if (rating > 0) {
				returnString.append("+");
			}
			returnString.append(rating);
			returnString.append(". By: ");
			returnString.append(result.get(targetName).get("created_by"));
			returnString.append(")");
			returnString.append(" - ");
			returnString.append("http://www.scp-wiki.net/");
			returnString.append(targetName);

			return returnString.toString();

		} catch (Exception e) {
			logger.error("There was an exception retreiving metadata", e);
		}

		return "I couldn't find anything matching that, apologies.";
	}
	
	public static String getAuthorDetail(String user){
		user = user.toLowerCase();
		Page authorPage = null;
		try{
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("findAuthorPage"), user);
			ResultSet rs = stmt.getResultSet();
			if(rs != null && rs.next()){
				authorPage = new Page(rs.getString("pagename"),
						rs.getString("title"),
						rs.getBoolean("scppage"),
						rs.getString("scptitle"));
			}
			rs.close();
			stmt.close();
			
			ArrayList<Page> pages = new ArrayList<Page>();
			stmt = Connector.getStatement(Queries.getQuery("findPagesByAuthor"), user);
			rs = stmt.getResultSet();
			while(rs != null && rs.next()){
				pages.add( new Page(rs.getString("pagename"),
						rs.getString("title"),
						rs.getInt("rating"),
						rs.getString("created_by"),
						rs.getTimestamp("created_on"),
						rs.getBoolean("scppage"),
						rs.getString("scptitle")));
			}
			
			if(pages.size() == 0){
				return "I'm sorry, I don't think that author exists.  Check your spelling?";
			}
			
			StringBuilder str = new StringBuilder();
			str.append(Colors.BOLD);
			str.append(user);
			str.append(Colors.NORMAL);
			str.append(" - ");
			
			if(authorPage != null){
				str.append("(");
				str.append("www.scp-wiki.net/");
				str.append(authorPage.getPageLink());
				str.append(") ");
			}
			String authorPageName = authorPage == null ? "null" : authorPage.getPageLink();
			int scps = 0;
			int tales = 0;
			int rating = 0;
			Timestamp ts = new java.sql.Timestamp(0l);
			Page latest = null;
			for(Page p: pages){
				if(!p.getPageLink().equals(authorPageName)){
					if(p.getScpPage()){
						scps++;
					}else{
						tales++;
					}
					rating += p.getRating();
					
					if(p.getCreatedAt().compareTo(ts) > 0){
						ts = p.getCreatedAt();
						latest = p;
					}
				}
			}
			str.append("has ");
			str.append(Colors.BOLD);
			str.append(scps + tales);
			str.append(Colors.NORMAL);
			str.append(" pages. (");
			str.append(Colors.BOLD);
			str.append(scps);
			str.append(Colors.NORMAL);
			str.append(" SCP articles, ");
			str.append(Colors.BOLD);
			str.append(tales);
			str.append(Colors.NORMAL);
			str.append(" Tales).");
			str.append(" They have ");
			str.append(Colors.BOLD);
			str.append(rating);
			str.append(Colors.NORMAL);
			str.append(" net upvotes with an average of ");
			str.append(Colors.BOLD);
			double avg = (rating) / (tales + scps);
			str.append(avg);
			str.append(Colors.NORMAL);
			str.append(".  Their latest page is ");
			str.append(Colors.BOLD);
			if(latest.getScpPage()){
				str.append(latest.getTitle());
				str.append(": ");
				str.append(latest.getScpTitle());
			}else{
				str.append(latest.getTitle());
			}
			str.append(Colors.NORMAL);
			str.append(" at ");
			str.append(Colors.BOLD);
			str.append(latest.getRating());
			str.append(Colors.NORMAL);
			str.append(".");
			
			return str.toString();
			
			
		}catch(Exception e){
			logger.error("Error constructing author detail",e);
		}
		
		return "I apologize, there's been an error.  Please inform DrMagnus there's an error with author details.";
	}

	public static String getPotentialTargets(String[] terms, String username) {
		ArrayList<Page> potentialPages = new ArrayList<Page>();
		String[] lowerterms = new String[terms.length - 1];
		for (int i = 1; i < terms.length; i++) {
			lowerterms[i - 1] = terms[i].toLowerCase();
			logger.info(lowerterms[i - 1]);
		}
		try {
			CloseableStatement stmt = Connector.getArrayStatement(
					Queries.getQuery("findSCPS"), lowerterms);
			logger.info(stmt.toString());
			ResultSet rs = stmt.getResultSet();

			while (rs != null && rs.next()) {
				potentialPages.add(new Page(rs.getString("pagename"), rs
						.getString("title"), rs.getBoolean("scppage"), rs
						.getString("scptitle")));
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("There was an issue grabbing potential SCP pages", e);
		}

		if (potentialPages.size() > 1) {
			storedEvents.put(username, potentialPages);
			StringBuilder str = new StringBuilder();
			str.append("(Beta)Did you mean : ");

			for (Page page : potentialPages) {
				str.append(Colors.BOLD);
				str.append(page.getScpPage() ? page.getTitle()
						+ page.getScpTitle() : page.getTitle());
				str.append(Colors.NORMAL);
				str.append(",");
			}
			str.append("?");
			return str.toString();
		} else {
			if (potentialPages.size() == 1) {
				return getPageInfo(potentialPages.get(0).getPageLink());
			} else {
				return "I couldn't find anything.";
			}
		}
	}

	public static String getStoredInfo(String index, String username) {
		try {
			return getPageInfo(storedEvents.get(username)
					.get(Integer.parseInt(index) - 1).getPageLink());
		} catch (Exception e) {
			logger.error("There was an exception getting stored info", e);
		}

		return "Either the command was malformed, or I have nothing for you to get.";
	}

}
