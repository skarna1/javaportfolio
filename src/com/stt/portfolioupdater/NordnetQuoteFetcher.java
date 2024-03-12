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

import javax.xml.xpath.XPathExpressionException;

public class NordnetQuoteFetcher extends HTTPQuoteFetcher {

	protected Map<String, String> tickers;
	protected String SYMBOL_MAP_FILE = "etc/NordnetStockQuoteFetcher.txt";

	public NordnetQuoteFetcher() {
		tickers = new HashMap<>();
		readTickers(this.SYMBOL_MAP_FILE);
	}

	public NordnetQuoteFetcher(String uri, String xpath) {
		this.uri = uri;
		this.xpath = xpath;
		tickers = new HashMap<>();
		readTickers(this.SYMBOL_MAP_FILE);
	}

	@Override
	public List<Item> parseHtml() {

		// System.out.println(getUri());
		// System.out.println(getXpath());

		List<Item> items = new ArrayList<>();

		try {
			org.w3c.dom.NodeList nodes = fetchNodes(getUri(), getXpath());
			if (nodes != null) {
				// System.out.println("Nodes : " + nodes.getLength());


				for (int i = 0; i < nodes.getLength(); i += 8) {

					Item item = new Item();

					String name = this.getValue(nodes.item(i));
					System.out.println("name: " + name);
					item.setName(name);
					item.setTicker(getTicker(name));

					try {
						double v = Double.parseDouble(this.getNumberValue(nodes.item(i + 1)));
						item.setLast(v);
					} catch (NumberFormatException e) {
					}
					try {
						double v = Double.parseDouble(this.getNumberValue(nodes.item(i + 5)));
						item.setHigh(v);
					} catch (NumberFormatException e) {
					}
					try {
						double v = Double.parseDouble(this.getNumberValue(nodes.item(i + 6)));
						item.setLow(v);
					} catch (NumberFormatException e) {
					}

					item.setGivenDate(new Date());
					// item.print();

					if (item.getTicker() != null) {
						items.add(item);
						// item.print();
					}
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}

	private String getValue(org.w3c.dom.Node node) {
		if (node == null)
			return "";
		String value = node.getNodeValue().trim();
		return value;
	}

	private String getNumberValue(org.w3c.dom.Node node) {
		String value = getValue(node).replace(',', '.');
		value = value.replaceAll(" ", "");
		value = value.replaceAll("[^\\d.]", "");
		return value;
	}

	protected String getTicker(String name) {
		// System.out.println(name);
		if (this.tickers.containsKey(name)) {

			return this.tickers.get(name);
		} else {
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
		String uri = "https://www.nordnet.fi/markkinakatsaus/osakekurssit?page=1&exchangeCountry=FI&limit=100";
		String xpathstr = "(//div[@role=\"row\"]/div[@role=\"cell\"]/div/span/text()|//div[2]/span/span/a/text())";

		NordnetQuoteFetcher fetcher = new NordnetQuoteFetcher(uri, xpathstr);
		List<Item> items = fetcher.parseHtml();
		for (Item item : items) {
			item.print();
		}
	}
}
