package com.stt.portfolio.transactions;

public class SymbolChange extends ChangeTransaction {

	
	String newSymbol;
	
	public SymbolChange(String newSymbol) {
		this.newSymbol = newSymbol;
	}
	
	public String getNewSymbol() {
		return newSymbol;
	}

	public void setNewSymbol(String newSymbol) {
		this.newSymbol = newSymbol;
	}

	
	public void process(I_BookEntryManagement bookEntryManager)
	{
		bookEntryManager.renameBookEntry(getSymbol(), getNewSymbol(), getDate());
	}
}
