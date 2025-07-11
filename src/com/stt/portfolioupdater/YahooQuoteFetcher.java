package com.stt.portfolioupdater;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
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

	private String userAgent="Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
	
	public YahooQuoteFetcher() {

	}

	public String getSession() {
		try {
			
			CookieManager manager = ((CookieManager) client.cookieHandler().get());
			CookieStore cookieStore = manager.getCookieStore();
			
			HttpRequest request = HttpRequest.newBuilder()
					.timeout(Duration.ofSeconds(10, 0)).GET()
					.uri(URI.create("https://fc.yahoo.com"))
					.header("User-Agent", this.userAgent)
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
	                .header("Accept-Encoding", "gzip, deflate, br, zstd")
	                .header("Accept-Language", "en-US,en;q=0.9")
	                .header("Priority", "u=0, i")
	                .header("Sec-Ch-Ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"")
	                .header("Sec-Ch-Ua-Mobile", "?0")
	                .header("Sec-Ch-Ua-Platform", "\"macOS\"")
	                .header("Sec-Fetch-Dest", "document")
	                .header("Sec-Fetch-Mode", "navigate")
	                .header("Sec-Fetch-Site", "none")
	                .header("Sec-Fetch-User", "?1")
	                .header("Upgrade-Insecure-Requests", "1")
					
					.version(HttpClient.Version.HTTP_2).build();

			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		
			
			if (response.uri().toString() != "https://fc.yahoo.com") {
				String content = "";
				Pattern p = Pattern.compile("<input type=\"hidden\" name=\"([a-zA-Z]+)\" value=\"(.*)\"");
				Matcher m = p.matcher(response.body());
				
				while (m.find()) {
					String value = m.group(2).replace("&#x3D;", "=");
					content +=  m.group(1) + "=" + URLEncoder.encode(value, "UTF-8") + "&";
				}
				content += "consentUUID=default&agree=agree&agree=agree";
				
				
				p = Pattern.compile("<button type=\"submit\" data-beacon=\"(.*)\" class");
				m = p.matcher(response.body());
				while (m.find()) {
					String value = m.group(1);
					System.out.println("beacon:   " +value);
					URI uri = new URI("https://consent.yahoo.com"+value);
					System.out.println("uri:   " +uri.toString());
				
					System.out.println("POST: " + uri.toString());
					HttpRequest postrequest = HttpRequest.newBuilder().uri(uri)
							
							.POST(HttpRequest.BodyPublishers.ofString("")).build();
					
					System.out.println(postrequest.toString());
					HttpResponse<String> postresponse = client.send(postrequest, BodyHandlers.ofString());
					System.out.println("Response code: " + postresponse.statusCode());
					break;
				}
				
		
				URI uri = new URI(response.uri().toString());
				String sessionId = uri.getQuery();
				System.out.println("SessionId: " + sessionId);
				System.out.println("POST: " + uri.toString());
				HttpRequest postrequest = HttpRequest.newBuilder().uri(uri)
						
						.header("User-Agent", this.userAgent)
						.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.header("Accept-Language", "en-US,en;q=0.8")
					
						.header("Content-Type", "application/x-www-form-urlencoded")
						.header("Origin", "https://consent.yahoo.com")
						.header("Referer", response.uri().toString())
						.header("Accept-Encoding", "gzip, deflate, br, zstd")
						
						.header("Sec-Fetch-Dest", "document")
					    .header("Sec-Fetch-Mode","navigate")
					    .header("Sec-Fetch-Site", "none")
						
					    .header("Sec-Fetch-User","?1")
				
					    .header("Upgrade-Insecure-Requests","1")
						
						.header("TE", "trailers")
						.POST(HttpRequest.BodyPublishers.ofString(content)).build();
				
				System.out.println(postrequest.headers().toString());
				HttpResponse<String> postresponse = client.send(postrequest, BodyHandlers.ofString());
				System.out.println("Response code: " + postresponse.statusCode());
				if (response.statusCode() != 200) {
					System.out.println("Response code: " + postresponse.statusCode());
					//System.out.println(postresponse.body());
					return null;
				}
				URI copyuri = new URI("https://guce.yahoo.com/copyConsent?"+ sessionId);
				
				request = HttpRequest.newBuilder().uri(copyuri)
						
						.header("User-Agent", this.userAgent)
						.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.header("Accept-Language", "en-US,en;q=0.8")
					
						.header("Content-Type", "application/x-www-form-urlencoded")
						.header("Origin", "https://consent.yahoo.com")
						.header("Referer", response.uri().toString())
						.header("Accept-Encoding", "gzip, deflate, br, zstd")
						
						.header("Sec-Fetch-Dest", "document")
					    .header("Sec-Fetch-Mode","navigate")
					    .header("Sec-Fetch-Site", "none")
						
					    .header("Sec-Fetch-User","?1")
				
					    .header("Upgrade-Insecure-Requests","1")
						
						.header("TE", "trailers")
						.GET().build();
				
				System.out.println(request.headers().toString());
				HttpResponse<String> copyresponse = client.send(request, BodyHandlers.ofString());
				System.out.println("Response code: " + copyresponse.statusCode());
				if (response.statusCode() != 200) {
					System.out.println("Response code: " + copyresponse.statusCode());
					System.out.println(copyresponse.body());
					return null;
				}
				
				System.out.println(copyresponse.uri().toString());
				//System.out.println(postresponse.body());
			}
		
		
			System.out.println("Got cookies");
			for (HttpCookie cookie : cookieStore.getCookies()) {
				System.out.println(cookie.getName()+"="+cookie.getValue()+ " "+ "'"+cookie.getDomain()+"'");
				
				
			
			}
		
			
			return "";
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public String getCrumb(String cookie) throws URISyntaxException, IOException, InterruptedException {
		String uri = "https://query1.finance.yahoo.com/v1/test/getcrumb";
		System.out.println(cookie);
	
			HttpRequest request = HttpRequest.newBuilder(new URI(uri))
					.timeout(Duration.ofSeconds(30, 0)).GET()
				.header("User-Agent", this.userAgent)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Priority", "u=0, i")
                .header("Sec-Ch-Ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"")
                .header("Sec-Ch-Ua-Mobile", "?0")
                .header("Sec-Ch-Ua-Platform", "\"macOS\"")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .header("Upgrade-Insecure-Requests", "1")
				.version(HttpClient.Version.HTTP_2)
				.build();

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		System.out.println("Crumb: " + "'"+response.body().strip() + "'");
		System.out.println("Response code: " + response.statusCode());
		return response.body().strip();
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

		String cookie = getSession();
		if (cookie == null) {
			System.out.println("Could not get session cookie");
			return items;
		}
		String crumb;
		try {
			crumb = getCrumb(cookie);
		} catch (URISyntaxException | IOException | InterruptedException e1) {
			System.out.println("Could not get crumb");
			return items;
		}
		if (crumb.equals("Too Many Requests")) {
			System.out.println("Could not get crumb");
			return items;
		}
		int j = 0;
		for (String symbolString : symbollist) {
			 System.out.println(symbolString);

			try {
				String uri = yahoourl + symbolString + "&crumb=" + URLEncoder.encode(crumb, "UTF-8");
				
				System.out.println(uri);

				HttpRequest request = HttpRequest.newBuilder(new URI(uri))
						.timeout(Duration.ofSeconds(20, 0))
						.GET()
						.header("User-Agent", this.userAgent)
						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
		                //.header("Accept-Encoding", "gzip, deflate, br, zstd")
		                .header("Accept-Language", "en-US,en;q=0.9")
		                .header("Priority", "u=0, i")
		                .header("Sec-Ch-Ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"")
		                .header("Sec-Ch-Ua-Mobile", "?0")
		                .header("Sec-Ch-Ua-Platform", "\"macOS\"")
		                .header("Sec-Fetch-Dest", "document")
		                .header("Sec-Fetch-Mode", "navigate")
		                .header("Sec-Fetch-Site", "none")
		                .header("Sec-Fetch-User", "?1")
		                .header("Upgrade-Insecure-Requests", "1")
						.version(HttpClient.Version.HTTP_2)
						.build();

				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				System.out.println("Response code: " + response.statusCode());

				String jsonstr = response.body();
				//System.out.println(jsonstr);
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
		String symbol = symbols[0];
		String ticker = symbols[1];
		
		String receivedTicker=element.get("symbol").getAsString();
		if (!symbol.equals(receivedTicker)) {
			System.out.println("ERROR: symbol mismatch: "+ symbol + " " + receivedTicker);
			return;
		}

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
