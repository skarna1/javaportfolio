package com.stt.portfolio.quotes.portfoliofiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.stt.portfolio.ParseException;
import com.stt.portfolio.Util;
import com.stt.portfolio.quotes.Quote;
import com.stt.portfolio.quotes.QuoteManager;

public class FileQuoteManager implements QuoteManager {

	// last quotes
	Map<String, Quote> quotes = new HashMap<String, Quote>();

	// all quotes
	Map<String, ArrayList<Quote>> allQuotes = new HashMap<String, ArrayList<Quote>>();

	Date currentDate; 
	
	public FileQuoteManager() {
		Calendar c = Calendar.getInstance();
		currentDate = c.getTime();
	}

	@Override
	public void init() {
		readQuotes();
	}

	@Override
	public Quote getQuote(String ticker) {
		if (quotes.containsKey(ticker)) {
			return quotes.get(ticker);
		}

		return null;
	}

	@Override
	public Quote getQuote(String ticker, Date date) {
		//System.out.println("Get quotes for " + ticker + " date: " + date);
		if (Util.compareDates(currentDate, date) == 0) {
			return getQuote(ticker);
		}

		if (!allQuotes.containsKey(ticker)) {
			System.out.println("Reading all quotes for " + ticker);
			readAllQuotes(ticker, "etc/kurssidata/" + ticker + ".csv");
		}
		ArrayList<Quote> quotes = allQuotes.get(ticker);

		Quote q = null;
		if (quotes != null) {

			Quote quote = new Quote();
			quote.setDate(date);
			int i = Collections.binarySearch(quotes, quote);
			//System.out.println(i + " " + quotes.size());
			if (i >= 0) {
				q = quotes.get(i);
				 //System.out.println(date + " " + q.getDate());
			} else {
				i=-i;
				if (i == quotes.size())
					i = i - 1;
				while (i>=0 && i < quotes.size()) {
					q = (Quote) quotes.get(i);
					if (!q.getDate().after(date)) {
						//System.out.println(ticker + " " + quotes.size() + " "
						//		+ date + " " + q.getDate());
						break;
					}
					i = i - 1;
				}
			}
		}
		return q;
	}

	private void readQuotes() {
		String path = "etc/kurssidata/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		QuoteParser parser = new QuoteParser();
		for (int i = 0; i < listOfFiles.length; ++i) {

			if (listOfFiles[i].isFile()) {
				String filename = listOfFiles[i].getName();
				if (filename.endsWith(".csv")) {

					String line = Tail.tail(listOfFiles[i].getAbsolutePath());

					Quote quote;
					try {
						quote = parser.parse(line);

						String ticker = filename.substring(0,
								filename.lastIndexOf('.'));
						quote.setTicker(ticker);

						quotes.put(ticker, quote);

						// System.out.println(ticker + ": " + quote.getLast()
						// + " " + quote.getDate());

					} catch (ParseException e) {
						System.out.println(filename+" has invalid line: "+ line);
						//e.printStackTrace();
					}
				}
			}
		}
	}

	private void readAllQuotes(String ticker, String filename) {
		allQuotes.put(ticker, new ArrayList<Quote>());
		Scanner scanner;
		QuoteParser parser = new QuoteParser();
		try {
			scanner = new Scanner(new File(filename));
			if (scanner.hasNextLine())
				scanner.nextLine();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Quote quote = parser.parse(line);
				allQuotes.get(ticker).add(quote);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String path = "/d/portfolio/kurssidata/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		System.out.println(listOfFiles[1].getName());
		System.out.println(listOfFiles[1].getAbsolutePath());

		String ticker = listOfFiles[1].getName().substring(0,
				listOfFiles[1].getName().lastIndexOf('.'));
		System.out.println(ticker);

	}

}
