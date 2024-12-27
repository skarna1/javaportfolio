package com.stt.portfolioupdater;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SPCurrencyQuoteFetcher extends HTTPDocumentFetcher implements CcyFetcher {
	public static String charsetName = "ISO-8859-1";
	public static Locale usLocale = new Locale("en", "US");
	public static String defaulturi = "https://www.suomenpankki.fi/WebForms/ReportViewerPage.aspx?report=/tilastot/valuuttakurssit/valuuttakurssit_today_fi";
	public static String defaultxpathstr = "(//table[@cols='4'])[last()]/tr[position() > 1]/td[position() > 2]/div/div";
	private String date = null;
	private String uri = defaulturi;
	private String xpathstr = defaultxpathstr;
	
	static Map<String, Double> rates = new HashMap<String, Double>();

	
	public SPCurrencyQuoteFetcher() {
		super();
		this.date = null;
	}
	
	public SPCurrencyQuoteFetcher(String uri, String xpathstr) {
		super(uri);
		this.uri = uri;
		this.xpathstr = xpathstr;
		this.date = null;
	}

	public double getExchangeRate(String ccy) throws Exception {
		if (!rates.containsKey(ccy)) {
			parseHtmlCcy();
		}
		Double r = rates.get(ccy);
		if (r == null) {
			throw new Exception("Rate for " + ccy + " not found");
		}
		return r;
	}
	
	public Map<String, Double> parseHtmlCcy() {

		try {
			Elements nodes = fetchNodes(this.uri, this.xpathstr);
			// System.out.println(nodes.getLength());

			this.date = parseDate(nodes);
			// System.out.println(date);
			for (int i = 1; i < nodes.size(); i += 2) {
				parseCcy(nodes, i, rates);

			}
		} catch (XPathExpressionException e) {

			e.printStackTrace();
		}
		return rates;
	}

	private String parseDate(Elements nodes) {
		Element div = nodes.get(0);
		Element n = div.firstElementChild();
		return n.text();
	}

	private void parseCcy(Elements nodes, int i, Map<String, Double> rates) {
		Element div = nodes.get(i);
		Element n = div.firstElementChild();
		String ccy = n.text();
		div = nodes.get(i + 1);
		n = div.firstElementChild();
		String rate = n.text();
		rates.put(ccy, convertToDouble(rate));
	}

	public static void main(String[] args) {		
		SPCurrencyQuoteFetcher fetcher = new SPCurrencyQuoteFetcher();
		Map<String, Double> rates = fetcher.parseHtmlCcy();

		System.out.println(rates.get("USD"));
	}

	public String getDate() {
		return this.date;
	}
}