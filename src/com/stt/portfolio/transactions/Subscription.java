package com.stt.portfolio.transactions;

import java.util.Date;

/*
 * Subscription without rights, same as BUY, but costs are typically 0
 */


public class Subscription extends Transaction {
	
	Date taxPurchaseDate;
	
	public void process(I_BookEntryModifier modifier) {
		
		modifier.buy(this);
		
	}

	
	public String getOp() {
		return "MERKINTÄ";
	}
	
	@Override
	public  String getLineContent() {
		return super.getLineContent() + 
		getDateString(taxPurchaseDate) + SEP ;
	}


	public Date getTaxPurchaseDate() {
		return taxPurchaseDate;
	}


	public void setTaxPurchaseDate(Date taxPurchaseDate) {
		this.taxPurchaseDate = taxPurchaseDate;
	}
	
	
}
