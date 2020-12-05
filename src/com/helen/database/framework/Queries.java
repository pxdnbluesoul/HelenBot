package com.helen.database.framework;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.HashMap;

public class Queries {

    private final static String findQuery = "select * from queries";
    private static final Logger logger = Logger.getLogger(Queries.class);
    private static HashMap<String, String> queryCache = new HashMap<String, String>();
    private static Boolean valid = false;

    public static void clear() {
        valid = false;
        loadQueries();
    }

    public static String getQuery(String query_name) {
        if (!valid) {
            loadQueries();
        }

        if (queryCache.containsKey(query_name)) {
            return queryCache.get(query_name);
        } else {
            logger.error("Cannot find query specified");
        }

        return null;
    }

    private static void loadQueries() {
        try {
            CloseableStatement stmt = Connector.getStatement(findQuery);
            ResultSet rs = stmt.getResultSet();
            while (rs != null && rs.next()) {
                queryCache.put(rs.getString("query_name"),
                        rs.getString("query"));
            }
            valid = true;
        } catch (Exception e) {
            logger.error("Exception trying to load queries in to cache", e);
        }
    }

}
