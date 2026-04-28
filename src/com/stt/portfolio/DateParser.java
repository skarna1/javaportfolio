package com.stt.portfolio;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateParser {
	
	public DateParser() {
	}
	
	public LocalDate parseLocalDate(String dateStr) throws ParseException {
		String[] items = dateStr.split("\\.");
		if (items.length < 3) {
			throw new ParseException("Invalid date string: " + dateStr);
		}
		int d = Integer.parseInt(items[0].trim());
		int m = Integer.parseInt(items[1].trim());
		int y = Integer.parseInt(items[2].trim());
		
		return LocalDate.of(y, m, d);
	}
	
	public LocalDate parseLocalDateDDMMYYYY(String dateStr) throws ParseException {
		if (dateStr.length() < 8) {
			throw new ParseException("Invalid date string: " + dateStr);
		}
		int d = Integer.parseInt(dateStr.substring(0, 2));
		int m = Integer.parseInt(dateStr.substring(2, 4));
		int y = Integer.parseInt(dateStr.substring(4, 8));
		
		return LocalDate.of(y, m, d);
	}
	
	public Date parseDate(String dateStr) throws ParseException {
		return localDateToDate(parseLocalDate(dateStr));
	}
	
	public Date parseDateDDMMYYYY(String dateStr) throws ParseException {
		return localDateToDate(parseLocalDateDDMMYYYY(dateStr));
	}
	
	private Date localDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}