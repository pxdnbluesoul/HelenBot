package com.helen.database.users;

import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;

public class Hugs {

    private final static Logger logger = Logger.getLogger(Hugs.class);

    public static String getHugMessage(String username) {
        String hug = "I'm sorry, I don't think you've told me what you would like me to say yet.";
        try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getHug"), username)) {
            try (ResultSet rs = stmt != null ? stmt.getResultSet() : null) {
                if (rs != null && rs.next()) {
                    hug = rs.getString("hug");
                }
            }
        } catch (Exception e) {
            logger.error("Couldn't get hug", e);
        }

        return hug.trim();
    }

    public static String storeHugmessage(CommandData data) {
        try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertHug"), data.getSender().toLowerCase(),
                data.getMessageWithoutCommand())) {
            if (stmt != null) {
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            if (!e.getMessage().contains("hugs_pkey")) {
                logger.error("Couldn't store the hug", e);

            } else {
                logger.info("Updating");
                updateHugMessage(data.getSender().toLowerCase(), data.getMessageWithoutCommand());
            }
        }

        return "*Jots that down on her clipboard* Noted, " + data.getSender() + ".";
    }

    public static void updateHugMessage(String username, String message) {
        try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("updateHug"), message,
                username)) {
            if (stmt != null) {
                stmt.executeUpdate();
                logger.info("Update finished");
            }
        } catch (Exception e) {
            logger.error("Couldn't store the hug", e);

        }
    }

}
