package com.stt.portfolioupdater;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

public class KauppalehtiJsonExchangerateFetcher extends HTTPDocumentFetcher {

	static final String uri = "https://www.kauppalehti.fi/porssi/valuutat/";

	static Map<String, Double> rates = new HashMap<>();

	public double getExchangeRate(String ccy) {
		if (!rates.containsKey(ccy)) {
			parseHtmlCcy(uri);
		}
		Double r = rates.get(ccy);
		if (r == null) {
			return 1.0;
		}
		return r;
	}

	private double parseHtmlCcy(String uri) {
		try {
			InputStream in = fetch(uri);

			if (in != null) {
				// System.out.println(stringToSearch);
				// specify that we want to search for two groups in the string
				// "name":"Outokumpu","symbol":"OUT1V","isin":"FI0009002422",
				// "tradeCurrency":"EUR","lastPrice":8.56
				String contents = convertStreamToString(in);

				in.close();

				Pattern p = Pattern.compile("\"symbol\":\"([^\"]*)\","
						+ "\"marketName\":\"([^\"]*)\",.*?"
						+ "\"lastPrice\":([0-9.]*),.*?\"dateTime\":\"([^\"]*)\",");
				Matcher m = p.matcher(contents);
				// List<String> allMatches = new ArrayList<String>();
				// if our pattern matches the string, we can try to extract our groups
				while (m.find()) {
					String ccy = m.group(1);
					String lastPrice = m.group(3);
					//String date = m.group(4);
					String prefix = "EUR";
					if (ccy.startsWith(prefix)) {
						ccy = ccy.substring(prefix.length());
					}
					rates.put(ccy, Double.parseDouble(lastPrice));
					//System.out.println(ccy + " " + lastPrice);
				}
			}
		} catch (IOException e) {
			System.out.println("ioexception");
			e.printStackTrace();
		}
		return 1.0;
	}

	private static String convertStreamToString(java.io.InputStream is) {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static void main(String[] args) {
		KauppalehtiJsonExchangerateFetcher fetcher = new KauppalehtiJsonExchangerateFetcher();
		double rate = fetcher.getExchangeRate("SEK");
		System.out.println("SEK Rate: " + rate);

		rate = fetcher.getExchangeRate("USD");
		System.out.println("USD Rate: " + rate);
		
		rate = fetcher.getExchangeRate("CAD");
		System.out.println("CAD Rate: " + rate);
		
		rate = fetcher.getExchangeRate("NOK");
		System.out.println("NOK Rate: " + rate);
	}

}
