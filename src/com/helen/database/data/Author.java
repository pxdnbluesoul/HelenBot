package com.helen.database.data;

import com.helen.database.Selectable;

public class Author implements Selectable {

    private String authorName;

    public Author(String author) {
        authorName = author;
    }

    public String getAuthor() {
        return authorName;
    }

    public Object selectResource() {
        return authorName;
    }
}
