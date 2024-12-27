package com.stt.portfolioupdater;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



public abstract class HTTPDocumentFetcher {

	String xpath= null;
	String uri = null;
	Charset charset = null;

	public HTTPDocumentFetcher() {
	}

	public HTTPDocumentFetcher(String uri) {
		this.uri = uri;
	}

	protected Elements fetchNodes(String uri, String xpathExpression)
			throws XPathExpressionException {

		try {
			Document doc = Jsoup.connect(uri).get();

			Elements elements = doc.selectXpath(xpathExpression);
			
			elements.forEach(System.out::println);

			return elements;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}

	protected String fetch(String url) {

		HttpClient client = HttpClient.newBuilder()
			      .version(Version.HTTP_2)
			      .followRedirects(Redirect.ALWAYS)
			      .connectTimeout(Duration.ofSeconds(5))
			      .build();

		HttpRequest request = HttpRequest.newBuilder()
			      .uri(URI.create(url)).timeout(Duration.ofMillis(5000))
			      .build();

		try {
			 HttpResponse<String> response =
				      client.send(request, BodyHandlers.ofString());


			System.out.println(response);

			Optional<String> contentType = response.headers().firstValue("Content-Type");
			if (contentType.isPresent()) {
				String contentTypeStr = contentType.get();
				//System.out.println("CONTENT-TYPE: " + contentTypeStr + ";");
				Pattern pattern = Pattern.compile("charset=(.*$)");
				Matcher matcher = pattern.matcher(contentTypeStr);
				if (matcher.find())
				{
				    String charset=matcher.group(1);
					// System.out.println("CHARSET: " + charset + ";");
				    try {
				    	this.charset = Charset.forName(charset);
				    }
				    catch (java.nio.charset.IllegalCharsetNameException ex)
				    {

				    }
				}

			}

			if (response.statusCode() != 200) {
				System.err.println("Method failed: " + response.body());
				return "";
			}

			return response.body();

		}  catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			return "";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Double convertToDouble(String value) {
		value = value.replace(',', '.');
		value = value.replaceAll(" ", "");
		value = value.replaceAll("[^\\d.]", "");
		return Double.parseDouble(value);
	}
}