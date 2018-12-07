package com.stt.portfolioupdater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class IEXQuoteFetcher extends HTTPQuoteFetcher {
	
	private KauppalehtiJsonExchangerateFetcher currencyFetcher;
	private ScriptEngine engine;
	  
	public IEXQuoteFetcher() {
		currencyFetcher = new KauppalehtiJsonExchangerateFetcher();
		ScriptEngineManager sem = new ScriptEngineManager();
	    this.engine = sem.getEngineByName("javascript");
	}
	
	@Override
	public List<Item> parseHtml() {
		List<Item> items = new ArrayList<>();
		String[] stocks = xpath.split(";");
	
		for (String stockString : stocks){
			String[] parts = stockString.split(",");
			String symbolString = parts[0];
			String localsymbol = parts[1];
			String ccy = parts[2];

			try {
				String uri = "https://api.iextrading.com/1.0/stock/" + symbolString + "/quote";
				String lines = YahooUtils.readInput(uri);
				
				Map contents = this.parseJson(lines);
				parseItem(contents, localsymbol, ccy, items);
			} catch (Exception e) {
				System.out.println("Error: " + stockString);
				e.printStackTrace();
			}
		}
		return items;
	}

	private void parseItem(Map contents, String symbol,
			String ccy, List<Item> items) throws IOException, Exception {
		
		double rate = 1.0;
		if (!ccy.equalsIgnoreCase("EUR")) {
			rate = currencyFetcher.getExchangeRate(ccy);
			if (rate == 0.0) {
				return;
			}
		}
		
		double last = convertToDouble(contents.get("latestPrice"));
		double high = last;
		double low = last;
		try {
			high = convertToDouble(contents.get("high"));
			low = convertToDouble(contents.get("low"));
		}
		catch (NullPointerException e) 
		{
		}
		long volume = 0;
		Object o = contents.get("latestVolume");
		if (o instanceof Long) {
			volume = (long) o;
		}
		else if (o instanceof Integer) {
			volume = Long.valueOf((int) o);
		}
		else if (o == null) {
			volume = 0;
		}
		
		String datestr = (String) contents.get("latestTime");
		// date format: December 15, 2017
		// System.out.println(symbol + " " + datestr);
		if (!datestr.trim().equals("N/A")) {
			Date date = null;
			if (datestr.toUpperCase().endsWith("M")) {
			    date = Calendar.getInstance().getTime();
			}
			else {
				DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
				date = df.parse(datestr);
			}
			Item item = new Item();
			item.setValues(symbol, last, high, low, volume, date, rate);
			items.add(item);
			//System.out.println("added " + symbol + " " + datestr);
			//System.out.println(item.getLine());
		}
	}

	private List<String>  createSymbolList(String[] stocks) {
		List<String> symbollist = new ArrayList<>();
				
		for (String stock : stocks) {
			String[] symbols = stock.split(",");
			String symbol = symbols[0];
			
			symbollist.add(symbol);
		}
		return symbollist;
	}
	
	 public Map parseJson(String json) throws IOException, ScriptException {
	        String script = "Java.asJSONCompatible(" + json + ")";
	        Object result = this.engine.eval(script);
	        Map contents = (Map) result;
	        return contents;
	    }

	double convertToDouble(Object o)
	{
		if (o instanceof Integer) {
			return Double.valueOf((int) o);
		}
		return (double) o;
	}
	public static void main(String [ ] args)
	{
		
		
	}
}
