package com.stt.portfolio.gui.treetable;

import com.stt.portfolio.BookEntryItem;

public class BookEntryItemNode {

	BookEntryItem item;
	BookEntryNode parent = null;
	static Object[] items = new Object[0];
	
	public BookEntryItemNode(BookEntryItem item, BookEntryNode parent) {
		this.item = item;
		this.parent = parent;
	}

	public Object[] getChildren() {
		
		return items;
	}

	public BookEntryItem getItem() {
		return item;
	}

	public BookEntryNode getParent() {
		return parent;
	}
	
	public String toString() {
		return parent.getBookEntry().getName();
	}
	
}
