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

public abstract class TalentumQuoteFetcher extends HTTPQuoteFetcher {

	protected Map<String, String> tickers;
	
	public TalentumQuoteFetcher() {
		super();
		tickers = new HashMap<>();
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

	 
	public List<Item> parseHtml() {
		//System.out.println(getUri());
		// System.out.println(getXpath());
		List<Item> items = new ArrayList<Item>();
	
		try {
			org.w3c.dom.NodeList nodes = fetchNodes(getUri(), getXpath());
			// System.out.println(nodes.getLength());
			if (nodes != null) {
	
				for (int i = 0; i < nodes.getLength(); i++) {
					org.w3c.dom.Node tr = nodes.item(i);
	
					Item item = new Item();
	
					parseRow(item, tr);
					//System.out.println(item.getName());
					//item.print();
					if (item.getTicker() != null) {
						items.add(item);
						 //System.out.println(item.getTicker() + " " +
						 //item.getName()+ " " + item.getLast() + " " +
						 //item.getVolume());
	
					}
	
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return items;
	}
	protected void parseRow(Item item, org.w3c.dom.Node tr) {
		org.w3c.dom.NodeList nodes = tr.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node td = nodes.item(i);
	
			parseColumn(item, i, td);
		}
	}
	
	protected void parseColumn(Item item, int column, org.w3c.dom.Node td) {
		org.w3c.dom.NodeList nodes = td.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node tdNode = nodes.item(i);

			String value = tdNode.getNodeValue().trim();
			value = value.replace(',', '.');

			// System.out.println("col: " + column);
			if (column == getNameColumn()) {
				if (tdNode.getNodeName().equalsIgnoreCase("a")) {
					org.w3c.dom.Node n = tdNode.getFirstChild();
				//	System.out.println(n);
					if (n != null) {
						item.setName(n.getNodeValue());
						item.setTicker(getTicker(item.getName()));
					}
				}
			} 
			if (column == getLastValueColumn()) {
				try {
					item.setLast(Double.parseDouble(value));
				} catch (Exception e) {
				}
			} 
			if (column == getHighColumn()) {
				try {
					item.setHigh(Double.parseDouble(value));
				} catch (Exception e) {
				}
			} 
			if (column == getLowColumn()) {
				try {
					item.setLow(Double.parseDouble(value));
				} catch (Exception e) {
				}
			} 
			if (column == getVolumeColumn()) {
				try {
					value = value.replaceAll(" ", "");
					value = value.replace(new Character((char) 160).toString(),
							"");

					item.setVolume(Integer.parseInt(value));
				} catch (Exception e) {
					// System.out.println("volume: " + value);
				}
			}
			if (column == getDateColumn()) { // date
				try {
					Date date = parseDate(value);
					item.setGivenDate(date);
				} catch (Exception e) {
				}
			} 
			// System.out.print(nodes3.item(k).getNodeValue());
		}
	}
	protected String getTicker(String name) {
		if (this.tickers.containsKey(name)) {
			return this.tickers.get(name);
		}
		return null;
		
	}
	protected abstract int getNameColumn();
	protected abstract int getLastValueColumn();
	protected abstract int getHighColumn();
	protected abstract int getLowColumn();
	protected abstract int getVolumeColumn();
	protected abstract int getDateColumn();
}
