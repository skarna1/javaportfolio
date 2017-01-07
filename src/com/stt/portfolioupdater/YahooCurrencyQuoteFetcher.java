package com.stt.portfolioupdater;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class YahooCurrencyQuoteFetcher {
	public static String charsetName = "ISO-8859-1";
	public static Locale usLocale = new Locale("en", "US");
	public static String baseuri = "http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=EURXYZ=X";

	static Map<String, Double> rates = new HashMap<String, Double>();

	public double getExchangeRate(String ccy) {
		if (!rates.containsKey(ccy)) {
			String uri = baseuri.replace("XYZ", ccy);
			double rate = parseHtmlCcy(uri);
			if (rate > 0.0) {
				rates.put(ccy, rate);
			}
		}
		return rates.get(ccy);
	}

	private double parseHtmlCcy(String uri) {
		double rate = 0.0;

		try {
			String input = YahooUtils.readInput(uri);
			String[] fields = input.split(",");
			if (fields.length > 1) {
				rate = Double.parseDouble(fields[1]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rate;
	}
	

	public static void main(String[] args) {
		YahooCurrencyQuoteFetcher fetcher = new YahooCurrencyQuoteFetcher();
		String[] currencies = { "SEK", "USD" };
		for (String currency : currencies) {
			double rate = fetcher.getExchangeRate(currency);
			System.out.println(currency + " rate: " + rate);

		}
	}

}
