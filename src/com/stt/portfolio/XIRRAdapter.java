package com.stt.portfolio;

import in.satpathy.financial.XIRR;
import in.satpathy.financial.XIRRData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class XIRRAdapter {

	/**
	 * Gets the Internal Rate or Return. Openoffice and Excel have similar xirr
	 * function.
	 * 
	 * @return the xirr
	 */
	public double getXirr(Date quoteDate, BookEntry entry) {

		if (quoteDate == null) {
			return Double.NaN;
		}

		List<CashItem> items = new ArrayList<CashItem>();

		// System.out.println("xirr start");
		Iterator<BookEntryItem> iter = entry.getChildrenIterator();
		while(iter.hasNext()) {
            BookEntryItem e = iter.next();
			items.add(new CashItem(e.getCost(), e.getCost(), e.getPurchaseDate(), "XIRR"));

			List<CashItem> cashItems = e.getCashItems();
			for (CashItem ci : cashItems) {

				items.add(new CashItem(ci));
			}
		}

		items.add(new CashItem(entry.getMarketPrice() * entry.getAmount(), entry.getMarketPrice(), quoteDate, "XIRR"));
		Collections.sort(items);

		// System.out.println("date: " + quoteDate + " cost: " + values[i]);

		XIRRData data = new XIRRData(items.size(), 0.002,
				getDoubleArray(items), getDateArray(items));
		return XIRR.xirr(data) * 100.0;
	}
	
	
	public double getXirr(Date quoteDate, double marketPrice, BookEntryItem item) {

		if (quoteDate == null) {
			return Double.NaN;
		}

		List<CashItem> items = new ArrayList<CashItem>();

		// System.out.println("xirr start");

		items.add(new CashItem(item.getCost(), item.getCost(), item.getPurchaseDate(), "XIRR"));

		for (CashItem ci : item.getCashItems()) {

			items.add(new CashItem(ci));
		}

		items.add(new CashItem(marketPrice * item.getAmount(), marketPrice, quoteDate, "XIRR"));
		Collections.sort(items);

		// System.out.println("date: " + quoteDate + " cost: " + values[i]);

		XIRRData data = new XIRRData(items.size(), 0.002,
				getDoubleArray(items), getDateArray(items));
		return XIRR.xirr(data) * 100.0;
	}
	
	
	/**
	 * Gets the double array.
	 * 
	 * @param list
	 *            the list
	 * 
	 * @return the double array
	 */
	private double[] getDoubleArray(List<CashItem> list) {
		double[] dvalues = new double[list.size()];
		int i = 0;
		for (CashItem d : list) {
			dvalues[i++] = d.getCost();
		}
		return dvalues;
	}

	/**
	 * Gets the date array.
	 * 
	 * @param list
	 *            the list
	 * 
	 * @return the date array
	 */
	private double[] getDateArray(List<CashItem> list) {
		double[] dvalues = new double[list.size()];
		int i = 0;
		for (CashItem d : list) {
			dvalues[i++] = XIRRData.getExcelDateValue(d.getDate());
		}
		return dvalues;
	}

}
