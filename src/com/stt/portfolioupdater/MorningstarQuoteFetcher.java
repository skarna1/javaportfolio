package com.stt.portfolioupdater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

public class MorningstarQuoteFetcher extends HTTPQuoteFetcher {

	public MorningstarQuoteFetcher(String uri, String xpath) {
		super(uri);
		this.uri = uri;
		this.xpath = xpath;
	}
	
	public MorningstarQuoteFetcher() {
		super();
	}
	
	public List<Item> parseHtml() {

		// System.out.println(getUri());
		// System.out.println(getXpath());

		List<Item> items = new ArrayList<>();

		try {
			org.w3c.dom.NodeList nodes = fetchNodes(getUri(), getXpath());
			if (nodes != null) {
				// System.out.println("Nodes: " + nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					org.w3c.dom.Node tr = nodes.item(i);

					Item item = new Item();

					parseRow(item, tr);
					//System.out.println(getName());
					
					item.setName(getName());

					item.setTicker(getTicker(item.getName()));
					// System.out.println(getName() + " "
					// + getTicker(item.getName()));
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

	private void parseRow(Item item, org.w3c.dom.Node tr) {
		org.w3c.dom.NodeList nodes = tr.getChildNodes();
		// System.out.println(nodes.getLength());
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

			// System.out.println(column + " : " + value);
			if (column == 0) { // date
				readDate(item, tdNode);

			} else if (column == 2) {
				try {
					setPrice(item, value);

				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
			}
		}
	}

	protected void setPrice(Item item, String value) {
		//System.out.println(value);
		double rate = 1.0;
		String ccy = value.substring(0, 3);
		value = value.substring(3);
		value = value.replaceAll("\u00A0", "");
		value = value.trim();
		
		//System.out.println("value: " + value);
		//System.out.println("ccy: " + ccy);
		
		if (!ccy.equals("EUR")) {
			KauppalehtiJsonExchangerateFetcher kl = new KauppalehtiJsonExchangerateFetcher();
			rate = kl.getExchangeRate(ccy);
		}
		item.setRate(rate);
		setItemValues(item, value);
	}

	private void readDate(Item item, org.w3c.dom.Node tdNode) {
		org.w3c.dom.Node sibling = tdNode.getNextSibling();
		if (sibling != null) {
			org.w3c.dom.NodeList n = sibling.getChildNodes();
			if (n.getLength() > 0) {
				sibling = n.item(0).getNextSibling();

				String datestring = sibling.getNodeValue().trim();
				try {
					Date date = parseDate(datestring);
					item.setGivenDate(date);
					// System.out.println(date);
				} catch (Exception e) {
				}
			}
		}
	}

	protected void setItemValues(Item item, String value) {
		value = value.replaceAll(",", ".");
		double v = Double.parseDouble(value);
		item.setLast(v);
		item.setHigh(v);
		item.setLow(v);
		item.setDecimals(4);
	}

	private String getTicker(String name) {
		if (name.equalsIgnoreCase("Nordnet superrahasto suomi"))
			return "NONSUPERSUOMI";
		else if (name.equalsIgnoreCase("Nordnet superrahasto ruotsi"))
			return "NONSUPERRUOTSI";
		else if (name.equalsIgnoreCase("Nordnet superrahasto norja"))
			return "NONSUPERNORJA";
		else if (name.equalsIgnoreCase("Nordnet superrahasto tanska"))
			return "NONSUPERTANSKA";
		return "";
	}

	public static void main(String[] args) {
		String uri = "http://www.morningstar.fi/fi/funds/snapshot/snapshot.aspx?id=F00000TH8U";
		
		String xpath = "//div[@id='overviewQuickstatsDiv']/table[@border='0']/tr[2]";
		MorningstarQuoteFetcher fetcher = new MorningstarQuoteFetcher(uri, xpath);
		fetcher.setName("Nordnet superrahasto norja");
		List<Item> items = fetcher.parseHtml();
		//for (Item item : items) {
		//	item.print();
		//}
	}
}
