package com.helen.search;

import com.helen.commands.CommandData;
import com.helen.database.Selectable;

public class WikipediaAmbiguous implements Selectable {

    private CommandData data;
    private String title;

    public WikipediaAmbiguous(CommandData data, String title) {
        this.data = data;
        this.title = title;
    }

    public CommandData getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public Object selectResource() {
        return title;
    }
}
