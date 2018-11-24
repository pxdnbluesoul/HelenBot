package com.helen.database.entities;

import com.helen.database.framework.Selectable;

public class Author implements Selectable {

	private String authorName;
	
	public Author(String author){
		authorName = author;
	}
	
	public String getAuthor(){
		return authorName;
	}
	
	public Object selectResource(){
		return authorName;
	}
}
