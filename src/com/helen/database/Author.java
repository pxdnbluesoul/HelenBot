package com.helen.database;

public class Author implements Selectable {

	private String authorName = null;
	
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
