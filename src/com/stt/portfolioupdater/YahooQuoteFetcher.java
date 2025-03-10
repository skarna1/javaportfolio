package com.stt.portfolioupdater;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class YahooQuoteFetcher extends HTTPQuoteFetcher {

	private String yahoourl = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=";
	private int chunk = 10;

	private HttpClient client = null;

	public YahooQuoteFetcher() {

	}

	public HttpCookie getSession() {
		try {

			HttpRequest request = HttpRequest.newBuilder(new URI("https://finance.yahoo.com"))
					.timeout(Duration.ofSeconds(10, 0)).GET()

					.headers("accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
							"Accept-language", "en-US,en;q=0.5", "User-Agent",
							"Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/112.0", "Sec-Fetch-Dest",
							"document", "Sec-Fetch-User", "?1", "Upgrade-Insecure-Requests", "1", "Sec-fetch-mode",
							"navigate", "TE", "trailers")
					.version(HttpClient.Version.HTTP_2).build();

			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				System.out.println("Response code: " + response.statusCode());
				System.out.println(response.body());
				return null;
			}


			if (response.uri().toString() != "https://finance.yahoo.com") {
				String content = "agree=agree";
				Pattern p = Pattern.compile("<input type=\"hidden\" name=\"([a-zA-Z]+)\" value=\"([a-zA-Z-_0-9]+)\"");
				Matcher m = p.matcher(response.body());
				while (m.find()) {
					content += "&" + m.group(1) + "=" + m.group(2);
				}
				content +="&tag=TCF2&step=Layer1-AcceptAll&brandDomain=finance.yahoo.com&brandBid=&userType=nonreg&sdk=false&tos=fi-FI&country=FI";
				System.out.println(content);
				HttpRequest postrequest = HttpRequest.newBuilder().uri(new URI(response.uri().toString()))
						.headers("Content-Type", "application/x-www-form-urlencoded")
						.POST(HttpRequest.BodyPublishers.ofString(content)).build();
				HttpResponse<String> postresponse = client.send(postrequest, BodyHandlers.ofString());

				if (response.statusCode() != 200) {
					System.out.println("Response code: " + postresponse.statusCode());
					System.out.println(postresponse.body());
					return null;
				}
			}
			CookieStore cookieStore = ((CookieManager) client.cookieHandler().get()).getCookieStore();
			
			for (HttpCookie cookie : cookieStore.getCookies()) {
				
					return cookie;
			}
		} catch (Exception e) {

		}
		return null;
	}

	public String getCrumb() throws URISyntaxException, IOException, InterruptedException {
		String uri = "https://query1.finance.yahoo.com/v1/test/getcrumb";

		HttpRequest request = HttpRequest.newBuilder(new URI(uri)).timeout(Duration.ofSeconds(20, 0)).GET()
				.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/112.0")
				.version(HttpClient.Version.HTTP_2).build();

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

		return response.body();

	}

	@Override
	public List<Item> parseHtml() {

		List<Item> items = new ArrayList<>();

		String[] stocks = xpath.split(";");

		List<String> symbollist = createSymbolList(stocks);

		if (client == null) {
			client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
					.cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
			System.out.println("httpclient created");
		}

		HttpCookie cookie = getSession();
		if (cookie == null) {
			System.out.println("Could not get session cookie");
			return items;
		}
		String crumb;
		try {
			crumb = getCrumb();
		} catch (URISyntaxException | IOException | InterruptedException e1) {
			System.out.println("Could not get crumb");
			return items;
		}
		
		int j = 0;
		for (String symbolString : symbollist) {
			 System.out.println(symbolString);

			try {
				String uri = yahoourl + symbolString + "&crumb=" + crumb;

				HttpRequest request = HttpRequest.newBuilder(new URI(uri)).timeout(Duration.ofSeconds(20, 0)).GET()
						.version(HttpClient.Version.HTTP_2).build();

				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				System.out.println("Response code: " + response.statusCode());

				String jsonstr = response.body();
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(jsonstr, JsonObject.class);
				JsonArray result = jsonObject.get("quoteResponse").getAsJsonObject().get("result").getAsJsonArray();
				// System.out.println(result);
				for (int i = j; i < Math.min(j + chunk, stocks.length); i++) {
					parseItem(items, stocks[i], result.get(i - j).getAsJsonObject());
				}
				j += chunk;

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + " " + symbollist);
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
