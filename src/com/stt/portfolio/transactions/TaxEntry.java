package com.stt.portfolio.transactions;

import java.util.Calendar;
import java.util.Date;

public class TaxEntry {
	
	private Date purchaseDate;
	private Date sellDate;
	double purchasePrice;
	double sellPrice;
	double sellCosts;
	double profit;
	String symbol;
	double amount;
	String broker;
	
	

	public double getPurchasePriceAssumption() {
		return getPurchasePricePercentage()/100.0 * getSellPrice();
	}
	
	double getPurchasePricePercentage() {
		if (isOwnedTimeOver10Years(getPurchaseDate(), getSellDate())) {
			return 40.0;
		}
		return 20.0;
	}

	boolean isOwnedTimeOver10Years(Date purchaseDate, Date sellDate) {

		int yearLimit = 10;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(purchaseDate);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(sellDate);

		int years = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);

		if (years < yearLimit) {
			return false;
		}
		if (years > yearLimit) {
			return true;
		}

		int months = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		if (months < 0) {
			return false;
		}
		if (months > 0) {
			return true;
		}

		int days = c2.get(Calendar.DATE) - c1.get(Calendar.DATE);

		if (days < 0) {
			return false;
		}

		return true;

	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Date getSellDate() {
		return sellDate;
	}

	public void setSellDate(Date sellDate) {
		this.sellDate = sellDate;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public double getSellCosts() {
		return sellCosts;
	}

	public void setSellCosts(double sellCosts) {
		this.sellCosts = sellCosts;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}
	

}
