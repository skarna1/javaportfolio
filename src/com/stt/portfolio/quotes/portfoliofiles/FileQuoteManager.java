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

	private final File quotesDirectory;
	Date currentDate;
	
	public FileQuoteManager() {
		this(new File("etc/kurssidata"));
	}

	FileQuoteManager(File quotesDirectory) {
		this.quotesDirectory = quotesDirectory;
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
				if (i >= quotes.size())
					i = quotes.size()- 1;
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
		File[] files = quotesDirectory.listFiles();
		if (files == null) {
			System.err.println("Unable to read quote directory: " + quotesDirectory);
			return;
		}
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".csv")) {
				readQuotes(file);
			}
		}
	}

	private void readQuotes(File file) {
		String filename = file.getName();
		String ticker = filename.substring(0, filename.lastIndexOf('.'));
		ArrayList<Quote> tickerQuotes = new ArrayList<Quote>();
		QuoteParser parser = new QuoteParser();
		// Some source files begin with a non-quote metadata line.
		boolean firstLine = true;
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				try {
					Quote quote = parser.parse(line);
					quote.setTicker(ticker);
					tickerQuotes.add(quote);
				} catch (ParseException e) {
					if (!firstLine) {
						System.err.println(filename + " has invalid line: " + line);
					}
				}
				firstLine = false;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Unable to read quote file: " + file);
			e.printStackTrace();
		}
		if (!tickerQuotes.isEmpty()) {
			allQuotes.put(ticker, tickerQuotes);
			quotes.put(ticker, tickerQuotes.get(tickerQuotes.size() - 1));
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
