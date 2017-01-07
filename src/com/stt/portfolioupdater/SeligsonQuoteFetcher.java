package com.stt.portfolioupdater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

public class SeligsonQuoteFetcher extends HTTPQuoteFetcher {

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
				//System.out.println("Nodes: " + nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					org.w3c.dom.Node tr = nodes.item(i);

					Item itemA = new Item();
					Item itemB = new Item();
					parseRow(itemA, tr, true);
					parseRow(itemB, tr, false);
					//itemA.print();
					//itemB.print();
					if (itemA.getTicker() != null) {
						items.add(itemA);
						
					}
					if (itemB.getTicker() != null) {
						items.add(itemB);
			
					}
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}

	private void parseRow(Item item, org.w3c.dom.Node tr, boolean isA) {
		org.w3c.dom.NodeList nodes = tr.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node td = nodes.item(i);

			parseColumn(item, i, td, isA);
			
		}
	}

	private void parseColumn(Item item, int column, org.w3c.dom.Node td, boolean isA) {
		org.w3c.dom.NodeList nodes = td.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			org.w3c.dom.Node tdNode = nodes.item(i);

			String value = tdNode.getNodeValue().trim();
			value = value.replace(',', '.');
			value = value.replaceAll(" ", "");
		
			if (column == 0) {
				if (tdNode.getNodeName().equalsIgnoreCase("a")) {
					org.w3c.dom.Node n = tdNode.getFirstChild();
					String name = n.getNodeValue();

					item.setName(name);
					
					item.setTicker(getTicker(name, isA));
					
					
				}
			} else if (column == 1) { // date
				try {
					
					
					Date date = parseDate(value);
					item.setGivenDate(date);
					//System.out.println(date);
				} catch (Exception e) {
				}
			} else if (column == 2 && isA) { // Value of Kasvu osuus
				try {
					setItemValues(item, value);
					
				} catch (Exception e) {
				}
			} else if (column == 3 && ! isA) { // Value of Tuotto osuus
				try {
					setItemValues(item, value);
				} catch (Exception e) {
				}
			}
			
		}
	}

	private void setItemValues(Item item, String value) {
		double v = Double.parseDouble(value);
		item.setLast(v);
		item.setHigh(v);
		item.setLow(v);
		item.setDecimals(4);
	}
	
	private String getTicker(String name, boolean isA) {
		String suffix = (isA) ? "A" : "B" ;
		//System.out.println(name + "  " + suffix);
		if (name.equalsIgnoreCase("Aasia")) {
			return "SELIGJAP" + suffix;
		}else if (name.startsWith("Euro Corp")) {
			return "SELEUCBI" + suffix;
		}else if (name.startsWith("Rahamarkk")) {
			return "SELIGRAHA" + suffix;
		}else if (name.equalsIgnoreCase("Euro-obligaatio")) {
			return "SELEUROBL" + suffix;
		}else if (name.equalsIgnoreCase("Suomi")) {
			return "SELIGSUOM" + suffix;
		}else if (name.startsWith("OMXH 25 ETF") && !isA) {
			return "SLGOMXH25";
		}else if (name.equalsIgnoreCase("Global Brands")) {
			return "SELGLOBAL" + suffix;
		}else if (name.startsWith("Global Pharma")) {
			return "SELIG25PH" + suffix;
		}else if (name.startsWith("Pohjois-Amer")) {
			return "SELIGPAM" + suffix;
		}else if (name.startsWith("Kehittyvät markk")) {
			return "SELIGKEHM" + suffix;
		}else if (name.equalsIgnoreCase("Eurooppa")) {
			return "SELIGEUR" + suffix;
		}else if (name.equalsIgnoreCase("Russian Prosperity") && isA) {
			return "SELIGPRORU";
		}else if (name.equalsIgnoreCase("Russian Prosperity") && !isA) {
			return "SELIGRUPRK";
		}else if (name.equalsIgnoreCase("Pharos")) {
			return "SELIGPHAR" + suffix;
		}else if (name.equalsIgnoreCase("Phoenix")) {
			return "SELPHOENI" + suffix;
		}else if (name.equalsIgnoreCase("Phoebus")) {
			return "PHOEBUS" + suffix;
		} else if (name.equalsIgnoreCase("Tropico LatAm")) {
			return "TROPLATAM" + suffix;
		}
		return null;
	}

	
}
