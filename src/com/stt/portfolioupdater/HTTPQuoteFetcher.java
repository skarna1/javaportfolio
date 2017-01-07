package com.stt.portfolioupdater;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class HTTPQuoteFetcher extends HTTPDocumentFetcher {
	private boolean ignoreVolume = false;
	private String name;

	public abstract List<Item> parseHtml();

	public boolean isIgnoreVolume() {
		return ignoreVolume;
	}

	public void setIgnoreVolume(boolean ignoreVolume) {
		this.ignoreVolume = ignoreVolume;
	}

	protected Date parseDate(String dateStr) throws Exception {

		String[] items = dateStr.split("\\.");
		if (items.length < 3) {
			throw new Exception("Invalid date string: " + dateStr);
		}
		String day = items[0].trim();
		String month = items[1].trim();
		String year = items[2].trim();
		int y = Integer.parseInt(year);
		int m = Integer.parseInt(month) - 1;
		int d = Integer.parseInt(day);
		if (y < 100) {
			y = y + 2000;
		}
		Calendar c = Calendar.getInstance();
		c.set(y, m, d);
		// System.out.println(d);
		// System.out.println(c.getTime());
		return c.getTime();
	}

	protected Date parseYahooDate(String dateStr) throws Exception {

		String[] items = dateStr.split("/");
		if (items.length < 3) {
			throw new Exception("Invalid date string: " + dateStr);
		}
		String day = items[1].trim();
		String month = items[0].trim();
		String year = items[2].trim();
		int y = Integer.parseInt(year);
		int m = Integer.parseInt(month) - 1;
		int d = Integer.parseInt(day);

		Calendar c = Calendar.getInstance();
		c.set(y, m, d);

		return c.getTime();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
