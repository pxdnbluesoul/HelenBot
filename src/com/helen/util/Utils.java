package com.helen.util;

import com.helen.database.framework.DatabaseObject;

import java.util.ArrayList;

public class Utils {

    private static final Long YEARS = 1000 * 60 * 60 * 24 * 365L;
    private static final Long DAYS = 1000 * 60 * 60 * 24L;
    private static final Long HOURS = 1000 * 60L * 60;
    private static final Long MINUTES = 1000 * 60L;

    
    public static String findTime(Long time) {
        time = System.currentTimeMillis() - time;
        long diff;
        if (time >= YEARS) {
            diff = time / YEARS;
            return (time / YEARS) + " year" + (diff > 1 ? "s" : "") + " ago";

        } else if (time >= DAYS) {
            diff = time / DAYS;
            return (time / DAYS) + " day" + (diff > 1 ? "s" : "") + " ago";

        } else if (time >= HOURS) {
            diff = (time / HOURS);
            return (time / HOURS) + " hour" + (diff > 1 ? "s" : "") + " ago";

        } else if (time >= MINUTES) {
            diff = time / MINUTES;
            return (time / MINUTES) + " minute" + (diff > 1 ? "s" : "") + " ago";

        } else {
            return "a few seconds ago";
        }

    }

    public static String buildResponse(ArrayList<? extends DatabaseObject> dbo) {
        StringBuilder str = new StringBuilder();
        str.append("{");
        for (int i = 0; i < dbo.size(); i++) {
            if(dbo.get(i).displayToUser()){
                if (i != 0) {
                    str.append(dbo.get(i).getDelimiter());
                }
                str.append(dbo.get(i).toString());
            }
        }
        str.append("}");
        return str.toString();
    }
}
