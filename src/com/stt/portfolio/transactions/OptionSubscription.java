package com.stt.portfolio.transactions;



public class OptionSubscription extends Transaction {
	
	String optionTicker;
	double subscriptionRatio = 1.0;  // one share requires x options, subscriptionRatio = x
	
	
	
	public void process(I_BookEntryModifier modifier) {
		modifier.optionSubscribe(this);
	}

	
	public String getOp() {
		return "MERK.OIKEUS";
	}


	public String getOptionTicker() {
		return optionTicker;
	}


	public void setOptionTicker(String optionTicker) {
		this.optionTicker = optionTicker;
	}


	public double getSubscriptionRatio() {
		return subscriptionRatio;
	}


	public void setSubscriptionRatio(double subscriptionRatio) {
		this.subscriptionRatio = subscriptionRatio;
	}
	
	@Override
	public  String getLineContent() {
		return super.getLineContent() + 
		 getOptionTicker() + SEP + getSubscriptionRatio() + SEP  ;
	}
}
