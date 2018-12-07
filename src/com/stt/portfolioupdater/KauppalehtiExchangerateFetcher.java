package com.stt.portfolioupdater;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

public class KauppalehtiExchangerateFetcher extends HTTPDocumentFetcher {

	static final String uri = "http://www.kauppalehti.fi/5/i/porssi/valuutat/valuutta.jsp?curid=";

	static Map<String, Double> rates = new HashMap<>();

	public double getExchangeRate(String ccy) {
		if (!rates.containsKey(ccy)) {
			double rate = parseHtmlCcy(uri + ccy);
			if (rate > 0.0) {
				rates.put(ccy, rate);
			}
		}
		Double r = rates.get(ccy);
		if (r == null) {
			return 1.0;
		}
		return r;
	}

	private double parseHtmlCcy(String uri) {
		double rate = 0.0;

		try {
			org.w3c.dom.NodeList nodes = fetchNodes(uri,
					"//table[@class='table_stockexchange']/tr[position()=2]/td[position()=2]");
			if (nodes != null) {
				org.w3c.dom.Node td = nodes.item(0);
				if (td != null) {
					org.w3c.dom.NodeList nodes2 = td.getChildNodes();

					org.w3c.dom.Node td2 = nodes2.item(0);
					String value = td2.getNodeValue().trim();
					try {
						return Double.parseDouble(value);
					} catch (NumberFormatException e) {
						System.out.println("Failed to parse " + uri + " value: " + value);
					}
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return rate;
	}
	
	public static void main(String [ ] args) {
		KauppalehtiExchangerateFetcher fetcher = new KauppalehtiExchangerateFetcher();
		double rate = fetcher.getExchangeRate("SEK");
		System.out.println("Rate: " + rate);
		
		rate = fetcher.getExchangeRate("USD");
		System.out.println("Rate: " + rate);
	}
	
}
