package com.stt.portfolio.transactions;

import java.util.Date;

public abstract class ChangeTransaction {
	String symbol;
	Date date;
	
	
	public abstract void process(I_BookEntryManagement bookEntryManager);
	
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
