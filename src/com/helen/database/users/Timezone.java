package com.helen.database.users;


import com.helen.database.DatabaseObject;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Timezone implements DatabaseObject {

    public static String setTimezone(String userName, String timeZone) {
        try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("setTimezone"), userName.toLowerCase(), timeZone)) {
            if (stmt != null && stmt.executeUpdate()) {
                return "*Jots that down on her clipboard* Gotcha, I'll make a note of that.";
            } else {
                return "Hmm, I didn't quite get that.  Magnus, a word?";
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    public static Set<String> getTimezone(String username) {
        Set<String> timezones = new HashSet<>();
        try {

            Integer groupId = Nicks.getNickGroup(username);
            List<String> usernames = new ArrayList<>();
            if (groupId != null && groupId != -1) {
                List<String> nicks = Nicks.getNicksByGroup(groupId);
                if (nicks != null) {
                    usernames = nicks;
                } else {
                    return timezones;
                }
            } else {
                usernames.add(username);
            }
            Array a = Connector.getConnection().createArrayOf("text", usernames.toArray());
            try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getTimezoneArray"), a)) {
                try (ResultSet rs = stmt != null ? stmt.execute() : null) {
                    while (rs != null && rs.next()) {
                        timezones.add(rs.getString("timezone"));
                    }
                }
                return timezones;
            }
        } catch (Exception e) {
            return timezones;
        }
    }

    public static String deleteMemo(String userName) {
        try (CloseableStatement stmt = Connector.getStatement(Queries.getQuery("deleteTimezone"), userName)) {
            if (stmt != null && stmt.executeDelete()) {
                return "Timezone deleted for: " + userName;
            } else {
                return "Hmm, I didn't quite get that.  Magnus, a word?";
            }
        } catch (Exception e) {
            return "Hmm, I didn't quite get that.  Magnus, a word?";
        }
    }

    @Override
    public String getDelimiter() {
        return null;
    }

    @Override
    public boolean displayToUser() {
        return false;
    }
}
