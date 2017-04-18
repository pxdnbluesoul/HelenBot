package com.irc.helen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TestMain {

	public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException {

		StringBuilder result = new StringBuilder();
		URL url = new URL(
				"http://www.dictionaryapi.com/api/v1/references/collegiate/xml/test?key=b5708b1a-8601-4fc7-9966-549b276382e2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		System.out.println(result.toString());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		InputSource is;

		builder = factory.newDocumentBuilder();
		is = new InputSource(new StringReader(result.toString()));
		Document doc = builder.parse(is);
		NodeList list = doc.getElementsByTagName("entry");
		doSomething(doc.getDocumentElement());

	}

	public static void doSomething(Node node) {
		// do something with the current node instead of System.out
		
		if(node.getNodeName().equalsIgnoreCase("ew")){
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}
		
		if(node.getNodeName().equalsIgnoreCase("fl")){
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}
		
		if(node.getNodeName().equalsIgnoreCase("dt")){
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}
		
		if(node.getNodeName().equalsIgnoreCase("sn")){
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}
		
		if(node.getNodeName().equalsIgnoreCase("snp")){
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}
	

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which is Element
				doSomething(currentNode);
			}
		}
	}
}
