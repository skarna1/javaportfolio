package com.stt.portfolioupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class YahooQuoteFetcher extends HTTPQuoteFetcher {

	private String yahoourl = "https://query1.finance.yahoo.com/v7/finance/quote?lang=en-US&region=US&corsDomain=finance.yahoo.com&symbols=";
	private int chunk = 10;

	public YahooQuoteFetcher() {

	}

	@Override
	public List<Item> parseHtml() {

		List<Item> items = new ArrayList<>();

		String[] stocks = xpath.split(";");

		List<String> symbollist = createSymbolList(stocks);

		int j = 0;
		for (String symbolString : symbollist) {
			// System.out.println(symbolString);

			try {
				URL url = new URL(yahoourl + symbolString);
				URLConnection request = url.openConnection();
				request.connect();

				BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
				String inputLine;
				String jsonstr = "";
				while ((inputLine = in.readLine()) != null) {
					jsonstr += inputLine;
				}
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(jsonstr, JsonObject.class);
				JsonArray result = jsonObject.get("quoteResponse").getAsJsonObject().get("result").getAsJsonArray();

				for (int i = j; i < Math.min(j + chunk, stocks.length); i++) {
					parseItem(items, stocks[i], result.get(i - j).getAsJsonObject());
				}
				j += chunk;

			} catch (Exception e) {
				System.out.println("Error: " + symbollist);
				e.printStackTrace();
			}
		}
		return items;
	}

	private void parseItem(List<Item> items, String stock, JsonObject element) throws IOException, Exception {

		String[] symbols = stock.split(",");
		// String symbol = symbols[0];
		String ticker = symbols[1];

		// System.out.println(ticker);

		double rate = 1.0;


		if (element != null && element.keySet().contains("regularMarketPrice")) {

			double last = element.get("regularMarketPrice").getAsDouble();
			double high = element.get("regularMarketDayHigh").getAsDouble();
			double low = element.get("regularMarketDayLow").getAsDouble();
			long volume = element.get("regularMarketVolume").getAsLong();
			String ccy = element.get("currency").getAsString();
			long seconds = element.get("regularMarketTime").getAsLong();
			java.util.Date date = new java.util.Date(seconds * 1000);

			if (!ccy.equals("EUR")) {
				CcyFetcher currencyFetcher = CcyFactory.createCcyFetcher();
				rate = currencyFetcher.getExchangeRate(ccy);
				// System.out.println("rate: " + rate);
			}

			Item item = new Item();
			item.setValues(ticker, last, high, low, volume, date, rate);

			items.add(item);

			// System.out.println(item.getLine());

		}
	}

	private List<String> createSymbolList(String[] stocks) {
		List<String> lists = new ArrayList<String>();

		for (int i = 0; i < stocks.length; i += chunk) {
			String[] stockChunk = Arrays.copyOfRange(stocks, i, Math.min(i + chunk, stocks.length));

			String symbollist = createSymbolString(stockChunk);
			lists.add(symbollist);
		}
		return lists;
	}

	private String createSymbolString(String[] stocks) {
		String symbollist = "";
		for (String stock : stocks) {
			String[] symbols = stock.split(",");
			String symbol = symbols[0].replace(".", "-");
			if (symbollist.length() > 0) {
				symbollist = symbollist + ",";
			}
			symbollist = symbollist + symbol;
		}
		return symbollist;
	}

	public static void main(String[] args) {

		YahooQuoteFetcher fetcher = new YahooQuoteFetcher();
		fetcher.setXpath("MSFT,MSFT,USD;KO,KO,USD");
		List<Item> items = fetcher.parseHtml();
		for (Item item : items) {
			item.print();
		}
	}
}
