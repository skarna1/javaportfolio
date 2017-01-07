package com.stt.portfolio.transactions;

public class Transfer extends Transaction {

	@Override
	public String getOp() {
		
		return "SIIRTO";
	}

	@Override
	public void process(I_BookEntryModifier modifier) {
		modifier.removeCapital(this);

	}

}
