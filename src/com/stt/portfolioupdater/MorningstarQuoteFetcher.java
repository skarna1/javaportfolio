package com.stt.portfolioupdater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class MorningstarQuoteFetcher extends HTTPQuoteFetcher {

	public MorningstarQuoteFetcher(String uri, String xpath) {
		super(uri);
		this.uri = uri;
		this.xpath = xpath;
	}

	public MorningstarQuoteFetcher() {
		super();
	}

	@Override
	public List<Item> parseHtml() {

		// System.out.println(getUri());
		// System.out.println(getXpath());

		List<Item> items = new ArrayList<>();

		try {
			Elements nodes = fetchNodes(getUri(), getXpath());
			if (nodes != null) {
				//System.out.println("Nodes: " + nodes.size());
				for (int i = 0; i < nodes.size(); i++) {
					Element tr = nodes.get(i);

					Item item = new Item();

					parseRow(item, tr);
					// System.out.println(getName());

					item.setName(getName());

					if (getTicker() != null) {
						item.setTicker(getTicker());
					}
					else {
						item.setTicker(getTicker(item.getName()));
					}
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

	private void parseRow(Item item, Element tr) {
		Elements nodes = tr.children();
		// System.out.println(nodes.getLength());
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
				TextNode n = (TextNode) tdNode;
				value = n.text().trim();
				value = value.replace(',', '.');
				value = value.replaceAll(" ", "");

			}
		
			// System.out.println(column + " : " + value);
			if (column == 0) { // date
				readDate(item, value);

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
		// System.out.println(value);
		double rate = 1.0;
		String ccy = value.substring(0, 3);
		value = value.substring(3);
		value = value.replaceAll("\u00A0", "");
		value = value.replace(',', '.');
		value = value.trim();

		try {
			if (!ccy.equals("EUR")) {
				CcyFetcher currencyFetcher = CcyFactory.createCcyFetcher();
				rate = currencyFetcher.getExchangeRate(ccy);
				System.out.println("rate: " + rate);
			}
			item.setRate(rate);
			setItemValues(item, value);
		} catch (Exception e) {

		}
	}
	
	void readDate(Item item, String datestring) {
	
		try {
			Date date = parseDate(datestring);
			item.setGivenDate(date);
			// System.out.println(date);
		} catch (Exception e) {
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
		//lastPrice:{value:42.43033,
		String uri = "http://www.morningstar.fi/fi/funds/snapshot/snapshot.aspx?id=F00000TH8U";

		String xpath = "//div[@id='overviewQuickstatsDiv']/table[@border='0']/tbody/tr[2]";
		MorningstarQuoteFetcher fetcher = new MorningstarQuoteFetcher(uri, xpath);
		fetcher.setName("Nordnet superrahasto norja");
		List<Item> items = fetcher.parseHtml();
		for (Item item : items) {
			item.print();
		}
	}
}
