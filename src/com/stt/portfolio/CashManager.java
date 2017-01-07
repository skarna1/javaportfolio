package com.stt.portfolio;

import in.satpathy.financial.XIRR;
import in.satpathy.financial.XIRRData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CashManager {
	private double cash = 0.0;

	private List<CashItem> investedSums = new ArrayList<CashItem>();

	private List<CashItem> cashItems = new ArrayList<CashItem>();

	public void clear() {
		cash = 0.0;
		investedSums.clear();
		cashItems.clear();
	}

	public double getCash() {
		return cash;
	}

	public void addCash(double cost, Date date) {
	//	System.out.println(date + " " + cost);
		cashItems.add(new CashItem(cost, cost, date, "CASH"));
	}

	public void process() {
		Collections.sort(cashItems);
		cash = 0.0;
		for (CashItem c : cashItems) {
			if (cash >= -c.getCost()) {
				cash += c.getCost();
			} else {
				addInvestedSum(-c.getCost() - cash, c.getDate());
				cash += -c.getCost() - cash;
				cash += c.getCost();

			}
		}
	}

	public double getInvestedCapital() {
		double amount = 0.0;
		for (CashItem d : investedSums) {
			amount += d.getCost();
		}
		return amount;
	}

	public void changeInvestedCapital(double d, Date date) {
		//System.out.println(date + " " + d);
		addInvestedSum(d, date);
		addCash(d, date);
	}

	/**
	 * Gets the Internal Rate or Return of the whole portfolio. Openoffice and
	 * Excel have similar xirr function.
	 * 
	 * @return the xirr
	 */
	public double getXirr(double currentValue, Date currentDate) {
		Collections.sort(investedSums);
		List<CashItem> items = getCashItemsBefore(currentDate);
		int size = items.size() + 1;
		double[] values = new double[size];
		double[] dates = new double[size];
		int i = 0;
		for (CashItem c : items) {

			values[i] = -c.getCost();
			dates[i] = XIRRData.getExcelDateValue(c.getDate());

			// System.out.println("date: " + cal.getTime() + " cost: " +
			// values[i]);
			i++;

		}
		values[i] = currentValue;
		dates[i] = XIRRData.getExcelDateValue(currentDate);
		// System.out.println("date: " + cal.getTime() + " cost: " + values[i]);

		XIRRData data = new XIRRData(i + 1, 0.002, values, dates);
		double xirrValue = XIRR.xirr(data);

		return xirrValue;
	}

	public double getXirrBetweenDates(double startValue, Date startDate,
			double currentValue, Date currentDate) {
		Collections.sort(investedSums);
		List<CashItem> items = getCashItemsBetween(startDate, currentDate);
		int size = items.size() + 1 + 1;
		double[] values = new double[size];
		double[] dates = new double[size];
		int i = 0;

		values[i] = -startValue;
		dates[i] = XIRRData.getExcelDateValue(startDate);
		i++;
		for (CashItem c : items) {

			values[i] = -c.getCost();
			dates[i] = XIRRData.getExcelDateValue(c.getDate());

			// System.out.println("date: " + cal.getTime() + " cost: " +
			// values[i]);
			i++;
		}

		values[i] = currentValue;
		dates[i] = XIRRData.getExcelDateValue(currentDate);
		// System.out.println("date: " + cal.getTime() + " cost: " + values[i]);

		XIRRData data = new XIRRData(i + 1, 0.002, values, dates);
		double xirrValue = XIRR.xirr(data);

		return xirrValue;
	}

	private List<CashItem> getCashItemsBetween(Date startDate, Date endDate) {
		List<CashItem> list = new ArrayList<CashItem>();
		for (CashItem c : investedSums) {
			if (endDate.after(c.getDate()) && startDate.before(c.getDate())) {
				list.add(c);
			}

		}

		return list;
	}

	private List<CashItem> getCashItemsBefore(Date endDate) {
		List<CashItem> list = new ArrayList<CashItem>();
		for (CashItem c : investedSums) {
			if (endDate.after(c.getDate())) {
				list.add(c);
			}

		}

		return list;
	}

	private void addInvestedSum(double d, Date date) {
		investedSums.add(new CashItem(d, d, date, "INVESTED"));

	}
}
