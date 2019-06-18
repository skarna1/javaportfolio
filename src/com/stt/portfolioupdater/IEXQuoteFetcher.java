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
				String uri = "https://api.iextrading.com/1.0/tops?symbols=" + symbolString;
				String lines = YahooUtils.readInput(uri);
			    //System.out.println("lines: " + lines);
				//
				// [{"symbol":"PG","sector":"householdpersonalproducts",
				//   "securityType":"commonstock","bidPrice":110.16,"bidSize":100,
				//   "askPrice":110.25,"askSize":100,"lastUpdated":1560870353076,
				//   "lastSalePrice":110.22,"lastSaleSize":100,
				//   "lastSaleTime":1560870332640,"volume":74884,"marketPercent":0.03445}]
				//
				//
				List contents = this.parseJson(lines);
				if (contents != null) {
					parseItem((Map)(contents.get(0)), localsymbol, ccy, items);
				}
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
		
		double last = convertToDouble(contents.get("lastSalePrice"));
		double high = last;
		double low = last;
//		try {
//			high = convertToDouble(contents.get("high"));
//			low = convertToDouble(contents.get("low"));
//		}
//		catch (NullPointerException e) 
//		{
//		}
		long volume = 0;
		Object o = contents.get("volume");
		if (o instanceof Long) {
			volume = (long) o;
		}
		else if (o instanceof Integer) {
			volume = Long.valueOf((int) o);
		}
		else if (o == null) {
			volume = 0;
		}
		
		Long seconds = new Double((double)contents.get("lastSaleTime")).longValue();
		Date date = new Date(seconds);
		if (date != null) {
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
	
	 public List parseJson(String json) throws IOException, ScriptException {
	        String script = "Java.asJSONCompatible(" + json + ")";
	        Object result = this.engine.eval(script);
	        List contents = (List) result;
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
