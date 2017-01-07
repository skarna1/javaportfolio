package com.stt.portfolioupdater;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
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
		HttpConnectionManager manager = new SimpleHttpConnectionManager();
		manager.getParams().setConnectionTimeout(7000);
		manager.getParams().setSoTimeout(3000);

		HttpClient client = new HttpClient(manager);
		  
		
		GetMethod httpGet = new GetMethod(url);
		httpGet.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		try {

			int statusCode = client.executeMethod(httpGet);
			//System.out.println("http get: " + url + " status: " + statusCode);
			// Make sure only success code content is returned, else return
			// blank.
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + httpGet.getStatusLine());
				httpGet.releaseConnection();
				return null;
			}
			return httpGet.getResponseBodyAsStream();
		} catch (HttpException e) {
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