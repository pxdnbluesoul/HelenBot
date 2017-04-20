package com.irc.helen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helen.search.Definition;

public class TestMain {

	public static void main(String args[]) throws DOMException, Exception {

		String keyword = "Revolver";
		
		
		Document doc = findDefinition(keyword.toLowerCase());
		NodeList sugList = doc.getElementsByTagName("suggestion");
		if(sugList.getLength() > 0){
			System.out.println(sugList.item(0).getFirstChild().getNodeValue());
			doProcess(findDefinition(sugList.item(0).getFirstChild().getNodeValue()), sugList.item(0).getFirstChild().getNodeValue());
		}else{
			doProcess(doc, keyword.toLowerCase());
		}
		
		
		
		
		
		

	}
	
	public static Document findDefinition(String keyword) throws Exception{
		StringBuilder result = new StringBuilder();
		URL url = new URL(
				"http://www.dictionaryapi.com/api/v1/references/collegiate/xml/" + keyword + "?key=b5708b1a-8601-4fc7-9966-549b276382e2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
			System.out.println(line);
		}
		rd.close();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		InputSource is;

		builder = factory.newDocumentBuilder();
		is = new InputSource(new StringReader(result.toString()));
		return builder.parse(is);
	}
	
	public static void doProcess(Document doc, String keyword){
		NodeList list = doc.getElementsByTagName("entry");
		if(list.getLength() > 0){
		ArrayList<Node> nodesToAnalyze = new ArrayList<Node>();
		try {
			for (int i = 0; i < list.getLength(); i++) {
				NodeList nl = ((Element) list.item(i)).getElementsByTagName("ew");
				for (int j = 0; j < nl.getLength(); j++) {
					if (nl.item(j).getFirstChild().getNodeValue().equals(keyword)) {
						nodesToAnalyze.add(list.item(i));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<Definition> test = new ArrayList<Definition>();
		for (Node n : nodesToAnalyze) {
			Definition def = new Definition();
			Element nodeler = (Element) n;
			def.partOfSpeech = nodeler.getElementsByTagName("fl").item(0).getFirstChild().getNodeValue();
			NodeList defs = ((Element) nodeler.getElementsByTagName("def").item(0)).getElementsByTagName("dt");
			for (int i = 0; i < defs.getLength(); i++) {
				NodeList sxList = ((Element) defs.item(i)).getElementsByTagName("sx");
				NodeList fwList = ((Element) defs.item(i)).getElementsByTagName("fw");
				NodeList testList = defs.item(i).getChildNodes();
				String definition;
				
				
				if (sxList.getLength() > 0) {
					definition =  testList.item(1).getFirstChild().getNodeValue().replace(":", "");
				}else if(fwList.getLength() > 0){
					definition = defs.item(i).getFirstChild().getNodeValue().replace(":", "") + testList.item(1).getFirstChild().getNodeValue().replace(":", "");
					
				}
					else {
					definition = defs.item(i).getFirstChild().getNodeValue().replace(":", "");
					
				}
				if (!definition.trim().isEmpty()) {
					def.definitions.add(definition);
				}
			}
			test.add(def);
		}
		StringBuilder str = new StringBuilder();
		str.append(keyword);
		str.append(": ");
		for (Definition d : test) {
			str.append(d.partOfSpeech);
			str.append(" - ");
			int i = 0;
			for (String s : d.definitions) {
				if (i < 2) {
					str.append((i + 1));
					str.append(". ");
					str.append(d.definitions.get(i++));
					str.append(" ");
				}
			}

		}
		
		System.out.println(str.toString());
		}else{
			System.out.println("I couldn't find a definition for " + keyword);
		}
	}

	public static void doSomething(Node node, String keyword) {
		// do something with the current node instead of System.out

		if (node.getNodeName().equalsIgnoreCase("ew")) {
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}

		if (node.getNodeName().equalsIgnoreCase("fl")) {
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}

		if (node.getNodeName().equalsIgnoreCase("dt")) {
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}

		if (node.getNodeName().equalsIgnoreCase("sx")) {
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}

		if (node.getNodeName().equalsIgnoreCase("snp")) {
			System.out.println(node.getNodeName() + ":" + node.getFirstChild().getNodeValue());
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

				// calls this method for all the children which is Element
				doSomething(currentNode, keyword);
			}
		}
	}

	public static void procDef(Node node) {

	}
}
