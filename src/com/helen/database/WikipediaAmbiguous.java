package com.helen.database;

import com.helen.commands.CommandData;

public class WikipediaAmbiguous implements Selectable {

  private CommandData data;
  private String title;

  public WikipediaAmbiguous(CommandData data, String title){
    this.data = data;
    this.title = title;
  }

  public CommandData getData(){
    return data;
  }

  public String getTitle(){
    return title;
  }

  public Object selectResource(){
    return title;
  }
}
