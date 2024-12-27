package com.stt.portfolioupdater;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.xpath.XPathExpressionException;



public class ECBCurrencyQuoteFetcher extends HTTPDocumentFetcher implements CcyFetcher {
	public static String charsetName = "ISO-8859-1";
	public static Locale usLocale = new Locale("en", "US");
	public static String defaulturi = "https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html";
	public static String defaultxpathstr = "(//table[@class=\"forextable\"]/*/tr[position()>0]/td[@class=\"currency\"] | //table[@class=\"forextable\"]/*/tr[position()>0]/td/*/span[@class=\"rate\"]) ";
	private Date date = null;
	private String uri = defaulturi;
	private String xpathstr = defaultxpathstr;

	public Map<String, Double> rates = new HashMap<String, Double>();


	public ECBCurrencyQuoteFetcher() {
		super();
		this.date = null;
	}

	public ECBCurrencyQuoteFetcher(String uri, String xpathstr) {
		super(uri);
		this.uri = uri;
		this.xpathstr = xpathstr;
		this.date = null;
	}

	public Date getDate() {
		return this.date;
	}

	@Override
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

	public void parseHtmlCcy() {

		try {
			org.jsoup.select.Elements elements = fetchNodes(this.uri, this.xpathstr);
			//System.out.println("Length: " + elements.size());

			this.date = Calendar.getInstance().getTime();
			// System.out.println(date);

			this.rates = IntStream.range(0, elements.size() / 2).boxed()
				    .collect(Collectors.toMap(i -> elements.get(i * 2).id(),
				    		i -> convertToDouble(elements.get(i * 2 + 1).text())));


		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ECBCurrencyQuoteFetcher fetcher = new ECBCurrencyQuoteFetcher();
		fetcher.parseHtmlCcy();

		System.out.println(fetcher.rates.get("USD"));
	}


}