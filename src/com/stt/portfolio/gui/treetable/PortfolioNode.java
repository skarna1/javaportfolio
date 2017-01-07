package com.stt.portfolio.gui.treetable;

import java.util.List;

import com.stt.portfolio.BookEntry;

public class PortfolioNode {

	BookEntryNode[] bookEntries = null;
	
	public PortfolioNode(List<BookEntry> bookEntryList) {
		
		bookEntries= new BookEntryNode[bookEntryList.size()];
		int i = 0;
		for (BookEntry be : bookEntryList) {
			bookEntries[i++] = new BookEntryNode(be);
		}		
	}
	
	public Object[] getChildren() {
		
		return bookEntries;
	}
	public String toString() {
		return "";
	}
	
	public Class getColumnClass(int column) {
		System.out.println("column " + column + " " + bookEntries[0].getColumnClass(column));
		return bookEntries[0].getColumnClass(column);
	}
	
}
