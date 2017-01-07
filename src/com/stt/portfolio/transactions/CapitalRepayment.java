package com.stt.portfolio.transactions;

public class CapitalRepayment extends Transaction {

	@Override
	public String getOp() {
		return "PÄÄOMAN PALAUTUS";
	}

	@Override
	public void process(I_BookEntryModifier modifier) {
		
		modifier.addCapitalRepayment(this);
	}

}
