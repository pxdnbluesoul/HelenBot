package com.irc.helen;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;

import com.helen.database.XmlRpcTypeNil;

public class TestMain {

	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient client;

	
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
		try {
			String regex = "<td style=\"vertical-align: top;\"><a href=\"\\/(.+)\">(.+)-(.+)<\\/a><\\/td>";
			Pattern r = Pattern.compile(regex);
			ArrayList<String> pagelist = new ArrayList<String>();

			URL u;
			InputStream is = null;
			DataInputStream dis;
			String s;
			u = new URL("http://www.scp-wiki.net/most-recently-created");
			is = u.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));
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
			for (String str : pagelist) {
				System.out.println(str);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
