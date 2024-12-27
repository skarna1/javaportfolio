package com.stt.portfolioupdater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class SeligsonQuoteFetcher extends HTTPQuoteFetcher {

	@Override
	public List<Item> parseHtml() {
		//System.out.println(getUri());
		//System.out.println(getXpath());


		List<Item> items = new ArrayList<>();

		try {
			Elements nodes = fetchNodes(
					getUri(),
					getXpath());
			if (nodes != null) {
				//System.out.println("Nodes: " + nodes.getLength());
				for (int i = 0; i < nodes.size(); i++) {
					Element tr = nodes.get(i);
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

	private void parseRow(Item item, Element tr) {
		Elements nodes = tr.children();
		for (int i = 0; i < nodes.size(); ++i) {
			Element td = nodes.get(i);

			parseColumn(item, i, td);
		}
	}

	private void parseColumn(Item item, int column, Element td) {
		
		List<Node> nodes = td.childNodes();
		for (int i = 0; i < nodes.size(); ++i) {
			Node tdNode = nodes.get(i);
			String value="";
			if (tdNode instanceof TextNode) {
				value = ((TextNode) tdNode).text().trim();
				value = value.replace(',', '.');
				value = value.replaceAll(" ", "");
				value = value.replaceAll("[^\\d.]", "");
				//System.out.println("column " + column + " " + "value " + value);
			}
			if (column == 0) {
				if (tdNode.nodeName().equalsIgnoreCase("a")) {
					
					Node node = tdNode.firstChild();
					if (node instanceof TextNode) {
						TextNode textNode = (TextNode) node;
						String name = textNode.text();
					
						item.setName(name);
						item.setTicker(getTicker(name));
					}
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
		}else if (name.startsWith("Kehittyvät Markkinat")) {
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

	
	public static void main(String[] args) {		
		String uri = "http://www.seligson.fi/suomi/rahastot/FundValues_FI.html";
		String xpath ="//table[@class='rahasto']/tbody/tr[td[@data-label]]";
		SeligsonQuoteFetcher fetcher = new SeligsonQuoteFetcher();
		fetcher.setUri(uri);
		fetcher.setXpath(xpath);
		List<Item> items = fetcher.parseHtml();
		for (Item item : items) {
			item.print();
		}
	}
	
}



