package com.stt.portfolio.quotes;

import java.util.Date;

import com.stt.portfolio.Util;

public class Quote implements Comparable {

	private String ticker = null;
	private double last = 0.0;
	private double high = 0.0;
	private double low = 0.0;
	private double rate = 1.00;
	private int amount = 0;
	private Date date = null;
	
	
	private double getValueIfExists(String value, double defaultValue) {
		if (value.length() > 0) {
			return Double.parseDouble(value);
		}
		return defaultValue;
	}
	
	public void setHighIfAvailable(String high) {
		setHigh(getValueIfExists(high, last));
		
	}
	
	public void setLowIfAvailable(String low) {
		setLow(getValueIfExists(low, last));
	}
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public double getLast() {
		return last;
	}
	public void setLast(double last) {
		this.last = last;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getRate() {		
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}

	@Override
	public int compareTo(Object o) {
		
		return Util.compareDates(this.getDate(), ((Quote)o).getDate()); 
	}
	
}
