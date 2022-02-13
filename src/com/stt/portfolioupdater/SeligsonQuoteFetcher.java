package com.stt.portfolioupdater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

public class SeligsonQuoteFetcher extends HTTPQuoteFetcher {

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
				//System.out.println("Nodes: " + nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					org.w3c.dom.Node tr = nodes.item(i);
					Item itemA = new Item();
					parseRow(itemA, tr);
					//itemA.print();

					if (itemA.getTicker() != null) {
						items.add(itemA);
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
			value = value.replace(',', '.');
			value = value.replaceAll(" ", "");
			value = value.replaceAll("[^\\d.]", "");
			//System.out.println("column " + column + " " + "value " + value);
			if (column == 0) {
				if (tdNode.getNodeName().equalsIgnoreCase("a")) {
					org.w3c.dom.Node n = tdNode.getFirstChild();
					String name = n.getNodeValue();
					item.setName(name);
					item.setTicker(getTicker(name));
				}
			} else if (column == 1) { // date
				try {
					Date date = parseDate(value);
					item.setGivenDate(date);
					//System.out.println(date);
				} catch (Exception e) {
				}
			} else if (column == 2) { // Value of Kasvu osuus
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

	private String getTicker(String name) {
		String suffix = "A";
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
		}else if (name.startsWith("OMXH25")) {
			return "SLG OMXH25";
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
		}else if (name.equalsIgnoreCase("Russian Prosperity")) {
			return "SELIGPRORU";
		}else if (name.equalsIgnoreCase("Pharos")) {
			return "SELIGPHAR" + suffix;
		} else if (name.equalsIgnoreCase("Perheyhtiöt")) {
			return "SELPERHE" + suffix;
		}else if (name.equalsIgnoreCase("Phoebus")) {
			return "PHOEBUS" + suffix;
		} else if (name.equalsIgnoreCase("Tropico LatAm")) {
			return "TROPLATAM" + suffix;
		}
		return null;
	}
}
