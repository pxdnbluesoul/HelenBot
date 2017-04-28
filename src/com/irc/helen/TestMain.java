package com.irc.helen;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.jibble.pircbot.Colors;
import org.jsoup.Jsoup;

import com.helen.database.CloseableStatement;
import com.helen.database.Configs;
import com.helen.database.Connector;
import com.helen.database.Queries;
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

	public static void main(String args[]) throws XmlRpcException {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("site", "scp-wiki");
			params.put("page", "most-recently-created");
			params.put("tags_none", new String[]{"admin"});
			//params.put("rating", "");
			params.put("order", "created_at desc");
			params.put("rating", "=");

			try {
				@SuppressWarnings("unchecked")
				Object[] result = (Object[]) pushToAPI("pages.select", params);

				for(int i = 0;i < 5; i++){
					System.out.println(result[i]);
				}
				

			} catch (Exception e) {
			e.printStackTrace();
			}
		}
		
	
	

}
