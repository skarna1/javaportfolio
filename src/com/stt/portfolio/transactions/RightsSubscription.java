package com.stt.portfolio.transactions;

import java.util.Calendar;

public class RightsSubscription extends Transaction {

	private double amountOfOldOwnership = 0.0;
	private double ratio;
	
	Calendar c = Calendar.getInstance();
	
	public RightsSubscription() {
		c.set(Calendar.YEAR, 2005);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DATE, 1);
	}
	
	@Override
	public String getOp() {

		return "MERKINTÄ_OIKEUKSILLA";
	}

	@Override
	public void process(I_BookEntryModifier modifier) {
		modifier.subscribe(this);

	}

	public double getAmountOfOldOwnership() {
		
		return amountOfOldOwnership;
	}

	public void setAmountOfOldOwnership(double amountOfOldOwnership) {
		this.amountOfOldOwnership = amountOfOldOwnership;
	}

	public boolean isCostShared() {
		return c.getTime().after(getDate());
	}

	

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	
	@Override
	public  String getLineContent() {
		return super.getLineContent() + 
		 formatNDecimals(getAmountOfOldOwnership(), 2) + SEP + formatNDecimals(getRatio(), 8) + SEP  ;
	}

}
