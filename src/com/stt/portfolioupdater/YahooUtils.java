package com.stt.portfolioupdater;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;


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
		
		InputStream in = new ByteArrayInputStream(fetchString(url).getBytes());
		return in;
	}
	
	protected static String fetchString(String url) {
		
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
	

			 
			if (response.statusCode() != 200) {
				System.err.println("Method failed: " + response.body());
				return null;
			}

			return response.body();
			
		}  catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
