package com.helen.database;

import java.util.ArrayList;

public class Tag {
	
	ArrayList<Page> pagesContainingTag;
	String tagName;
	
	public Tag(String tagName){
		this.tagName = tagName;
		pagesContainingTag = new ArrayList<Page>();
	}
	
	public ArrayList<Page> getPages(){
		return pagesContainingTag;
	}
	
	public void addPage(Page p){
		pagesContainingTag.add(p);
	}
	
}
