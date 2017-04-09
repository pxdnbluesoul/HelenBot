package com.helen.search;

import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;

import com.helen.database.CloseableStatement;
import com.helen.database.Configs;
import com.helen.database.Connector;
import com.helen.database.Queries;

public class WikidotSearch {

	private static final Logger logger = Logger.getLogger(WikidotSearch.class);

	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient client;

	private static HashSet<String> storedPages = new HashSet<String>();
	
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
			client.setConfig(config);

		} catch (Exception e) {
			logger.error("There was an exception", e);
		}
	}

	private static Object pushToAPI(String method, Object... params)
			throws XmlRpcException {
		return (Object) client.execute(method, params);
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
		loadPages();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", Configs.getSingleProperty("site").getValue());
		try {

			Object[] result = (Object[]) pushToAPI("pages.select", params);

			// Convert result to a String[]
			String[] pageList = new String[result.length];
			for (int i = 0; i < result.length; i++) {
				pageList[i] = (String) result[i];
			}
			int i = 0;
			logger.info(pageList.length);
			for (String str : pageList) {
				if(!storedPages.contains(str)){
					try{
						CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertPage"), str);
						stmt.executeUpdate();
					}catch(Exception e){
						logger.error("Couldn't insert page name",e);
					}
				}
			}

			
		} catch (Exception e) {
			logger.error("There was an exception", e);
		}
	}

	public static void getTags() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", Configs.getSingleProperty("site").getValue());

		try {

			Object[] result = (Object[]) pushToAPI("tags.select", params);

			// Convert result to a String[]
			String[] pageList = new String[result.length];
			for (int i = 0; i < result.length; i++) {
				pageList[i] = (String) result[i];
			}
			int i = 0;
			logger.info(pageList.length);
			for (String str : pageList) {
				logger.info(str);
			}
		} catch (Exception e) {
			logger.error("There was an exception", e);
		}
	}
	
	private static void loadPages(){
		storedPages = new HashSet<String>();
		
		try{
			CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getStoredPages"));
			ResultSet rs = stmt.getResultSet();
			
			while(rs != null && rs.next()){
				storedPages.add(rs.getString("pagename"));
			}
		}catch(Exception e){
			logger.error("There was an exception retreiving stored pages",e);
		}
	}

}
