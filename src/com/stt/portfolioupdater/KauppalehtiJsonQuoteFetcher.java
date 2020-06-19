package com.stt.portfolioupdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class KauppalehtiJsonQuoteFetcher extends HTTPQuoteFetcher {

	public KauppalehtiJsonQuoteFetcher() {
		super();
	}

	public KauppalehtiJsonQuoteFetcher(String uri) {
		super(uri);
	}

	public List<Item> parseHtml() {
		List<Item> items = new ArrayList<>();
		try {
			
			InputStream in = new ByteArrayInputStream(fetch(uri).getBytes());

			if (in != null) {
				// System.out.println(stringToSearch);
				// specify that we want to search for two groups in the string
				// "name":"Outokumpu","symbol":"OUT1V","isin":"FI0009002422",
				// "tradeCurrency":"EUR","lastPrice":8.56,"dayLowPrice":3.57,"dayHighPrice":3.681
				// "quantity":3400763
				String contents = convertStreamToString(in);

				in.close();

				Pattern p = Pattern.compile("name\":\"([^\"]*)\",\"symbol\":\"([^\"]*)\","
						+ "\"isin\":\"([^\"]*)\",\"tradeCurrency\":\"([^\"]*)\","
						+ "\"lastPrice\":([0-9.]*),.*?\"quantity\":([0-9]*),.*?"
						+ "\"dateTime\":\"([^\"]*)\",.*?\"dayLowPrice\":([0-9.]*),"
						+ "\"dayHighPrice\":([0-9.]*)");
				Matcher m = p.matcher(contents);
				// List<String> allMatches = new ArrayList<String>();
				// if our pattern matches the string, we can try to extract our groups
				while (m.find()) {
					String name = m.group(1);
					String ticker = m.group(2);
					String lastPrice = m.group(5);
					String quantity = m.group(6);
					String date = m.group(7);
					String dayLowPrice = m.group(8);
					String dayHighPrice = m.group(9);
					 
					try {
						Item item = new Item();
						item.setVolume(0);
						item.setDecimals(4);
						item.setName(name);
						item.setTicker(ticker);
						item.setLow(Double.parseDouble(dayLowPrice));
						item.setHigh(Double.parseDouble(dayHighPrice));
						item.setVolume(Integer.parseInt(quantity));
						item.setLast(Double.parseDouble(lastPrice));
						item.setGivenDate(parseKauppalehtiDate(date));
						items.add(item);
						//System.out.format("'%s', '%s', '%s', '%s', '%s', '%s', '%s'\n", name, ticker, lastPrice, dayLowPrice, dayHighPrice, quantity, date);
					} catch (NumberFormatException e) {
						System.out.println(e.getMessage());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}

	private static String convertStreamToString(java.io.InputStream is) {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static void main(String[] args) {
		String uri = "https://www.kauppalehti.fi/porssi/kurssit/XHEL";
		KauppalehtiJsonQuoteFetcher fetcher = new KauppalehtiJsonQuoteFetcher(uri);
		List<Item> items = fetcher.parseHtml();

	}
}
