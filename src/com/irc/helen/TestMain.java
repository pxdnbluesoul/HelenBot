
package com.irc.helen;

import com.helen.commands.IRCCommand;
import com.helen.database.Configs;
import com.helen.database.XmlRpcTypeNil;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.jibble.pircbot.Colors;

import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestMain {

	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient client;

	static{
		config = new XmlRpcClientConfigImpl();

		try {
			config.setServerURL(new URL("https://www.wikidot.com/xml-rpc-api.php"));
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

		}catch (Exception e){
			e.printStackTrace();
		}

	}
	private static Object pushToAPI(String method, Object... params)
			throws XmlRpcException {
		return (Object) client.execute(method, params);
	}


	public static void main(String[] args) throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String targetName = "decompression";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", "scp-wiki");
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
			System.out.println(result.keySet());
			for(String s: result.keySet()){
				for(String j: result.get(s).keySet()){
					if(j.equals("tags")) {
						for(Object o: (Object[])result.get(s).get(j)){
							System.out.println(o.toString());
						}
					}else{
						System.out.println(result.get(s).get(j));
					}
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	
}
