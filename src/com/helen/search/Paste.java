package com.helen.search;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.ZonedDateTime;


@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement
public class Paste extends BasePaste {

    /**
     * Key to the Paste
     */
    @XmlElement(name = "paste_key")
    private String key;

    /**
     * Title of the paste
     */
    @XmlElement(name = "paste_title")
    private String title;

    /**
     * Size in bytes of the paste.
     */
    @XmlElement(name = "paste_size")
    private long size;

    /**
     * URL for acessing the paste.
     */
    @XmlElement(name = "paste_url")
    private String url;

    /**
     * How many times this paste was viewed.
     */
    @XmlElement(name = "paste_hits")
    private long hits;

    /**
     * Visibility of the paste.
     */
    @XmlElement(name = "paste_private")
    private String visibility;

    /**
     * How long to the paste expire.
     */
    private String expiration;

    /**
     * Syntax highlight.
     */
    @XmlElement(name = "paste_format_short")
    private String highLight;

    /**
     * The content
     */
    private String content;


    public Paste() {
    }

    /**
     * <p>Sets a new unique key.</p>
     * <p>Beware, this information is fetched by the API, doens't set it manually unless you know what you are doing.</p>
     *
     * @param key the new key.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return Paste's title.
     */
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }


    public ZonedDateTime getLocalExpirationDate() {
        return localExpirationDate;
    }

    public ZonedDateTime getLocalPasteDate() {
        return localPasteDate;
    }

    @Override
    public String toString() {
        return "Paste{" + "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", highLight=" + highLight +
                ", visibility=" + visibility +
                ", expiration=" + expiration +
                ", localExpirationDate=" + getLocalExpirationDate() +
                ", localPasteDate=" + getLocalPasteDate() +
                ", url='" + url + '\'' +
                ", size=" + size +
                ", hits=" + hits +
                '}';
    }


}

