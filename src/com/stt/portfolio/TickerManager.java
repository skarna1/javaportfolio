package com.stt.portfolio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TickerManager implements I_TickerManager {

	String filename = "";

	Map<String, Stock> stocksByTicker = new HashMap<String, Stock>();

	Map<String, Stock> stocksByName = new HashMap<String, Stock>();

	Map<String, List<String>> stocksBySector = new HashMap<String, List<String>>();

	Set<String> sectors = new LinkedHashSet<String>();

	Map<String, Stock> allStocksByTicker = new HashMap<String, Stock>();

	Set<String> currencies = new HashSet<String>();
	
	static TickerManager me = null;
	
	public static TickerManager createTickerManager() {
		if (me == null) {
			me = new TickerManager("etc/tunnukset.csv");
			me.init();
		}
		return me;
	}
	
	public TickerManager(String filename) {
		this.filename = filename;
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getStock(java.lang.String)
	 */
	public Stock getStock(String ticker) {
		return allStocksByTicker.get(ticker);
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getSectors()
	 */
	public Object[] getSectors() {
		return sectors.toArray();
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getStocksBySector()
	 */
	public Map<String, List<String>> getStocksBySector() {
		return stocksBySector;
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getTicker(java.lang.String)
	 */
	public String getTicker(String name) {
		Stock s = stocksByName.get(name);
		if (s != null) {
			return s.getTicker();
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getName(java.lang.String)
	 */
	public String getName(String ticker) {
		Stock s = allStocksByTicker.get(ticker);
		if (s != null) {
			return s.getName();
		}
		System.out.println("unknown ticker: " + ticker);
		return ticker;
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#init()
	 */
	public void init() {
		BufferedReader in = null;
		try {

			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename), "ISO8859_1"));
			String str;

			while ((str = in.readLine()) != null) {
				if (!str.startsWith("#")) {
					Stock stock = parse(str);
					if (stock != null) {
						if (stock.getType().equalsIgnoreCase("S")
								|| stock.getType().equalsIgnoreCase("F")
								|| stock.getType().equalsIgnoreCase("O")) {

							stocksByTicker.put(stock.getTicker(), stock);
							stocksByName.put(stock.getName(), stock);
							sectors.add(stock.getSector());
							if (!stocksBySector.containsKey(stock.getSector())) {
								stocksBySector.put(stock.getSector(),
										new ArrayList<String>());
							}
							stocksBySector.get(stock.getSector()).add(
									stock.getName());
						}
						allStocksByTicker.put(stock.getTicker(), stock);
						if (!stock.getCcy().equalsIgnoreCase("EUR")) {
							currencies.add(stock.getCcy());
						}
					}
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (ParseException e) {

			e.printStackTrace();
		}
	}

	private Stock parse(String line) throws ParseException {
		String[] items = line.split(";");
		if (items.length < 4) {
			throw new ParseException("Parse error in " + filename + " line: "
					+ line);
		}

		String sector = items[0].trim();
		String type = items[1].trim();
		String ticker = items[2].trim();
		String name = items[3].trim();
		
		Stock stock = new Stock();
		stock.setSector(sector);
		stock.setTicker(ticker);
		stock.setName(name);
		stock.setType(type);
		if (items.length > 4) {
			stock.setPriceDivider(Integer.parseInt(items[4].trim()));
		}
		if (items.length > 5) {
			stock.setCcy(items[5].trim());
		}
		else {
			stock.setCcy("EUR"); // Default currency is Euro
		}
		
		// Set country if it exists
		if (items.length > 6) {
			stock.setCountry(items[6].trim());
		}
		
		
		return stock;

	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getCcys()
	 */
	public Object[] getCcys() {
		
		return currencies.toArray();
	}
	
	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getDecimals(java.lang.String)
	 */
	public int getDecimals(String ticker) {
		if (getStock(ticker).getType().equals("F")) {
			return 5;
		}
		return 2;
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.I_TickerManager#getCountry(java.lang.String)
	 */
	public String getCountry(String ticker) {
		
		Stock s = getStock(ticker);
		if (s != null) {
			return s.getCountry();
		}	
		else {
			System.out.println("No ticker: " + ticker);
			return Stock.DEFAULT_COUNTRY;
		}
	}
}
