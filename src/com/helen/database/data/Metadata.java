package com.helen.database.data;

public class Metadata {

    private String title;
    private String username;
    private String authorageType;
    private String date;

    public Metadata(String title, String username, String authorageType, String date) {
        this.title = title;
        this.username = username;
        this.authorageType = authorageType;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public String toString() {
        return "Article: " + title + " author: " + username + " authorage type: " + authorageType
                + (date != null ? " date:  " + date : "");
    }
}
