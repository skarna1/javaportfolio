package com.stt.portfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stt.portfolio.transactions.TaxEntry;


/**
 * The Class TaxManager.
 */
public class TaxManager {

	/*
	 * Collection of all tax entries
	 */
	/** The tax entries. */
	private List<TaxEntry> taxEntries = new ArrayList<TaxEntry>();
	
	/** The dividends received from different countries from different years*/
	Map<String, Map<Integer, Double> > dividents = new HashMap<String, Map<Integer, Double> >();
	
	/** The dividend taxes paid to different countries from different years*/
	Map<String, Map<Integer, Double> > dividendTaxes = new HashMap<String, Map<Integer, Double> >();
	
	/**
	 * Clear.
	 */
	public void clear() {
		taxEntries.clear();
	}
	
	/**
	 * Gets the tax entries.
	 * 
	 * @param year the year
	 * 
	 * @return the tax entries
	 */
	public List<TaxEntry> getTaxEntries(int year) {
		// Find entries of the year
		List<TaxEntry> yearTaxEntries = new ArrayList<TaxEntry>();
		Calendar c = Calendar.getInstance();
		for (TaxEntry e : taxEntries) {
			c.setTime(e.getSellDate());
			if (c.get(Calendar.YEAR) == year) {
				yearTaxEntries.add(e);
			}
		}
		return yearTaxEntries;
	}
	
	/**
	 * Gets the tax report years.
	 * 
	 * @return the tax report years
	 */
	public Integer[] getTaxReportYears() {

		Set<Integer> yearSet = new HashSet<Integer>();
		Calendar c = Calendar.getInstance();
		for (TaxEntry e : taxEntries) {
			c.setTime(e.getSellDate());
			int year = c.get(Calendar.YEAR);
			yearSet.add(year);
		}

		Integer[] years = yearSet.toArray(new Integer[yearSet.size()]);
		Arrays.sort(years);
		return years;
	}

	/**
	 * Adds the all.
	 * 
	 * @param taxEntries2 the tax entries2
	 */
	public void addAll(List<TaxEntry> taxEntries2) {
		taxEntries.addAll(taxEntries2);
		
	}
	
	public void add(TaxEntry taxEntry) {
		taxEntries.add(taxEntry);
		
	}

	/**
	 * Gets the combined tax report table.
	 * 
	 * @param year the year
	 * @param tickerManager the ticker manager
	 * 
	 * @return the combined tax report table
	 */
	public Object[][] getCombinedTaxReportTable(I_TickerManager tickerManager, int year) {

		int i = 0;

		List<TaxEntry> yearTaxEntries = getTaxEntries(year);

		// Build table
		Object[][] table = new Object[yearTaxEntries.size()][13];

		for (TaxEntry e : yearTaxEntries) {

			int j = 0;

			Stock stock = tickerManager.getStock(e.getSymbol());
			table[i][j++] = (stock != null) ? stock.getName() : e.getSymbol();

			table[i][j++] = new Double(e.getAmount());

			table[i][j++] = e.getPurchaseDate();
			table[i][j++] = e.getSellDate();

			table[i][j++] = new Double(e.getPurchasePrice());
			table[i][j++] = new Double(0.0);
			table[i][j++] = new Double(e.getSellPrice());
			table[i][j++] = new Double(e.getSellCosts());

			table[i][j++] = e.getBroker();
			if (e.getPurchasePriceAssumption() > e.getPurchasePrice()) {
				table[i][j++] = new Double(e.getPurchasePriceAssumption()); // hankintameno-olettama
				table[i][j++] = new Double(e.getSellPrice()
						- e.getPurchasePriceAssumption());
			} else {
				table[i][j++] = new Double(0.0);
				table[i][j++] = new Double(e.getProfit());
			}

			++i;

		}
		//printDividents();
		//printDividendTaxes();
		return table;
	}
	
	
	
	public void addDivident(String country, Date date, double divident) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		double oldValue = 0.0;
		Map<Integer, Double> d = null;
		if (dividents.containsKey(country)) {
			d = dividents.get(country);
		}
		else {
			d = new HashMap<Integer, Double>();
			dividents.put(country, d);
		}
	
		if (d.containsKey(year)) {
			oldValue = d.get(year);
		}
		d.put(year, oldValue + divident);

	}
	public void addDividentTax(String country, Date date, double divident) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		double oldValue = 0.0;
		Map<Integer, Double> d = null;
		if (dividendTaxes.containsKey(country)) {
			d = dividendTaxes.get(country);
		}
		else {
			d = new HashMap<Integer, Double>();
			dividendTaxes.put(country, d);
		}
	
		if (d.containsKey(year)) {
			oldValue = d.get(year);
		}
		d.put(year, oldValue + divident);

	}
	private void printDividents() {
		Set<String> countries = dividents.keySet();
		Iterator<String> ci = countries.iterator();
		while(ci.hasNext()) {
			String country = ci.next();
			System.out.println("Dividents from " + country);
			
			Map<Integer, Double> d = dividents.get(country);
			Set<Integer> years = d.keySet();
			Iterator<Integer> yi = years.iterator();
			while (yi.hasNext()) {
				Integer year = yi.next();
				Double divident = d.get(year);
				System.out.println("Year: " + year + " divident: " + divident + " tulo: "  + divident * 0.7);
			}
		}
	}
	
	private void printDividendTaxes() {
		Set<String> countries = dividendTaxes.keySet();
		Iterator<String> ci = countries.iterator();
		while(ci.hasNext()) {
			String country = ci.next();
			System.out.println("Dividend taxes for " + country);
			
			Map<Integer, Double> d = dividendTaxes.get(country);
			Set<Integer> years = d.keySet();
			Iterator<Integer> yi = years.iterator();
			while (yi.hasNext()) {
				Integer year = yi.next();
				Double divident = d.get(year);
				System.out.println("Year: " + year + " dividend tax: " + divident );
			}
		}
	}
	
}
