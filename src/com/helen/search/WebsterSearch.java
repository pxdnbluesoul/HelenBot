package com.helen.search;

import com.helen.commands.Command;
import com.helen.database.framework.Config;
import com.helen.database.framework.Configs;
import org.apache.log4j.Logger;
import org.jibble.pircbot.Colors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

public class WebsterSearch {

    private static Logger logger = Logger.getLogger(WebsterSearch.class);

    public static String dictionarySearch(String query) {
        query = query.toLowerCase();
        String result = Command.ERROR;
        try {
            Optional<Document> doc = findDefinition(query);
            if (!doc.isPresent()) {
                return result;
            }
            NodeList sugList = doc.get().getElementsByTagName("suggestion");
            if (sugList.getLength() > 0) {
                System.out.println(sugList.item(0).getFirstChild().getNodeValue());
                Optional<Document> resultingPage = findDefinition(sugList.item(0).getFirstChild().getNodeValue());
                if (!resultingPage.isPresent()) {
                    return Command.ERROR;
                }
                result = processDocument(resultingPage.get(), sugList.item(0).getFirstChild().getNodeValue());
            } else {
                result = processDocument(doc.get(), query);
            }

        } catch (Exception e) {
            logger.error("Exception dictionary searching", e);
        }
        return result;
    }

    private static Optional<Document> findDefinition(String keyword) {

        Document doc = null;
        try {
            StringBuilder result = new StringBuilder();
            Optional<Config> websterSearch = Configs.getSingleProperty("dictionaryURL");
            Optional<Config> dictionaryKey = Configs.getSingleProperty("dictionaryKey");
            if (!websterSearch.isPresent() || !dictionaryKey.isPresent()) {
                return Optional.empty();
            }
            URL url = new URL(websterSearch.get().getValue() + keyword
                    + "?key=" + dictionaryKey.get().getValue());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            InputSource is;

            builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(result.toString()));
            doc = builder.parse(is);
        } catch (Exception e) {
            logger.error("There was an exception attempting to retreive dictionary results", e);
        }

        return doc == null ? Optional.empty() : Optional.of(doc);
    }

    private static String processDocument(Document doc, String keyword) {
        NodeList list = doc.getElementsByTagName("entry");
        if (list.getLength() > 0) {
            String alternateForm =
                    ((Element) list.item(0))
                            .getElementsByTagName("ew")
                            .item(0)
                            .getFirstChild()
                            .getNodeValue();
            ArrayList<Node> nodesToAnalyze = new ArrayList<>();
            try {
                for (int i = 0; i < list.getLength(); i++) {
                    NodeList nl = ((Element) list.item(i)).getElementsByTagName("ew");
                    for (int j = 0; j < nl.getLength(); j++) {
                        String value = nl.item(j).getFirstChild().getNodeValue();
                        if (keyword.equals(value) || alternateForm.equals(value)) {
                            nodesToAnalyze.add(list.item(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<Definition> test = new ArrayList<>();
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
                        definition = testList.item(1).getFirstChild().getNodeValue();
                    } else if (fwList.getLength() > 0) {
                        definition = defs.item(i).getFirstChild().getNodeValue().replace(":", "") + testList.item(1).getFirstChild().getNodeValue().replace(":", "");

                    } else {
                        definition = defs.item(i).getFirstChild().getNodeValue().replace(":", "");
                    }
                    if (!definition.trim().isEmpty()) {
                        def.definitions.add(definition);
                    }
                }
                test.add(def);
            }
            StringBuilder str = new StringBuilder();
            str.append(Colors.BOLD);
            str.append(keyword);
            str.append(" - ");
            str.append(Colors.NORMAL);
            for (Definition d : test) {
                str.append(Colors.BOLD);
                str.append(d.partOfSpeech);
                str.append(": ");
                str.append(Colors.NORMAL);
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

            return str.toString();
        } else {
            return "I couldn't find a definition for " + keyword + ".";
        }
    }
}
