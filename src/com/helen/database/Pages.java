package com.helen.database;

import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.jibble.pircbot.Colors;
import org.jsoup.Jsoup;

public class Pages {

	private static final Logger logger = Logger.getLogger(Pages.class);
	private static Boolean synching = false;
	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient client;
	private static HashSet<String> storedPages = new HashSet<String>();
	private static HashMap<String, String> titleToPageName = new HashMap<String, String>();
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

		loadPages();
	}

	private static Object pushToAPI(String method, Object... params)
			throws XmlRpcException {
		return (Object) client.execute(method, params);
	}

	public static void uploadSeries() {
		String regex = "(?m)<li><a href=\"\\/(.+)\">(.+)<\\/a> - (.+)<\\/li>";
		Pattern r = Pattern.compile(regex);

		String[] pages = new String[] { "scp-series	", "scp-series-2",
				"scp-series-3", "scp-series-4", "joke-scps" };

		for (String page : pages) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("site", "scp-wiki");
			params.put("page", page);

			try {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> result = (HashMap<String, Object>) pushToAPI(
						"pages.get_one", params);

				String[] lines = ((String) result.get("html")).split("\n");
				ArrayList<String[]> pagelist = new ArrayList<String[]>();
				for (String s : lines) {
					Matcher m = r.matcher(s);
					if (m.find()) {
						pagelist.add(new String[] { m.group(1), m.group(2),
								Jsoup.parse(m.group(3)).text() });
					}
				}

				for (String[] pageParts : pagelist) {
					if (!storedPages.contains(pageParts[0])) {
						CloseableStatement stmt = Connector.getStatement(
								Queries.getQuery("insertPage"), pageParts[0],
								pageParts[2]);
						stmt.executeUpdate();
					} else {
						CloseableStatement stmt = Connector.getStatement(
								Queries.getQuery("updateTitle"),
								pageParts[2],pageParts[0]);
						stmt.executeUpdate();
					}
				}

				loadPages();

			} catch (Exception e) {
				logger.error(
						"There was an exception attempting to grab the series page metadata",
						e);
			}
		}
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

	public static void listPage() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", Configs.getSingleProperty("site").getValue());
		try {

			Object[] result = (Object[]) pushToAPI("pages.select", params);

			// Convert result to a String[]
			String[] pageList = new String[result.length];
			for (int i = 0; i < result.length; i++) {
				pageList[i] = (String) result[i];
			}
			logger.info(pageList.length);
			for (String str : pageList) {
				if (!storedPages.contains(str)) {
					try {
						CloseableStatement stmt = Connector.getStatement(
								Queries.getQuery("insertPage"), str, str);
						stmt.executeUpdate();
					} catch (Exception e) {
						logger.error("Couldn't insert page name", e);
					}
				}
			}

			loadPages();
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
		} catch (Exception e) {
			logger.error("Exception getting title", e);
		}
		return pageName;

	}

	public static String getPageInfo(String[] pagename) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", Configs.getSingleProperty("site").getValue());
		params.put("pages", pagename);
		ArrayList<String> keyswewant = new ArrayList<String>();
		keyswewant.add("title_shown");
		keyswewant.add("rating");
		keyswewant.add("created_at");
		keyswewant.add("title");
		keyswewant.add("created_by");
		keyswewant.add("tags");
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<String, Object>> result = (HashMap<String, HashMap<String, Object>>) pushToAPI(
					"pages.get_meta", params);

			StringBuilder returnString = new StringBuilder();
			returnString.append(Colors.BOLD);

			for (String targetName : result.keySet()) {
				try {
					// String title = (String)
					// result.get(targetName).get("title");
					String displayTitle = (String) result.get(targetName).get(
							"title_shown");
					Integer rating = (Integer) result.get(targetName).get(
							"rating");
					String creator = (String) result.get(targetName).get(
							"created_by");
					Date createdAt = df.parse((String) result.get(targetName)
							.get("created_at"));
					CloseableStatement stmt = Connector.getStatement(
							Queries.getQuery("updateMetadata"), displayTitle == null ? "unknown" : displayTitle,
							rating == null ? 0 : rating, creator == null ? "unknown" : creator,
							new java.sql.Timestamp(createdAt == null ? System.currentTimeMillis() : createdAt.getTime()) ,
							targetName);
					stmt.executeUpdate();
				} catch (Exception e) {
					logger.error("Error updating metadata", e);
				}
			}
		} catch (Exception e) {
			logger.error("There was an exception retreiving metadata", e);
		}

		return "I couldn't find anything matching that, apologies.";
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
			if (title == null || title.isEmpty()) {
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

	public static void getTags() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", Configs.getSingleProperty("site").getValue());
		ArrayList<String> tags = new ArrayList<String>();

		try {
			CloseableStatement stmt = Connector.getStatement(Queries
					.getQuery("getTags"));
			ResultSet rs = stmt.getResultSet();
			if (rs != null) {
				while (rs.next()) {
					tags.add(rs.getString("tag"));
				}
			}
			stmt.close();
		} catch (Exception e) {
			logger.error("Exception getting tags", e);
		}

		try {

			Object[] result = (Object[]) pushToAPI("tags.select", params);

			// Convert result to a String[]
			ArrayList<String> pageList = new ArrayList<String>();
			for (int i = 0; i < result.length; i++) {
				pageList.add((String) result[i]);
			}
			// Insert differences between wiki tags and current list
			for (String str : pageList) {
				if (!tags.contains(str)) {
					CloseableStatement stmt = Connector.getStatement(
							Queries.getQuery("insertTag"), str);
					stmt.executeUpdate();
				}
			}
			// remove things in the database that aren't on the wiki
			for (String str : tags) {
				if (!pageList.contains(str)) {
					CloseableStatement stmt = Connector.getStatement(
							Queries.getQuery("deleteTag"), str);
					stmt.executeUpdate();
					logger.info("Removed tag: " + str);
				}
			}
		} catch (Exception e) {
			logger.error("There was an exception", e);
		}
	}

	public static void pagesTest() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", Configs.getSingleProperty("site").getValue());

		try {

			Object[] result = (Object[]) pushToAPI("site.pages", params);

			// Convert result to a String[]
			String[] pageList = new String[result.length];
			for (int i = 0; i < result.length; i++) {
				pageList[i] = (String) result[i];
			}
			logger.info(pageList.length);
			for (String str : pageList) {
				logger.info(str);
			}
		} catch (Exception e) {
			logger.error("There was an exception", e);
		}
	}

	private static void loadPages() {
		storedPages = new HashSet<String>();

		try {
			CloseableStatement stmt = Connector.getStatement(Queries
					.getQuery("getStoredPages"));
			ResultSet rs = stmt.getResultSet();

			while (rs != null && rs.next()) {
				storedPages.add(rs.getString("pagename"));
			}
			
			CloseableStatement stmt1 = Connector.getStatement(Queries
					.getQuery("getPageTitles"));
			ResultSet rs1 = stmt.getResultSet();

			while (rs1 != null && rs1.next()) {
				titleToPageName.put(rs.getString("title"),rs.getString("pagename"));
			}
			stmt.close();
			stmt1.close();
		} catch (Exception e) {
			logger.error("There was an exception retreiving stored pages", e);
		}
	}

	public static void checkIfUpdate() {
		if (!synching && Configs.getSingleProperty("featurePages").getValue().equals("true")) {
			try {
				CloseableStatement stmt = Connector.getStatement(Queries
						.getQuery("lastPageUpdate"));
				ResultSet rs = stmt.getResultSet();
				if (rs != null && rs.next()) {
					java.sql.Timestamp ts = rs.getTimestamp("lastUpdate");
					if ((System.currentTimeMillis() - ts.getTime()) > (60 * 60 * 1000)) {
						synching = true;
						listPage();
						gatherMetadata();
						uploadSeries();
					}
					synching = false;
				}
			} catch (Exception e) {
				logger.error("Error checking if update required.", e);
			}
		}
	}

	private static void gatherMetadata() {
		try {
			int j = 0;
			String[] pageSet = new String[10];
			for (String str : storedPages) {
				if (j < 10) {
					pageSet[j] = str;
					j++;
				} else {
					getPageInfo(pageSet);
					pageSet = new String[10];
					j = 0;
				}
			}
		} catch (Exception e) {
			logger.error(
					"There was an error attempting to get pages in groups of ten",
					e);
		}
	}
	
	public static String getPotentialTargets(String[] terms){
		ArrayList<String> potentialPages = new ArrayList<String>();
		logger.info(terms);
		for(String str: titleToPageName.keySet()){
			boolean potential = true;
			for(int i = 1; i < terms.length; i++){
				if(!str.toLowerCase().contains(terms[i].toLowerCase())){
					potential = false;
				}
			}
			if(potential){
				potentialPages.add(str);
			}
		}
		
		if(potentialPages.size() > 1){
			StringBuilder str = new StringBuilder();
			str.append("Did you mean (beta feature, please pick exact title words): ");
			for(String page: potentialPages){
				str.append(getTitle(page));
				str.append(",");
			}
			str.append("?");
			return str.toString();
		}else{
			return getPageInfo(titleToPageName.get(potentialPages.get(0)));
		}
	}

}
