package com.stt.portfolioupdater;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Locale;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

public class YahooUtils {

	public static String charsetName = "ISO-8859-1";
	public static Locale usLocale = new Locale("en", "US");

	public static String readInput(String uri) {
		String input = "";
		try {
			InputStream is = fetch(uri);
			BufferedInputStream bis = new BufferedInputStream(is);

			byte[] contents = new byte[1024];
			int bytesRead=0;
			while((bytesRead = bis.read(contents)) != -1) { 
			    input += new String(contents, 0, bytesRead);
			}
		} catch (SocketTimeoutException e)
		{
			System.err.println("Timeout. Could not read " + uri);
		}
		catch (IOException ioe) {
			System.err.println("Could not read " + uri);
			ioe.printStackTrace();
		}
		return input;
	}
	
	public static InputStream fetch(String url) {
		HttpConnectionManager manager = new SimpleHttpConnectionManager();
		manager.getParams().setConnectionTimeout(7000);
		manager.getParams().setSoTimeout(3000);

		HttpClient client = new HttpClient(manager);
		  
		
		GetMethod httpGet = new GetMethod(url);
		httpGet.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		try {
			client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
		
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

}
