package com.stt.portfolioupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class YahooQuoteFetcher extends HTTPQuoteFetcher {
	
	private int chunk = 10;
	private YahooCurrencyQuoteFetcher currencyFetcher;
	
	public YahooQuoteFetcher() {
		currencyFetcher = new YahooCurrencyQuoteFetcher();
	}
	
	@Override
	public List<Item> parseHtml() {
	
		List<Item> items = new ArrayList<>();

		String[] stocks = xpath.split(";");

		List<String> symbollist = createSymbolList(stocks);
	
		int j = 0;
		for (String symbolString : symbollist){
			//System.out.println(symbolString);

			try {
				String uri = "http://finance.yahoo.com/d/quotes.csv?s="
						+ symbolString + "&f=hgl1vd1";
				//System.out.println(uri);
				String lines = YahooUtils.readInput(uri);
				//System.out.println(lines);
				StringReader r = new StringReader(lines);
				BufferedReader br = new BufferedReader(r);

				for (int i = j; i < Math.min(j+chunk, stocks.length); i++ ) {
					parseItem(items, stocks[i], br);
				}
				j+=chunk;
			} catch (Exception e) {
				System.out.println("Error: " + symbollist);
				e.printStackTrace();
			}
		}
		return items;
	}

	private void parseItem(List<Item> items, String stock, BufferedReader br) throws IOException, Exception {
		
		String[] symbols = stock.split(",");
		//String symbol = symbols[0];
		String ticker = symbols[1];
		String ccy = symbols[2];
		//System.out.println(ticker);


		double rate = 1.0;
		if (!ccy.equalsIgnoreCase("EUR")) {
			rate = currencyFetcher.getExchangeRate(ccy);
			if (rate == 0.0) {
				return;
			}
		}
		String line = br.readLine();
		if (line != null) {
			//System.out.println("line " + line);
			String[] lineitems = line.split(",");
			String highstr = lineitems[0];
			String lowstr = lineitems[1];
			String laststr = lineitems[2];
			String volumestr = lineitems[3];
			String datestr = lineitems[4];
			datestr = datestr.replace('"', ' ');
			datestr = datestr.trim();

			long volume = 0;
			double high = 0.0;
			double low = 0.0;
			double last = 0.0;
			if (highstr.equals("N/A"))
				highstr = laststr;
			if (lowstr.equals("N/A"))
				lowstr = laststr;
			try {
				volume = Long.parseLong(volumestr);
				high = Double.parseDouble(highstr);
				low = Double.parseDouble(lowstr);
				last = Double.parseDouble(laststr);
			}
			catch (NumberFormatException e)
			{
				System.out.println("fail: " + ticker);
				System.out.println(volumestr + " " + highstr + " " + lowstr + " " + laststr);
			}


			// System.out.println(symbol + " " + datestr);
			if ((!datestr.trim().equals("1/1/1970")) && (!datestr.trim().equals("N/A"))) {
				Date date = parseYahooDate(datestr);
				Item item = new Item();
				item.setValues(ticker, last, high, low, volume, date, rate);
				
				items.add(item);
				//System.out.println("added " + ticker + " " + datestr);
				//System.out.println(item.getLine());
			}
		}
	}

	private List<String> createSymbolList(String[] stocks) {
		List<String> lists=new ArrayList<String>();
		
		for (int i = 0; i < stocks.length; i += chunk){ 
			String[] stockChunk=Arrays.copyOfRange(stocks,i,Math.min(i+chunk,stocks.length));

			String symbollist= createSymbolString(stockChunk);
			lists.add(symbollist);	
		}
		return lists;
	}

	private String createSymbolString(String[] stocks) {
		String symbollist = "";
		for (String stock : stocks) {
			String[] symbols = stock.split(",");
			String symbol = symbols[0];
			if (symbollist.length() > 0) {
				symbollist = symbollist + "+";
			}
			symbollist = symbollist + symbol;
		}
		return symbollist;
	}
}
