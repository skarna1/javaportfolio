package com.stt.portfolioupdater;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

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
	
		//System.out.println(getUri());
		//System.out.println(getXpath());
		
		
		List<Item> items = new ArrayList<>();

		try {
			org.w3c.dom.NodeList nodes = fetchNodes(
					getUri(),
					getXpath());
			if (nodes != null) {
				// System.out.println("Nodes : " + nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					org.w3c.dom.Node tr = nodes.item(i);
					Item item = new Item();
					parseRow(item, tr);
					item.setGivenDate(new Date());
					//item.print();
					
					if (item.getTicker() != null) {
						items.add(item);
						item.print();
					}
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}

	
	private void parseRow(Item item, org.w3c.dom.Node tr) {
		org.w3c.dom.NodeList nodes = tr.getChildNodes();
		//System.out.println("Columns : " + nodes.getLength());
		for (int i = 1; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node td = nodes.item(i);
			parseColumn(item, i, td);
		}
	}

	private void parseColumn(Item item, int column, org.w3c.dom.Node td) {
		//org.w3c.dom.NodeList nodes = td.getChildNodes();
		//for (int i = 0; i < nodes.getLength(); ++i) {
		//	org.w3c.dom.Node tdNode = nodes.item(i);

		org.w3c.dom.Node tdNode = td.getFirstChild();
			if (column == 4) {
				tdNode = tdNode.getFirstChild();  // div
				if (tdNode != null) tdNode = tdNode.getFirstChild();
				
				if (tdNode.getNodeName().equalsIgnoreCase("a")) {
					org.w3c.dom.Node n = tdNode.getFirstChild();
					String name = n.getNodeValue();
					item.setName(name);
					System.out.println(name + " " + getTicker(name));
					item.setTicker(getTicker(name));
				
				}
			} else if (column == 7) {
				// div, span, span, span, span /span, /span, span,span, /span, /span, span value /span, span, /span,/span,/span/div
				// last
				tdNode = tdNode.getFirstChild();  // div
				
				if (tdNode != null) tdNode = tdNode.getFirstChild(); // span
				//if (tdNode != null) tdNode = tdNode.getFirstChild(); // span
				org.w3c.dom.NodeList childs = tdNode.getChildNodes();
				if (tdNode != null) tdNode = childs.item(2);
				String columnValue = this.getValue(tdNode);
				//System.out.println("Column " + column + " value: " + columnValue);
				double v = Double.parseDouble(columnValue);
				item.setLast(v);	
			}
			else if (column == 11) {
				// high
				tdNode = tdNode.getFirstChild();  // div
				
				if (tdNode != null) tdNode = tdNode.getFirstChild(); // span
				
				org.w3c.dom.NodeList childs = tdNode.getChildNodes();
				if (tdNode != null) tdNode = childs.item(2);
				String columnValue = this.getValue(tdNode);
				//System.out.println("Column " + column + " value: " + columnValue);
				double v = Double.parseDouble(columnValue);
				item.setHigh(v);
			}
			else if (column == 12) {
				// low
				tdNode = tdNode.getFirstChild();  // div
				
				if (tdNode != null) tdNode = tdNode.getFirstChild(); // span
			
				org.w3c.dom.NodeList childs = tdNode.getChildNodes();
				if (tdNode != null) tdNode = childs.item(2);
				String columnValue = this.getValue(tdNode);
				//System.out.println("Column " + column + " value: " + columnValue);
				double v = Double.parseDouble(columnValue);
				item.setLow(v);
			}
		//}
	}
	
	private String getValue(org.w3c.dom.Node node) {
		if (node == null) return "";
		String value = node.getNodeValue().trim();
		value = value.replace(',', '.');
		value = value.replaceAll(" ", "");
		value = value.replaceAll("[^\\d.]", "");
		
		return value;
	}

	protected String getTicker(String name) {
		//System.out.println(name);
		if (this.tickers.containsKey(name)) {
			
			return this.tickers.get(name);
		}
		else {
			System.out.println(name);
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
		}
		catch (IOException e) {
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
		String uri = "https://www.nordnet.fi/markkinakatsaus/osakekurssit/";
		String xpathstr = "//table[contains(@class, ' md')]/tbody/tr";
		
		NordnetQuoteFetcher fetcher = new NordnetQuoteFetcher(uri, 
				xpathstr);
		List<Item> items = fetcher.parseHtml();
		for (Item item : items) {
			item.print();
		}
	}
}
