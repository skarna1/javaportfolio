package com.stt.portfolioupdater;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


public abstract class HTTPDocumentFetcher {

	String xpath= null;
	String uri = null;
	
	
	public HTTPDocumentFetcher() {
		super();
	}
	
	protected org.w3c.dom.NodeList fetchNodes(String uri, String xpathExpression)
			throws XPathExpressionException {

		InputStream in = fetch(uri);

		if (in != null) {
			Document dom = tidyHtml(in);
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			XPathExpression expr = xPath.compile(xpathExpression);

			org.w3c.dom.NodeList nodes = (NodeList) expr.evaluate(dom,
					XPathConstants.NODESET);
			return nodes;
		}
		return null;
	}

	protected InputStream fetch(String url) {
		RequestConfig globalConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
		        .setCircularRedirectsAllowed(true)
		        .setConnectTimeout(3000)
		        .setSocketTimeout(5000)
		        .build();
		CloseableHttpClient client = HttpClients.custom()
				.useSystemProperties()
		        .setDefaultRequestConfig(globalConfig)
		        .build();
	    
		HttpGet httpGet = new HttpGet(url);

		try {
			 CloseableHttpResponse response = client.execute(httpGet);
			//System.out.println("http get: " + url + " status: " + statusCode);
			// Make sure only success code content is returned, else return
			// blank.
			 
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + response.getStatusLine());
				httpGet.releaseConnection();
				return null;
			}
			return response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			return null;
		}
	}

	protected Document tidyHtml(InputStream in) {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);

		Document dom = tidy.parseDOM(in, null);
		return dom;
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

}