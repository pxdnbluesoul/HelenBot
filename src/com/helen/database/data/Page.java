package com.helen.database.data;

import com.helen.database.Selectable;
import org.jibble.pircbot.Colors;

import static com.helen.database.data.Pages.findTime;

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

    public String getCreatedBy() {
        return createdBy;
    }
    
    public String getPageAsLine(){
        StringBuilder returnString = new StringBuilder();
        returnString.append(Colors.BOLD);

        if(this.getScpPage() && !this.getScpTitle().equals("[ACCESS DENIED]")){
            returnString.append(this.getTitle() + ": " + this.getScpTitle());
        }else{
            returnString.append(this.getTitle());
        }

        returnString.append(Colors.NORMAL);
        returnString.append(" (");
        returnString.append("Rating: ");
        returnString.append(this.getRating());
        returnString.append(". ");
        returnString.append("Written ");
        returnString.append(findTime(this.getCreatedAt().getTime()));
        returnString.append("By: ");
        returnString.append(this.getCreatedBy());
        returnString.append(")");
        returnString.append(" - ");
        returnString.append("http://scp-wiki.wikidot.com/");
        returnString.append(this.getPageLink());
        return returnString.toString();
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
