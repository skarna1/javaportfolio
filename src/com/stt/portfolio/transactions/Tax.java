package com.stt.portfolio.transactions;

public class Tax extends Transaction {

	@Override
	public String getOp() {
		
		return TR_TAX;
	}

	@Override
	public void process(I_BookEntryModifier modifier) {
		modifier.addCash(this);

	}

}
