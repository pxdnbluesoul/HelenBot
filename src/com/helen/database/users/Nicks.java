package com.helen.database.users;

import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nicks {

    final static Logger logger = Logger.getLogger(Nicks.class);

    public static String addNick(CommandData data) {
        try {
            UserNick nick = new UserNick(data);
            if (nick.getNickToGroup() != null) {
                if (nick.isNewNick()) {
                    CloseableStatement insertBase = Connector.getStatement(Queries.getQuery("insert_grouped_nick"),
                            nick.getGroupId(), data.getSender().toLowerCase());
                    if (insertBase.executeUpdate()) {
                        CloseableStatement insertStatement = Connector.getStatement(Queries.getQuery("insert_grouped_nick"),
                                nick.getGroupId(), nick.getNickToGroup().toLowerCase());
                        if (insertStatement.executeUpdate()) {
                            return "Established a new nickgroup under " + data.getSender() + " and added the nick " + nick.getNickToGroup() + " as a grouped nick.";
                        } else {
                            return "Something went very wrong, pinging DrMagnus.";
                        }
                    }

                } else {
                    CloseableStatement insertStatement = Connector.getStatement(Queries.getQuery("insert_grouped_nick"),
                            nick.getGroupId(), nick.getNickToGroup().toLowerCase());
                    if (insertStatement.executeUpdate()) {
                        return "Inserted " + nick.getNickToGroup() + " for user " + data.getSender() + ".";

                    } else {
                        return "Something went very wrong, pinging DrMagnus.";
                    }

                }


            } else {
                return "Your nick is already grouped with ID: " + nick.getGroupId();
            }

        } catch (Exception e) {
            logger.error("Error inserting in to the database", e);
        }
        return "Failed to insert grouped nick, please contact DrMagnus.";
    }

    public static String deleteNick(CommandData data) {
        Integer id = getNickGroup(data.getSender());
        if (id != null && id != -1) {
            try {
                Connector.getStatement(Queries.getQuery("deleteGroupedNick"), data.getTarget().toLowerCase()).executeUpdate();
            } catch (Exception e) {
                logger.error("Exception trying to delete nick.", e);
            }
            return "Deleted " + data.getTarget() + " from your grouped nicks.";
        } else {
            return "I didn't find any grouped nicks for your username.";
        }
    }

    private static boolean deleteNick(String data) {
        try {
            return Connector.getStatement(Queries.getQuery("deleteGroupedNick"), data.toLowerCase()).executeUpdate();
        } catch (Exception e) {
            logger.error("Exception trying to delete nick.", e);
        }
        return false;
    }

    public static String deleteAllNicks(CommandData data, boolean admin) {
        Integer id = getNickGroup(admin ? data.getTarget() : data.getSender());
        if (id != null && id != -1) {
            List<String> nicks = getNicksByGroup(id);
            boolean flag = true;
            for (String nick : nicks) {
                flag = deleteNick(nick);
                if (flag) {
                    continue;
                } else {
                    return "There was a problem deleting nicks.";
                }
            }
            return "Deleted all nicks for your group.";
        } else {
            return "Your nick is not grouped";
        }
    }

    public static List<String> getNicksByGroup(Integer id) {
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getNicks"),
                    id);
            ResultSet rs = stmt.execute();
            ArrayList<String> nicks = new ArrayList<>();
            while (rs != null && rs.next()) {
                nicks.add(rs.getString("nick").toLowerCase());
            }
            return nicks;
        } catch (Exception e) {
            logger.error("There was an exception returning nick grouped nicks.", e);
        }
        return null;

    }

    public static List<String> getNicksByUsername(String username) {
        Integer group = getNickGroup(username.toLowerCase());
        if (group != null && group != -1) {
            return getNicksByGroup(group);
        } else {
            return Collections.emptyList();
        }
    }

    public static Integer getNickGroup(String username) {
        try (CloseableStatement stmt = Connector.getStatement(Queries
                .getQuery("find_nick_group"), username
                .toLowerCase())) {
            try (ResultSet rs = stmt != null ? stmt.execute() : null) {
                if (rs != null && rs.next()) {
                    return rs.getInt("id");
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("Error looking up the nick_group id for " + username, e);
        }
        return null;
    }


}
