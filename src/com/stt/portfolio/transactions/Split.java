package com.stt.portfolio.transactions;

public class Split extends ChangeTransaction {
	double a;
	double b;
	
	public Split(double a , double b) {
		this.a = a;
		this.b = b;
	}
	
	public double getRatio() {
		return b / a;
	}
	
	
	public void process(I_BookEntryManagement bookEntryManager)
	{
		bookEntryManager.split(getSymbol(), getRatio(), getDate());
	}
}
