package com.stt.portfolioupdater;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NordnetQuoteFetcher extends HTTPQuoteFetcher {

	protected Map<String, String> tickers;
	protected Map<String, Boolean> found = new HashMap<>();
	protected String SYMBOL_MAP_FILE_FI = "etc/NordnetStockQuoteFetcher.txt";
	protected String SYMBOL_MAP_FILE_SE = "etc/NordnetStockQuoteFetcher_se.txt";
	protected Boolean reportUnknownName = true;
	protected int pages = 2;
	protected String ccy = "EUR";
	public NordnetQuoteFetcher() {
		tickers = new HashMap<>();
	}

	public NordnetQuoteFetcher(String uri, String xpath) {
		this.uri = uri;
		this.xpath = xpath;
		tickers = new HashMap<>();
	}

	private void readTickersForExchange(String exchange) {
		if (exchange == "OMXS") {
			this.ccy = "SEK";
			this.reportUnknownName = false;
			this.pages=11;
		    readTickers(this.SYMBOL_MAP_FILE_SE);
		}
		else {
			readTickers(this.SYMBOL_MAP_FILE_FI);
		}
	}

	public void init() {
		String exchange = getExchange();
		readTickersForExchange(exchange);
	}

	private String getExchange()  {
		URI uri;
		try {
			uri = new URI(getUri());
			String query = uri.getQuery();
			String[] parameters = query.split("&");
			for (String parameter : parameters) {
				if (parameter.equals("exchangeCountry=SE")) {
					return "OMXS";
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	@Override
	public List<Item> parseHtml() {
		// System.out.println(getXpath());

		List<Item> items = new ArrayList<>();

		for (int page = 1; page <= this.pages; page++) {
			String uri = getUri();
			uri = uri.replaceAll("page=\\d{1,2}&", "page="+page+"&");
			//System.out.println(uri);
			try {
				Elements elements = fetchNodes(uri, getXpath());
				if (elements != null) {
					//System.out.println("Nodes : " + elements.size());

					for (int i = 0; i < elements.size(); i += 11) {

						Item item = new Item();

						String name = this.getValue(elements.get(i));
						//System.out.println("name: " + name);
						item.setName(name);
						item.setTicker(getTicker(name));


						try {
							double v = Double.parseDouble(this.getNumberValue(elements.get(i + 2)));
							item.setLast(v);
						} catch (NumberFormatException e) {
						}
						try {
							double v = Double.parseDouble(this.getNumberValue(elements.get(i + 7)));
							item.setHigh(v);
						} catch (NumberFormatException e) {
						}
						try {
							double v = Double.parseDouble(this.getNumberValue(elements.get(i + 8)));
							item.setLow(v);
						} catch (NumberFormatException e) {
						}

						item.setGivenDate(new Date());
						// item.print();

						if (item.getTicker() != null) {
							double rate = 1.0;
							if (!ccy.equals("EUR")) {
								rate = CcyFactory.createCcyFetcher().getExchangeRate(ccy);
							}
							item.setRate(rate);
							items.add(item);
							// item.print();
						}
					}
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Set<String> notFoundNames = found
				.entrySet()
				.stream()
				.filter(entry -> Objects.equals(entry.getValue(), false))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

		if (notFoundNames.size() > 0) {
			System.out.println("Could not found ticker for:");
			notFoundNames.forEach(System.out::println);
		}
		return items;
	}

	private String getValue(Element element) {
		if (element == null)
			return "";
		String value = element.text().trim();
		return value;
	}

	private String getNumberValue(Element element) {
		String value = getValue(element).replace(',', '.');
		value = value.replaceAll(" ", "");
		value = value.replaceAll("[^\\d.]", "");
		return value;
	}

	protected String getTicker(String name) {
		// System.out.println(name);
		if (this.tickers.containsKey(name)) {
			found.put(name, true);
			return this.tickers.get(name);
		} else {
			if (reportUnknownName)
				System.out.println("ERROR: No ticker for " + name);
		}
		return null;
	}

	public void readTickers(String filename) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			while (line != null) {
				line = line.trim();
				String[] items = line.split("[ \t]", 2);

				if (items.length > 1) {
					if (items[0].length() > 0) {
						String ticker = items[0].trim();
						ticker = ticker.replace('_', ' ');
						String name = items[1].trim();
						tickers.put(name, ticker);
						found.put(name, false);
					}
				}
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		}

		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		String uri = "https://www.nordnet.fi/osakkeet/kurssit?page=1&exchangeCountry=SE&limit=100&selectedTab=prices";
		String xpathstr = "(//div[@role=\"cell\"]/div/span/span | //div[2]/span/span/a)";

	

		
		NordnetQuoteFetcher fetcher = new NordnetQuoteFetcher(uri, xpathstr);
		List<Item> items = fetcher.parseHtml();
		for (Item item : items) {
			item.print();
		}
	}
}
