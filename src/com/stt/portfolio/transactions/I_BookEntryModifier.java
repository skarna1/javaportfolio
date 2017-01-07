package com.stt.portfolio.transactions;

import java.util.List;

import com.stt.portfolio.BookEntryItem;

public interface I_BookEntryModifier {

	public void spinoff(double ratio, double stockratio, List<BookEntryItem> items);
	
	public void buy(Transaction t);
	
	public void sell(Transaction t);
	
	public void addDividend(Transaction t);
	
	public void addDividendTax(Transaction t);
	
	public void subscribe(RightsSubscription t);
	
	public void optionSubscribe(OptionSubscription t);
	
	public void addCash(Transaction t);
	
	public void removeCapital(Transaction t);
	
	public void addCapitalRepayment(Transaction t);
	
}
