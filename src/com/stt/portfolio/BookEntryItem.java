package com.stt.portfolio;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * The Class BookEntryItem.
 */
public class BookEntryItem implements Comparable<BookEntryItem> {

	/** The purchase date. */
	Date purchaseDate;
	
	/** The tax purchase date. */
	Date taxPurchaseDate; // Purchase date for tax calculation purposes
	
	/** The cost. */
	double cost; // total cost
	
	/** The tax cost. */
	double taxCost; // total cost for tax calculation, hankintahinta
	
	/** The amount. */
	double amount; // number of shares
	
	/** The dividends. */
	List<CashItem> dividents = new ArrayList<CashItem>();
	
	/** The cost in original currency. */
	double costInOriginalCurrency;
	
	/** The rate on purchase date. */
	double rateOnPurchaseDate;
	
	/** The parent. */
	BookEntryItem parent = null;

	/**
	 * Instantiates a new book entry item.
	 */
	public BookEntryItem() {
		this.purchaseDate = null;
		this.cost = 0.0;
		this.amount = 0.0;
		this.taxCost = 0.0;
		this.taxPurchaseDate = null;
		this.rateOnPurchaseDate = 1.000;
		this.costInOriginalCurrency = 0.0;
		this.parent = null;
	}

	/**
	 * Gets the tax purchase date.
	 * 
	 * @return the tax purchase date
	 */
	public Date getTaxPurchaseDate() {
		return taxPurchaseDate;
	}

	/**
	 * Sets the tax purchase date.
	 * 
	 * @param taxPurchaseDate the new tax purchase date
	 */
	public void setTaxPurchaseDate(Date taxPurchaseDate) {
		this.taxPurchaseDate = taxPurchaseDate;
	}

	/**
	 * Gets the tax cost.
	 * 
	 * @return the tax cost
	 */
	public double getTaxCost() {
		return taxCost;
	}

	/**
	 * Sets the tax cost.
	 * 
	 * @param taxCost the new tax cost
	 */
	public void setTaxCost(double taxCost) {
		this.taxCost = taxCost;
	}

	/**
	 * Instantiates a new book entry item.
	 * 
	 * @param i the i
	 */
	public BookEntryItem(BookEntryItem i) {
		this.purchaseDate = i.purchaseDate;
		this.cost = i.cost;
		this.amount = i.amount;
		this.dividents = new ArrayList<CashItem>();
		for (CashItem ci : i.dividents) {
			this.dividents.add(new CashItem(ci));
		}
		this.taxCost = i.taxCost;
		this.taxPurchaseDate = i.purchaseDate;
		this.rateOnPurchaseDate = i.rateOnPurchaseDate;
		this.costInOriginalCurrency = i.costInOriginalCurrency;
		this.parent = null;
	}

	/**
	 * Gets the purchase date.
	 * 
	 * @return the purchase date
	 */
	public Date getPurchaseDate() {
		return purchaseDate;
	}

	/**
	 * Sets the purchase date.
	 * 
	 * @param purchaseDate the new purchase date
	 */
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	/**
	 * Gets the cost.
	 * 
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Sets the cost.
	 * 
	 * @param cost the new cost
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Gets the amount.
	 * 
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 * 
	 * @param amount the new amount
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	/**
	 * Adds the amount.
	 * 
	 * @param amount the amount
	 */
	public void addAmount(double amount) {
		this.amount += amount;
	}

	/**
	 * Gets the cost in original currency.
	 * 
	 * @return the cost in original currency
	 */
	public double getCostInOriginalCurrency() {
		return costInOriginalCurrency;
	}

	/**
	 * Sets the cost in original currency.
	 * 
	 * @param costInOriginalCurrency the new cost in original currency
	 */
	public void setCostInOriginalCurrency(double costInOriginalCurrency) {
		this.costInOriginalCurrency = costInOriginalCurrency;
	}

	/**
	 * Gets the rate on purchase date.
	 * 
	 * @return the rate on purchase date
	 */
	public double getRateOnPurchaseDate() {
		return rateOnPurchaseDate;
	}

	/**
	 * Sets the rate on purchase date.
	 * 
	 * @param rateOnPurchaseDate the new rate on purchase date
	 */
	public void setRateOnPurchaseDate(double rateOnPurchaseDate) {
		this.rateOnPurchaseDate = rateOnPurchaseDate;
	}

	/**
	 * Gets the dividents.
	 * 
	 * @return the dividents
	 */
	public double getDividents() {
		double cash = 0.0;
		for (CashItem i : dividents) {
			cash += i.getCost();
			
		}
		return cash;
	}

	/**
	 * Adds the divident.
	 * 
	 * @param divident the divident
	 */
	public void addDivident(CashItem divident) {
		
		dividents.add(divident);
		
	}

	/**
	 * Gets the price of one share in euros.
	 * 
	 * @return the price
	 */
	public double getPrice() {
		if (getAmount() != 0.0) {
			return getCost() / getAmount();
		} else {
			return 0.0;
		}
	}

	/**
	 * Gets the profit.
	 * 
	 * @param currentPrice the current price of one share in euros
	 * 
	 * @return the profit
	 */
	public double getProfit(double currentPrice) {
		return currentPrice * amount + getCost() + getDividents();
	}

	/**
	 * Gets the profit percent.
	 * 
	 * @param currentPrice the current price
	 * 
	 * @return the profit percent
	 */
	public double getProfitPercent(double currentPrice) {
		if (getCost() != 0.0)
			return 100.0 * getProfit(currentPrice) / -getCost();
		else
			return 0.0;
	}
	
	/**
	 * Prints the.
	 */
	public void print() {

		System.out.println("Date: " + purchaseDate);
		System.out.println("Tax Date: " + taxPurchaseDate);
		System.out.println("Amount: " + amount);
		System.out.println("Cost: " + cost);
		System.out.println("Tax Cost: " + taxCost);
		System.out.println("dividends: " + getDividents());
		System.out.println();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BookEntryItem o) {
		return (purchaseDate.compareTo(o.purchaseDate));

	}

	/**
	 * Spinoff.
	 * 
	 * @param ratio the ratio
	 * @param stockratio 
	 */
	public void spinoff(double ratio) {
		setCost(getCost() * ratio);
		setTaxCost(getTaxCost() * ratio);
		setCostInOriginalCurrency(getCostInOriginalCurrency() * ratio);

		adjustCashItems(ratio);

	}

	/**
	 * Sell partial.
	 * 
	 * @param remainingRatio the remaining ratio
	 */
	public void sellPartial(double remainingRatio) {
		setCost(getCost() * remainingRatio);
		setTaxCost(getTaxCost() * remainingRatio);
		setCostInOriginalCurrency(getCostInOriginalCurrency() * remainingRatio);
		setAmount(getAmount() * remainingRatio);
		adjustCashItems(remainingRatio);

	}

	/**
	 * Adjust cash items.
	 * 
	 * @param ratio the ratio
	 */
	public void adjustCashItems(double ratio) {
		for (CashItem i : dividents) {
			i.setCost(i.getCost() * ratio);
		}
	}

	/**
	 * Gets the cash items.
	 * 
	 * @return the cash items
	 */
	public List<CashItem> getCashItems() {

		return dividents;
	}

	/**
	 * Sets the parent.
	 * 
	 * @param i the new parent
	 */
	public void setParent(BookEntryItem i) {
		this.parent = i;

	}

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public BookEntryItem getParent() {
		return parent;
	}

	
}
