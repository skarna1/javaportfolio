package com.stt.portfolio;

import java.util.Calendar;
import java.util.Date;

public class DateParser {
	

	public DateParser() {
	}
	
	public Date parseDate(String dateStr) throws ParseException {

		String[] items = dateStr.split("\\.");
		if (items.length < 3) {
			throw new ParseException("Invalid date string: " + dateStr);
		}
		String day = items[0].trim();
		String month = items[1].trim();
		String year = items[2].trim();
		int y = Integer.parseInt(year);
		int m = Integer.parseInt(month) - 1;
		int d = Integer.parseInt(day);
		
		Calendar c = Calendar.getInstance();
		c.set(y, m, d);

		return c.getTime();
	}
	
	public Date parseDateDDMMYYYY(String dateStr) throws ParseException {

		if (dateStr.length() < 8) {
			throw new ParseException("Invalid date string: " + dateStr);
		}
		String day = dateStr.substring(0, 2);
		String month = dateStr.substring(2, 4);
		String year = dateStr.substring(4, 8);
		int y = Integer.parseInt(year);
		int m = Integer.parseInt(month) - 1;
		int d = Integer.parseInt(day);
		
		Calendar c = Calendar.getInstance();
		c.set(y, m, d);

		return c.getTime();
	}
}