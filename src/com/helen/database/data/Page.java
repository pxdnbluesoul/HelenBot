package com.helen.database.data;

import com.helen.database.Selectable;

public class Page implements Selectable {


    private String pageLink;
    private String title;
    private Integer rating;
    private String createdBy;
    private java.sql.Timestamp createdAt;
    private Boolean scpPage;
    private String scpTitle;

    public Page(String pageLink, String title, Boolean scpPage, String scpTitle) {
        this.pageLink = pageLink;
        this.title = title;
        this.scpPage = scpPage;
        this.scpTitle = scpTitle;
    }

    public Page(String pageLink, String title, Integer rating, String createdBy,
                java.sql.Timestamp createdAt, Boolean scpPage, String scpTitle) {
        this.pageLink = pageLink;
        this.title = title;
        this.rating = rating;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.scpPage = scpPage;
        this.scpTitle = scpTitle;
    }

    public String getPageLink() {
        return pageLink;
    }

    public String getTitle() {
        return title;
    }

    public Integer getRating() {
        return rating;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public Boolean getScpPage() {
        return scpPage;
    }

    public String getScpTitle() {
        return scpTitle;
    }

    @Override
    public Object selectResource() {
        return pageLink;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageLink='" + pageLink + '\'' +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", scpPage=" + scpPage +
                ", scpTitle='" + scpTitle + '\'' +
                '}';
    }

}
