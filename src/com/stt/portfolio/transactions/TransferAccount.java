package com.stt.portfolio.transactions;

public class TransferAccount extends Transaction {

	String oldBroker;
	
	
	public String getOldBroker() {
		return oldBroker;
	}

	public void setOldBroker(String oldBroker) {
		this.oldBroker = oldBroker;
	}

	@Override
	public String getOp() {
		
		return Transaction.TR_ACCOUNT_TRANSFER;
	}

	@Override
	public void process(I_BookEntryModifier modifier) {
		

	}
	
	@Override
	public  String getLineContent() {
		return super.getLineContent() + 
		oldBroker + SEP ;
		
	}

}
