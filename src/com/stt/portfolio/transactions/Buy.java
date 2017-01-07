package com.stt.portfolio.transactions;



public class Buy extends Transaction {
	
	
	public void process(I_BookEntryModifier modifier) {
		modifier.buy(this);
	}

	
	public String getOp() {
		return "OSTO";
	}
	
	
}
