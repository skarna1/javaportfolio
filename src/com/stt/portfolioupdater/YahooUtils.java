package com.stt.portfolioupdater;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
		RequestConfig globalConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
		        .setCircularRedirectsAllowed(true)
		        .setConnectTimeout(3000)
		        .setSocketTimeout(5000)
		        .build();
		CloseableHttpClient client = HttpClients.custom()
		        .setDefaultRequestConfig(globalConfig)
		        .build();
	    
		HttpGet httpGet = new HttpGet(url);

		try {
			 CloseableHttpResponse response = client.execute(httpGet);
			//System.out.println("http get: " + url + " status: " + statusCode);
			// Make sure only success code content is returned, else return
			// blank.
			 
			if (response.getStatusLine().getStatusCode() != org.apache.http.HttpStatus.SC_OK) {
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
	

}
