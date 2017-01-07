package com.stt.portfolio.transactions;

public class DividendTax extends Transaction {

	@Override
	public String getOp() {
		
		return TR_DIVIDEND_TAX;
	}

	@Override
	public void process(I_BookEntryModifier modifier) {
		modifier.addDividendTax(this);

	}

}
