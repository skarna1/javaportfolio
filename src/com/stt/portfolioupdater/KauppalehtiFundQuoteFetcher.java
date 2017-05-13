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

public class KauppalehtiFundQuoteFetcher extends HTTPQuoteFetcher {

	protected Map<String, String> tickers;
	
	public KauppalehtiFundQuoteFetcher() {
		super();
		tickers = new HashMap<>();
		readTickers("etc/KauppalehtiFundQuoteFetcher.txt");
	}
	
	public List<Item> parseHtml() {
		//System.out.println(getUri());
		//System.out.println(getXpath());
		//xpath="//table[@border='0' and @cellspacing='1' and @width='630' and @align='left']/tr"
		
		List<Item> items = new ArrayList<>();

		try {
			org.w3c.dom.NodeList nodes = fetchNodes(
					getUri(),
					getXpath());
			if (nodes != null) {
				
//				System.out.println("Nodes: " + nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					org.w3c.dom.Node tr = nodes.item(i);

					Item item = new Item();
					item.setVolume(0);
					item.setDecimals(4);
					parseRow(item, tr);
					if (item.getTicker() != null) {
						items.add(item);
//						 System.out.println(item.getTicker() + " " +
//						 item.getName()+ " " + item.getLast() + " " +
//						 item.getVolume());

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
		for (int i = 0; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node td = nodes.item(i);

			parseColumn(item, i, td);
		}
	}

	private void parseColumn(Item item, int column, org.w3c.dom.Node td) {
		org.w3c.dom.NodeList nodes = td.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node tdNode = nodes.item(i);
			String value = tdNode.getNodeValue().trim();
			if (column == 0) {
				if (tdNode.getNodeName().equalsIgnoreCase("a")) {
					org.w3c.dom.Node n = tdNode.getFirstChild();
					
					if (n != null) {
						item.setName(n.getNodeValue());
						//System.out.println(item.getName());
						item.setTicker(getTicker(item.getName()));
					}
				}
			} else if (column==3) {
				try {
					item.setLast(Double.parseDouble(value));
				} catch (Exception e) {
				}
			}
			else if (column==9) {
				try {
					Date date = parseDate(value);
					item.setGivenDate(date);
				} catch (Exception e) {
				}
			}
		}
	}

	protected String getTicker(String name) {
		if (this.tickers.containsKey(name)) {
			return this.tickers.get(name);
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
						tickers.put(items[1].trim(), items[0].trim());
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
}
