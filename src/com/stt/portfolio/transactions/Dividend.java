package com.stt.portfolio.transactions;

public class Dividend extends Transaction {

	@Override
	public void process(I_BookEntryModifier modifier) {
		modifier.addDividend(this);

	}
	public String getOp() {
		return "OSINKO";
	}
}
