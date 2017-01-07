package com.stt.portfolio.transactions;

import java.util.Date;

public interface I_BookEntryManagement {
	
	public void renameBookEntry(String symbol, String newSymbol, Date date);
	
	public void spinoffBookEntry(String symbol, String newSymbol, double ratio, double stockratio, boolean storeParent);
	
	public void removeBookEntry(String symbol);
	
	public void split(String symbol, double ratio, Date date);
	
	
}
