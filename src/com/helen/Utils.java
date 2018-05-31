package com.helen;

public class Utils {

    private static final Long YEARS = 1000 * 60 * 60 * 24 * 365l;
    private static final Long DAYS = 1000 * 60 * 60 * 24l;
    private static final Long HOURS = 1000 * 60l * 60;
    private static final Long MINUTES = 1000 * 60l;

    
    public static String findTime(Long time) {
        time = System.currentTimeMillis() - time;
        Long diff = 0l;
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
}
