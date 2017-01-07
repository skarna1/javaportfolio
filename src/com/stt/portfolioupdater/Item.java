package com.stt.portfolioupdater;

import java.util.Calendar;
import java.util.Date;

public class Item {

	String name;
	String ticker; // Stock ticker
	double high; // highest price on the day
	double low; // lowest price on the day
	double last; // Last stock price
	long volume; // Turnover, vaihto
	double rate = 1.0000; // exchange rate
	Date givenDate = null;
	int decimals = 2;
	/*
	 * Date in the format DDMMYYYY This is the date of the updated stock quotes.
	 * Same for all Tickers.
	 */
	static String date;

	static {
		Calendar c = Calendar.getInstance();
		handleWeekend(c);
		date = String.format("%1$td%1$tm%1$tY", c);
	}

	/*
	 * If updating takes place on a weekend, then stock quotes are probably from
	 * last Friday. Adjust date accordingly.
	 */
	private static void handleWeekend(Calendar c) {
		int dayofweek = c.get(Calendar.DAY_OF_WEEK);
		if (dayofweek == Calendar.SATURDAY) {
			// System.out.println("saturday");
			c.add(Calendar.DAY_OF_MONTH, -1);
		} else if (dayofweek == Calendar.SUNDAY) {
			c.add(Calendar.DAY_OF_MONTH, -2);
		}
	}

	public String getDate() {
		if (givenDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(givenDate);
			handleWeekend(c);
			return String.format("%1$td%1$tm%1$tY", c);
		}

		return date;
	}

	public String formatNDecimals(double d, int n) {

		String formatted = String.format("%1$." + n + "f", d).replace(',', '.');

		return formatted;
	}
	
	public void setValues(String ticker, double last, double high, double low, long volume, Date date, double rate) {
		setTicker(ticker);
		setLast(last);
		setHigh(high);
		setLow(low);

		//System.out.println("volume: " + volume + " " + volumestr+ ".");
		setVolume(volume);
		setDecimals(4);
		setGivenDate(date);
		setRate(rate);
	}

	String getLine() {
		String line = getDate() + "," + formatNDecimals(high, decimals) + ","
				+ formatNDecimals(low, decimals) + ","
				+ formatNDecimals(last, decimals) + "," + volume;

		// Write rate only if it exists
		if (rate != 1.0000) {
			line += "," + formatNDecimals(rate, 5);
		}
		line += "\r\n";
		return line;
	}

	public void print() {
		System.out.println(getLine());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public double getLast() {
		return last;
	}

	public void setLast(double last) {
		this.last = last;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public Date getGivenDate() {
		return givenDate;
	}

	public void setGivenDate(Date givenDate) {
		this.givenDate = givenDate;
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

}
