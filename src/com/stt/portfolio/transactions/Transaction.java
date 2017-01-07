package com.stt.portfolio.transactions;

import java.util.Calendar;
import java.util.Date;

public abstract class Transaction implements Comparable {

	public static String TR_ACCOUNT_TRANSFER = "TILISIIRTO";
	public static String TR_DIVIDEND_TAX = "OSINKOVERO";
	public static String TR_TAX = "VERO";
	
	protected static final String SEP = " ; ";
	
	Date date;
	double price;
	double amount;
	double cost;
	double brokerCost;
	double rate = 1.000;
	String broker;
	String ticker;
	String name;
	int decimals = 2;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBrokerCost() {
		return brokerCost;
	}

	public void setBrokerCost(double brokerCost) {
		this.brokerCost = brokerCost;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	@Override
	public int compareTo(Object o) {
		Transaction t = (Transaction) o;
		return (getDate().compareTo(t.getDate()));

	}

	public String getLine() {
		return getLineContent() + "\r\n";
	}

	public  String getLineContent() {
		return getDateString(getDate()) + SEP + getOp() + SEP + getTicker() + SEP + getName() + SEP + getAmount() +
		SEP + round(getPrice()) + SEP + getBroker() + SEP + round(getBrokerCost()) + SEP + round(getCost()) + SEP + formatNDecimals(getRate(), 6) + SEP ;
	}
	
	protected String getDateString(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		return String.format("%1$td.%1$tm.%1$tY", c);
	}
	
	protected String round(double d) {
		
		return formatNDecimals(d, decimals);
	}
	
	public String formatNDecimals(double d, int n) {
		
		String formatted = String.format("%1$." + n + "f", d);
				
		return formatted;
	}
	
	public abstract void process(I_BookEntryModifier modifier);

	public abstract String getOp();
	
	
	
}
