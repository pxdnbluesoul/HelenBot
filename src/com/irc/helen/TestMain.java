package com.irc.helen;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;

import com.helen.database.XmlRpcTypeNil;

public class TestMain {

	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient client;

	private static Object pushToAPI(String method, Object... params)
			throws XmlRpcException {
		return (Object) client.execute(method, params);
	}

	static {
		config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(
					"https://www.wikidot.com/xml-rpc-api.php"));
			config.setBasicUserName("helenBot");
			config.setBasicPassword("rZzjkX5HuachlDf03DwxUZoY2kjHrHCp");
			config.setEnabledForExceptions(true);
			config.setConnectionTimeout(10 * 1000);
			config.setReplyTimeout(30 * 1000);

			client = new XmlRpcClient();
			client.setTransportFactory(new XmlRpcSun15HttpTransportFactory(
					client));
			client.setTypeFactory(new XmlRpcTypeNil(client));
			client.setConfig(config);

		} catch (Exception e) {
			System.out.println("Failboat");
		}

	}

	public static void main(String args[]) throws XmlRpcException, ParseException {
		String pagename = "SCP-106".toLowerCase();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", "scp-wiki");
		String[] target = new String[]{pagename.toLowerCase()};
		params.put("pages",target);
		ArrayList<String> keyswewant = new ArrayList<String>();
		keyswewant.add("title_shown");
		keyswewant.add("rating");
		keyswewant.add("created_at");
		keyswewant.add("title");
		keyswewant.add("created_by");
		keyswewant.add("tags");
		
			HashMap<String, HashMap<String, Object>> result = (HashMap<String, HashMap<String, Object>>) pushToAPI(
					"pages.get_meta", params);
			
			StringBuilder returnString = new StringBuilder();
			returnString.append(result.get(pagename).get("title_shown"));
			returnString.append(": ");
			returnString.append("Scp-106");
			returnString.append("(Rating: ");
			returnString.append(result.get(pagename).get("rating"));
			returnString.append(". By: )");
			returnString.append(result.get(pagename).get("created_by"));
			returnString.append(" - ");
			returnString.append("http://www.scp-wiki.net/");
			returnString.append(pagename.toLowerCase());
			returnString.append(" ");
			Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) result.get(pagename).get("created_at"));
			Object[] tags = (Object[]) result.get(pagename).get("tags");
			for(Object tag: tags){
				System.out.println(tag.toString());
			}
			returnString.append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) result.get(pagename).get("created_at")));
			System.out.println( returnString.toString());
			
		
	
	}

}
