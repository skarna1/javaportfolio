package com.stt.portfolio;

import java.util.Date;



public class CashItem implements Comparable<CashItem>  {
	Date date;
	double cost;
	double costPerShare;
	String itemType="";
	
	public CashItem(double cost, double costPershare, Date date, String itemType) {
		this.date = date;
		this.cost = cost;
		this.costPerShare = costPershare;
		this.itemType = itemType;
	}
	
	public CashItem(CashItem ci) {
		this.date = ci.date;
		this.cost = ci.cost;
		this.itemType = ci.itemType;
		this.costPerShare = ci.costPerShare;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public double getCostPerShare() {
		return costPerShare;
	}

	public void setCostPerShare(double costPerShare) {
		this.costPerShare = costPerShare;
	}

	public void print() {
		System.out.println(itemType);
		System.out.println(date);
		System.out.println(cost);
	}
	
	@Override
	public int compareTo(CashItem o) {
		return (getDate().compareTo(o.getDate()));

	}
}
