package com.stt.portfolio.transactions;

public class Sell extends Transaction {

	
	public void process(I_BookEntryModifier modifier) {
		modifier.sell(this);
	}

	public String getOp() {
		return "MYYNTI";
	}
}
