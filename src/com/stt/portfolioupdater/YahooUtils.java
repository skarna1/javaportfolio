package com.stt.portfolioupdater;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Scanner;

public class YahooUtils {

	public static String charsetName = "ISO-8859-1";
	public static Locale usLocale = new Locale("en", "US");
	
	public static String readInput(String uri) {
		String input = "";
		try {
			URL url = new URL(uri);
			URLConnection site = url.openConnection();

			InputStream is = site.getInputStream();
			Scanner scanner = new Scanner(new BufferedInputStream(is),
					charsetName);

			scanner.useLocale(usLocale);
			input = scanner.useDelimiter("\\A").next();
			scanner.close();
			is.close();
		} catch (SocketTimeoutException e)
		{
			System.err.println("Tiemout. Could not read " + uri);
		}
		catch (IOException ioe) {
			System.err.println("Could not read " + uri);
			ioe.printStackTrace();
		}
		return input;
	}
}
