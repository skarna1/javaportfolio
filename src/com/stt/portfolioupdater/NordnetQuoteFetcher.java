package com.stt.portfolioupdater;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
	
		//System.out.println(getUri());
		//System.out.println(getXpath());
		
		
		List<Item> items = new ArrayList<>();

		try {
			org.w3c.dom.NodeList nodes = fetchNodes(
					getUri(),
					getXpath());
			if (nodes != null) {
				System.out.println("Nodes: " + nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					org.w3c.dom.Node tr = nodes.item(i);
					Item item = new Item();
					parseRow(item, tr);
					//item.print();
					
					if (item.getTicker() != null) {
						items.add(item);
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
		for (int i = 1; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node td = nodes.item(i);
			parseColumn(item, i, td);
		}
	}

	private void parseColumn(Item item, int column, org.w3c.dom.Node td) {
		org.w3c.dom.NodeList nodes = td.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node tdNode = nodes.item(i);

			String value = tdNode.getNodeValue().trim();
			value = value.replace(',', '.');
			value = value.replaceAll(" ", "");
			value = value.replaceAll("[^\\d.]", "");
			//System.out.println("column " + column + " " + "value " + value);
			if (column == 1) {
				
				tdNode = tdNode.getFirstChild();
				
				if (tdNode.getNodeName().equalsIgnoreCase("a")) {
					org.w3c.dom.Node n = tdNode.getFirstChild();
					String name = n.getNodeValue();
					item.setName(name);
					//System.out.println(name);
					item.setTicker(getTicker(name));
				
				}
			} else if (column == 2) {
				try {
					double v = Double.parseDouble(value);
					item.setLast(v);

				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
			}
			else if (column == 7) {
				try {
					double v = Double.parseDouble(value);
					item.setHigh(v);

				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
			}
			else if (column == 8) {
				try {
					double v = Double.parseDouble(value);
					item.setLow(v);

				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
			}
			else if (column == 9) {
				try {
					long v = Long.parseLong(value);
					item.setVolume(v);

				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
			}
		}
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
		String uri = "https://www.nordnet.fi/mux/web/marknaden/kurslista/aktier.html?" + 
	                 "marknad=Finland&lista=1_1&large=on&mid=on&small=on&sektor=0" +
				     "&subtyp=price&sortera=aktie&sorteringsordning=stigande";
		NordnetQuoteFetcher fetcher = new NordnetQuoteFetcher(uri, 
				"//table[@id='kurstabell']/tbody/tr[@class='highLight first' or @class='highLight' or @class='highLight last']");
		List<Item> items = fetcher.parseHtml();
		//for (Item item : items) {
		//	item.print();
		//}
	}
}
