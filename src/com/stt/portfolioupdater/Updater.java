package com.stt.portfolioupdater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class Updater {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String path = "/home/sami/workspace/portfolio/etc/kurssidata/";

		if (args.length >= 1) {

			path = args[0];
		}

		MorningstarQuoteFetcher fetcher = new MorningstarQuoteFetcher();
		String uri = "http://www.morningstar.fi/fi/funds/snapshot/snapshot.aspx?id=F00000TH8W";
		String xpath = "//div[@id='overviewQuickstatsDiv']/table[@border='0']/tr[2]";
		fetcher.setUri(uri);
		fetcher.setXpath(xpath);
		fetcher.setName("Nordnet superrahasto Suomi");
		// List<Item> items = fetcher.parseHtml();

		// Updater updater = new Updater();
		// updater.writeToFiles(path, items);

		// uri="http://www.seligson.fi/suomi/rahastot/rahastoarvot.asp";
		// SeligsonQuoteFetcher fetcher = new SeligsonQuoteFetcher();
		// fetcher.setUri(uri);
		// fetcher.setXpath("//table[@border='0' and @cellspacing='1' and @width='613' and @align='left']/tr");

		List<Item> items = fetcher.parseHtml();
		// updater.writeToFiles(path, items, true);

		System.out.println("number of items: " + items.size());
		for (Item item : items) {
			item.print();
		}

	}

	public void writeToFiles(String path, List<Item> items, boolean ignoreVolume) {

		for (Item item : items) {

			// System.out.println("ticker: "+ item.getTicker());
			// item.print();
			if ((item.getVolume() > 0 || ignoreVolume) && item.getLast() > 0.0) {
				String filename = path + item.getTicker() + ".csv";
				try {

					File file = new File(filename);
					boolean fileCreated = file.createNewFile();
					BufferedWriter out = new BufferedWriter(new FileWriter(
							filename, true));
					if (fileCreated) {
						out.write("D8HLCV\n");
					}
					// System.out.println("filename: "+ filename);
					writeToFile(filename, item, out);
					out.close();
				} catch (IOException e) {
					System.out.println(filename);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// System.out.println("FAIL:" + item.ticker);
				// item.print();
			}
		}
	}

	public void writeToFiles(String path, List<Item> items) {
		writeToFiles(path, items, true);
	}

	public void writeToFile(String filename, Item item, BufferedWriter out)
			throws IOException {
		if (item.getTicker() != null) {
			removeLastLineIfDateExists(item.getDate(), filename);
			out.write(item.getLine());
		}
	}

	public void writeToFile(String filename, Item item) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
		writeToFile(filename, item, out);
		out.close();
	}

	public void removeLastLineIfDateExists(String date, String filename) {

		Vector<String> lines = Tail.tail(filename, 1);

		if (lines != null && lines.size() >= 1) {
			boolean isDatePresent = lines.get(0).startsWith(date);
			if (isDatePresent) {
				Tail.removeLastLine(filename, lines.get(0).length() + 1);
			}

		}

	}

}
